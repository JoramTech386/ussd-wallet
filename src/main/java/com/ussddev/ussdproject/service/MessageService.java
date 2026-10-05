package com.ussddev.ussdproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MessageService {

    private static final Map<String, Map<String, String>> MESSAGES = new HashMap<>();

    static {
        // Français
        Map<String, String> fr = new HashMap<>();
        fr.put("mainMenu", "Bienvenue\n1. Consulter mon solde\n2. Transférer\n3. Forfait\n4. Langue\n5. Quitter");
        fr.put("solde", "Votre solde est de %s FCFA");
        fr.put("auRevoir", "Merci, à bientôt.");
        fr.put("invalidChoice", "Choix invalide. Veuillez réessayer.");
        fr.put("unknownMenu", "Menu inconnu.");
        fr.put("sessionExpired", "Votre session a expiré. Veuillez recomposer le code.");
        fr.put("transfertMenu", "Entrez le numéro du destinataire:");
        fr.put("forfaitMenu", "Choisissez un forfait:\n1. 500 Mo\n2. 1 Go\n3. 2 Go");
        fr.put("langueMenu", "Choisissez la langue:\n1. Français\n2. English");

        fr.put("transfertMenu", "Entrez le numéro du destinataire (9 chiffres commençant par 6):");
        fr.put("invalidRecipient", "Numéro invalide. Réessayez:");
        fr.put("transfertAmount", "Entrez le montant à transférer:");
        fr.put("invalidAmount", "Montant invalide. Réessayez:");
        fr.put("insufficientBalance", "Solde insuffisant. Réessayez:");
        fr.put("transfertConfirm", "Confirmez le transfert de %s FCFA vers %s:\n1. Confirmer\n2. Annuler");
        fr.put("transfertSuccess", "Transfert réussi.");
        fr.put("transfertFailed", "Transfert échoué. Veuillez réessayer.");
        fr.put("transfertCancelled", "Transfert annulé.");

        fr.put("forfaitMenu", "Choisissez un forfait:\n1. 500 Mo - 500 FCFA\n2. 1 Go - 1000 FCFA\n3. 2 Go - 1800 FCFA");
        fr.put("forfaitConfirm", "Confirmez l'achat de %s pour %s FCFA:\n1. Confirmer\n2. Annuler");
        fr.put("forfaitSuccess", "Forfait %s acheté avec succès.");
        fr.put("forfaitFailed", "Achat échoué. Veuillez réessayer.");
        fr.put("forfaitCancelled", "Achat annulé.");

        fr.put("langueSuccess", "Langue changée en Français.");
        fr.put("langueFailed", "Échec du changement de langue. Veuillez réessayer.");

        // Anglais
        Map<String, String> en = new HashMap<>();
        en.put("mainMenu", "Welcome\n1. Check balance\n2. Transfer\n3. Package\n4. Language\n5. Quit");
        en.put("solde", "Your balance is %s FCFA");
        en.put("auRevoir", "Thank you, goodbye.");
        en.put("invalidChoice", "Invalid choice. Please try again.");
        en.put("unknownMenu", "Unknown menu.");
        en.put("sessionExpired", "Your session has expired. Please dial again.");
        en.put("transfertMenu", "Enter recipient number:");
        en.put("forfaitMenu", "Choose a package:\n1. 500 MB\n2. 1 GB\n3. 2 GB");
        en.put("langueMenu", "Choose language:\n1. Français\n2. English");

        en.put("transfertMenu", "Enter recipient number (9 digits starting with 6):");
        en.put("invalidRecipient", "Invalid number. Try again:");
        en.put("transfertAmount", "Enter amount to transfer:");
        en.put("invalidAmount", "Invalid amount. Try again:");
        en.put("insufficientBalance", "Insufficient balance. Try again:");
        en.put("transfertConfirm", "Confirm transfer of %s FCFA to %s:\n1. Confirm\n2. Cancel");
        en.put("transfertSuccess", "Transfer successful.");
        en.put("transfertFailed", "Transfer failed. Please try again.");
        en.put("transfertCancelled", "Transfer cancelled.");

        en.put("forfaitMenu", "Choose a package:\n1. 500 MB - 500 FCFA\n2. 1 GB - 1000 FCFA\n3. 2 GB - 1800 FCFA");
        en.put("forfaitConfirm", "Confirm purchase of %s for %s FCFA:\n1. Confirm\n2. Cancel");
        en.put("forfaitSuccess", "Package %s purchased successfully.");
        en.put("forfaitFailed", "Purchase failed. Please try again.");
        en.put("forfaitCancelled", "Purchase cancelled.");

        en.put("langueSuccess", "Language changed to English.");
        en.put("langueFailed", "Failed to change language. Please try again.");

        MESSAGES.put("fr", fr);
        MESSAGES.put("en", en);
    }

    /**
     * Récupère un message par code et langue.
     */
    public String getMessage(String code, String language) {
        Map<String, String> langMap = MESSAGES.getOrDefault(language, MESSAGES.get("fr"));
        String message = langMap.get(code);
        if (message == null) {
            log.warn("Message introuvable: code={}, langue={}", code, language);
            return "Message introuvable: " + code;
        }
        return message;
    }
}
