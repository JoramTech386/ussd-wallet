package com.ussddev.ussdproject.model.enums;

import java.math.BigDecimal;

public enum ForfaitPackage {


    MO_500("1", "500 Mo", new BigDecimal("500")),
    GO_1  ("2", "1 Go",   new BigDecimal("1000")),
    GO_2  ("3", "2 Go",   new BigDecimal("1800"));

    private final String code;
    private final String label;
    private final BigDecimal price;

    ForfaitPackage(String code, String label, BigDecimal price) {
        this.code = code;
        this.label = label;
        this.price = price;
    }

    public String code()  { return code; }
    public String label() { return label; }
    public BigDecimal price() { return price; }

    /**
     * Trouve un forfait par son code ("1", "2", "3").
     * Retourne null si le code est inconnu.
     */
    public static ForfaitPackage fromCode(String code) {
        for (ForfaitPackage p : values()) {
            if (p.code.equals(code)) return p;
        }
        return null;
    }
}
