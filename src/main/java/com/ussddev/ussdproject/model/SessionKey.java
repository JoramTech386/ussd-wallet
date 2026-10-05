package com.ussddev.ussdproject.model;

public record SessionKey(String msisdn, String sessionId) {

    public String toId() {
        return msisdn + ":" + sessionId;
    }
}
