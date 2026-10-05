package com.ussddev.ussdproject.core.impl;

import com.ussddev.ussdproject.core.CoreInterface;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CoreInterfaceStub implements CoreInterface {

    private final Map<String, String> langueParMsisdn = new ConcurrentHashMap<>();

    @Override
    public BigDecimal getSolde(String msisdn) {
        return new BigDecimal("5000");
    }

    @Override
    public boolean transferer(String msisdn, String destinataire, BigDecimal montant) {
        return true;
    }

    @Override
    public boolean acheterForfait(String msisdn, String codeForfait) {
        return true;
    }

    @Override
    public boolean changeLangue(String msisdn, String langue) {
        langueParMsisdn.put(msisdn, langue);
        return true;
    }

    @Override
    public String getLangue(String msisdn) {
        return langueParMsisdn.getOrDefault(msisdn, "fr");   // défaut : français
    }
}
