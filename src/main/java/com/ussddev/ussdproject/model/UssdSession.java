package com.ussddev.ussdproject.model;

import com.ussddev.ussdproject.model.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "ussd_sessions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UssdSession {

    @Id
    private String id;

    private String msisdn;

    private String sessionId;

    private String menuLevel;

    private LocalDateTime createdAt;

    private LocalDateTime expirationDate;

    @Builder.Default
    private Map<String, Object> temporaryData = new HashMap<>();

    /**
     * Vérifie si la session est expirée.
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }

    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    /**
     * Crée une nouvelle session à partir d'une SessionKey.
     */
    public static UssdSession createNew(SessionKey key, long ttlMinutes) {
        LocalDateTime now = LocalDateTime.now();
        return UssdSession.builder()
                .id(key.toId())
                .msisdn(key.msisdn())
                .sessionId(key.sessionId())
                .menuLevel("0")
                .status(SessionStatus.ACTIVE)
                .createdAt(now)
                .expirationDate(now.plusMinutes(ttlMinutes))
                .temporaryData(new HashMap<>())
                .build();
    }




}