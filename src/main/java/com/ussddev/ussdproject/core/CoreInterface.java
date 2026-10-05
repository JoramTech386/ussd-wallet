package com.ussddev.ussdproject.core;

import java.math.BigDecimal;

public interface CoreInterface {

    BigDecimal getSolde(String msisdn);

    boolean transferer(String msisdn, String destinataire, BigDecimal montant);

    boolean acheterForfait(String msisdn, String codeForfait);

    boolean changeLangue(String msisdn, String langue);

    String getLangue(String msisdn);
}
