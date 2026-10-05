package com.ussddev.ussdproject.service;

import com.ussddev.ussdproject.model.SessionKey;
import com.ussddev.ussdproject.model.SessionResult;
import com.ussddev.ussdproject.model.UssdSession;
import com.ussddev.ussdproject.model.dto.Inbound;
import com.ussddev.ussdproject.model.dto.OutBound;
import com.ussddev.ussdproject.model.enums.ForfaitPackage;
import com.ussddev.ussdproject.model.enums.ProcessState;
import com.ussddev.ussdproject.model.enums.SessionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UssdService {

    private final SessionService sessionService;
    private final MenuService menuService;
    private final BusinessService businessService;

    /**
     * Point d'entrée principal : traite une requête USSD.
     */
    public OutBound traiter(Inbound inbound) {
        log.info("Traitement USSD msisdn={} message={}",
                inbound.getMsisdn(), inbound.getMessage());

        // 1. Construire la clé et récupérer/créer la session
        SessionKey key = new SessionKey(inbound.getMsisdn(), inbound.getSessionId());
        SessionResult result = sessionService.getOrCreate(key);
        UssdSession session = result.session();

        // 2. Charger la langue si nouvelle session
        if (result.created()) {
            String languePreferee = businessService.getLangue(inbound.getMsisdn());
            session.getTemporaryData().put("language", languePreferee);
            log.info("Langue chargée pour la session: {}", languePreferee);

            return OutBound.continueWith(menuService.buildMenuPrincipal(session));
        }

        // 3. Suite : traitement normal
        String message = inbound.getMessage() == null ? "" : inbound.getMessage().trim();

        OutBound response;
        if (message.isEmpty()) {
            response = afficherMenu(session);
        } else {
            response = traiterSaisie(session, message);
        }

        // 4. Marquer TERMINATED si la réponse termine la session
        if (response.getCommand() == ProcessState.END) {
            session.setStatus(SessionStatus.TERMINATED);
            log.info("Session marquée TERMINATED. key={}", session.getId());
        }

        // 5. Sauvegarder
        sessionService.save(session);

        return response;
    }

    /**
     * Affiche le menu correspondant au menuLevel courant.
     * Utilisé quand l'utilisateur envoie une requête vide (ex: première entrée).
     */
    private OutBound afficherMenu(UssdSession session) {
        return switch (session.getMenuLevel()) {
            case "0" -> OutBound.continueWith(menuService.buildMenuPrincipal(session));
            default  -> OutBound.endWith(menuService.buildMenuInconnu(session));
        };
    }

    /**
     * Traite une saisie utilisateur selon le menuLevel courant.
     */
    private OutBound traiterSaisie(UssdSession session, String saisie) {
        return switch (session.getMenuLevel()) {
            case "0"  -> traiterMenuPrincipal(session, saisie);
            case "01" -> traiterConsultationSolde(session, saisie);
            case "02"  -> traiterMenuTransfert(session, saisie);
            case "021" -> traiterMenuMontant(session, saisie);
            case "022" -> traiterMenuConfirmation(session, saisie);
            case "03"          -> traiterMenuForfait(session, saisie);
            case "031", "032", "033" -> traiterConfirmationForfait(session, saisie);
            case "04"  -> traiterMenuLangue(session, saisie);
            default   -> OutBound.endWith(menuService.buildMenuInconnu(session));
        };
    }

    /**
     * Traite la saisie dans le menu principal.
     */
    private OutBound traiterMenuPrincipal(UssdSession session, String saisie) {
        switch (saisie) {
            case "1":
                // Nouveau flux : on vide les données temporaires
                session.getTemporaryData().clear();
                session.setMenuLevel("01");
                return executerConsultationSolde(session);

            case "2":
                session.getTemporaryData().clear();
                session.setMenuLevel("02");
                return OutBound.continueWith(menuService.buildMenuTransfert(session));

            case "3":
                session.getTemporaryData().clear();
                session.setMenuLevel("03");
                return OutBound.continueWith(menuService.buildMenuForfait(session));

            case "4":
                session.getTemporaryData().clear();
                session.setMenuLevel("04");
                return OutBound.continueWith(menuService.buildMenuLangue(session));

            case "5":
                session.setMenuLevel("-99");
                return OutBound.endWith(menuService.buildMenuAuRevoir(session));

            default:
                // Saisie invalide : on reste dans le menu principal
                return OutBound.continueWith(menuService.buildMenuErreur(session, "invalidChoice"));
        }
    }

    /**
     * Traite la saisie dans le menu "01" (consultation solde).
     * Note : en réalité, ce menu exécute l'action immédiatement après le choix "1".
     */
    private OutBound traiterConsultationSolde(UssdSession session, String saisie) {
        // Ici, on pourrait gérer "0" pour retour, etc.
        // Pour l'instant, on considère que la consultation se fait à l'entrée.
        return OutBound.endWith(menuService.buildMenuInconnu(session));
    }

    /**
     * Exécute la consultation du solde.
     */
    private OutBound executerConsultationSolde(UssdSession session) {
        BigDecimal solde = businessService.getSolde(session.getMsisdn());
        String message = menuService.buildMessageSolde(session, solde);
        return OutBound.endWith(message);
    }

    /**
     * Traite la saisie du numéro du destinataire (menuLevel = "02").
     * Valide le numéro, stocke-le, et passe à la saisie du montant.
     */
    private OutBound traiterMenuTransfert(UssdSession session, String saisie) {
        // Validation : 9 chiffres commençant par 6
        if (!saisie.matches("^6\\d{8}$")) {
            return OutBound.continueWith(menuService.buildInvalidRecipient(session));
        }

        session.getTemporaryData().put("recipient", saisie);
        session.setMenuLevel("021");
        return OutBound.continueWith(menuService.buildMenuMontant(session));
    }

    /**
     * Traite la saisie du montant (menuLevel = "021").
     * Valide le montant, vérifie le solde, et passe à la confirmation.
     */
    private OutBound traiterMenuMontant(UssdSession session, String saisie) {
        BigDecimal montant;
        try {
            montant = new BigDecimal(saisie);
        } catch (NumberFormatException e) {
            return OutBound.continueWith(menuService.buildInvalidAmount(session));
        }

        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            return OutBound.continueWith(menuService.buildInvalidAmount(session));
        }

        BigDecimal solde = businessService.getSolde(session.getMsisdn());
        if (montant.compareTo(solde) > 0) {
            return OutBound.continueWith(menuService.buildInsufficientBalance(session));
        }

        session.getTemporaryData().put("amount", montant.toPlainString());
        session.setMenuLevel("022");

        String recipient = (String) session.getTemporaryData().get("recipient");
        return OutBound.continueWith(
                menuService.buildMenuConfirmTransfert(session, recipient, montant));
    }

    /**
     * Traite la confirmation (menuLevel = "022").
     * "1" → exécute le transfert. "2" → annule.
     */
    private OutBound traiterMenuConfirmation(UssdSession session, String saisie) {
        switch (saisie) {
            case "1":
                String recipient = (String) session.getTemporaryData().get("recipient");
                String montantStr = (String) session.getTemporaryData().get("amount");
                BigDecimal montant = new BigDecimal(montantStr);

                boolean succes = businessService.effectuerTransfert(
                        session.getMsisdn(), recipient, montant);

                if (succes) {
                    return OutBound.endWith(menuService.buildTransfertSuccess(session));
                } else {
                    return OutBound.endWith(menuService.buildTransfertFailed(session));
                }

            case "2":
                session.getTemporaryData().clear();
                session.setMenuLevel("0");
                String menuPrincipal = menuService.buildTransfertCancelled(session)
                        + "\n\n" + menuService.buildMenuPrincipal(session);
                return OutBound.continueWith(menuPrincipal);

            default:
                return OutBound.continueWith(
                        menuService.buildMenuConfirmTransfert(
                                session,
                                (String) session.getTemporaryData().get("recipient"),
                                new BigDecimal((String) session.getTemporaryData().get("amount"))
                        ));
        }
    }

    /**
     * Traite le choix d'un forfait (menuLevel = "03").
     */
    private OutBound traiterMenuForfait(UssdSession session, String saisie) {
        ForfaitPackage forfait = ForfaitPackage.fromCode(saisie);
        if (forfait == null) {
            // Choix invalide : on réaffiche le menu des forfaits
            return OutBound.continueWith(menuService.buildMenuErreur(session, "invalidChoice")
                    + "\n\n" + menuService.buildMenuForfait(session));
        }

        session.setMenuLevel("03" + saisie);   // "031", "032" ou "033"
        return OutBound.continueWith(menuService.buildMenuConfirmForfait(session, forfait));
    }

    /**
     * Traite la confirmation d'achat de forfait (menuLevel = "031", "032" ou "033").
     */
    private OutBound traiterConfirmationForfait(UssdSession session, String saisie) {
        // Extraire le code forfait depuis le menuLevel ("031" → "1", "032" → "2", etc.)
        String code = session.getMenuLevel().substring(2);   // "1", "2" ou "3"
        ForfaitPackage forfait = ForfaitPackage.fromCode(code);

        switch (saisie) {
            case "1":
                boolean succes = businessService.acheterForfait(session.getMsisdn(), forfait.code());
                if (succes) {
                    return OutBound.endWith(menuService.buildForfaitSuccess(session, forfait));
                } else {
                    return OutBound.endWith(menuService.buildForfaitFailed(session));
                }

            case "2":
                session.getTemporaryData().clear();
                session.setMenuLevel("0");
                String message = menuService.buildForfaitCancelled(session)
                        + "\n\n" + menuService.buildMenuPrincipal(session);
                return OutBound.continueWith(message);

            default:
                return OutBound.continueWith(
                        menuService.buildMenuErreur(session, "invalidChoice")
                                + "\n\n" + menuService.buildMenuConfirmForfait(session, forfait));
        }
    }

    /**
     * Traite le choix de langue (menuLevel = "04").
     * "1" → français. "2" → anglais.
     * Persiste le choix côté core, puis termine la session.
     */
    private OutBound traiterMenuLangue(UssdSession session, String saisie) {
        String langue;
        switch (saisie) {
            case "1": langue = "fr"; break;
            case "2": langue = "en"; break;
            default:
                return OutBound.continueWith(
                        menuService.buildMenuErreur(session, "invalidChoice")
                                + "\n\n" + menuService.buildMenuLangue(session));
        }

        boolean succes = businessService.changeLangue(session.getMsisdn(), langue);
        if (!succes) {
            return OutBound.endWith(menuService.buildLangueFailed(session));
        }

        // Mettre à jour la langue de la session AVANT de construire le message,
        // pour que la confirmation soit dans la nouvelle langue.
        session.getTemporaryData().put("language", langue);

        return OutBound.endWith(menuService.buildLangueSuccess(session));
    }
}
