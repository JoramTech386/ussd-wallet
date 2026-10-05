package com.ussddev.ussdproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inbound {

    private String msisdn;

    private String sessionId;

    private String message;

    private String provider;

    /**
     * Clé composite utilisée pour identifier la session.
     */
    public String getKey(){
        return msisdn + ":"  + sessionId;
    }
}
