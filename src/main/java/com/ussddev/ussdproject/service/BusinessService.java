package com.ussddev.ussdproject.service;

import com.ussddev.ussdproject.core.CoreInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessService {

    private final CoreInterface coreInterface;

    /**
     * Récupère le solde d'un utilisateur.
     */
    public BigDecimal getSolde(String msisdn) {
        log.info("getSolde msisdn={}", msisdn);
        return coreInterface.getSolde(msisdn);
    }

    /**
     * Effectue un transfert d'argent.
     * Retourne true si succès, false sinon.
     */
    public boolean effectuerTransfert(String msisdn, String destinataire, BigDecimal montant) {
        log.info("effectuerTransfert from={} to={} montant={}", msisdn, destinataire, montant);
        return coreInterface.transferer(msisdn, destinataire, montant);
    }

    /**
     * Achète un forfait.
     */
    public boolean acheterForfait(String msisdn, String codeForfait) {
        log.info("acheterForfait msisdn={} code={}", msisdn, codeForfait);
        return coreInterface.acheterForfait(msisdn, codeForfait);
    }

    /**
     * Change la langue préférée d'un utilisateur.
     */
    public boolean changeLangue(String msisdn, String langue) {
        log.info("changeLangue msisdn={} langue={}", msisdn, langue);
        return coreInterface.changeLangue(msisdn, langue);
    }

    public String getLangue(String msisdn) {
        log.info("getLangue msisdn={}", msisdn);
        return coreInterface.getLangue(msisdn);
    }
}
