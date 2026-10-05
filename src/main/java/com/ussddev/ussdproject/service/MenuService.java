package com.ussddev.ussdproject.service;

import com.ussddev.ussdproject.model.UssdSession;
import com.ussddev.ussdproject.model.enums.ForfaitPackage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MessageService messageService;

    public String buildMenuPrincipal(UssdSession session) {
        return messageService.getMessage("mainMenu", getLanguage(session));
    }

    public String buildMenuInconnu(UssdSession session) {
        return messageService.getMessage("unknownMenu", getLanguage(session));
    }

    public String buildMenuErreur(UssdSession session, String errorCode) {
        return messageService.getMessage(errorCode, getLanguage(session));
    }

    public String buildSessionExpired(UssdSession session) {
        return messageService.getMessage("sessionExpired", getLanguage(session));
    }

    public String buildMenuAuRevoir(UssdSession session) {
        return messageService.getMessage("auRevoir", getLanguage(session));
    }

    public String buildMenuTransfert(UssdSession session) {
        return messageService.getMessage("transfertMenu", getLanguage(session));
    }

    public String buildMenuForfait(UssdSession session) {
        return messageService.getMessage("forfaitMenu", getLanguage(session));
    }

    public String buildMenuLangue(UssdSession session) {
        return messageService.getMessage("langueMenu", getLanguage(session));
    }

    public String buildMessageSolde(UssdSession session, BigDecimal solde) {
        String template = messageService.getMessage("solde", getLanguage(session));
        return String.format(template, solde.toPlainString());
    }

    private String getLanguage(UssdSession session) {
        Object lang = session.getTemporaryData().get("language");
        return lang != null ? lang.toString() : "fr";
    }


    public String buildInvalidRecipient(UssdSession session) {
        return messageService.getMessage("invalidRecipient", getLanguage(session));
    }

    public String buildMenuMontant(UssdSession session) {
        return messageService.getMessage("transfertAmount", getLanguage(session));
    }

    public String buildInvalidAmount(UssdSession session) {
        return messageService.getMessage("invalidAmount", getLanguage(session));
    }

    public String buildInsufficientBalance(UssdSession session) {
        return messageService.getMessage("insufficientBalance", getLanguage(session));
    }

    public String buildMenuConfirmTransfert(UssdSession session, String recipient, BigDecimal amount) {
        String template = messageService.getMessage("transfertConfirm", getLanguage(session));
        return String.format(template, amount.toPlainString(), recipient);
    }

    public String buildTransfertSuccess(UssdSession session) {
        return messageService.getMessage("transfertSuccess", getLanguage(session));
    }

    public String buildTransfertFailed(UssdSession session) {
        return messageService.getMessage("transfertFailed", getLanguage(session));
    }

    public String buildTransfertCancelled(UssdSession session) {
        return messageService.getMessage("transfertCancelled", getLanguage(session));
    }


    public String buildMenuConfirmForfait(UssdSession session, ForfaitPackage forfait) {
        String template = messageService.getMessage("forfaitConfirm", getLanguage(session));
        return String.format(template, forfait.label(), forfait.price().toPlainString());
    }

    public String buildForfaitSuccess(UssdSession session, ForfaitPackage forfait) {
        String template = messageService.getMessage("forfaitSuccess", getLanguage(session));
        return String.format(template, forfait.label());
    }

    public String buildForfaitFailed(UssdSession session) {
        return messageService.getMessage("forfaitFailed", getLanguage(session));
    }

    public String buildForfaitCancelled(UssdSession session) {
        return messageService.getMessage("forfaitCancelled", getLanguage(session));
    }

    public String buildLangueSuccess(UssdSession session) {
        return messageService.getMessage("langueSuccess", getLanguage(session));
    }

    public String buildLangueFailed(UssdSession session) {
        return messageService.getMessage("langueFailed", getLanguage(session));
    }
}

