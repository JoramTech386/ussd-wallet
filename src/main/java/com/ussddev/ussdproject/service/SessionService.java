package com.ussddev.ussdproject.service;

import com.ussddev.ussdproject.model.SessionKey;
import com.ussddev.ussdproject.model.SessionResult;
import com.ussddev.ussdproject.model.UssdSession;
import com.ussddev.ussdproject.model.enums.SessionStatus;
import com.ussddev.ussdproject.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Value("${ussd.session.ttl-minutes:2}")
    private long ttlMinutes;

    /**
     * Retrouve la session active pour une clé donnée.
     * Si elle n'existe pas ou est expirée, en crée une nouvelle.
     */
    public SessionResult getOrCreate(SessionKey key) {
        String id = key.toId();
        Optional<UssdSession> existing = sessionRepository.findById(id);

        log.info("findById key={} present={} expired={} status={}",
                id, existing.isPresent(),
                existing.map(UssdSession::isExpired).orElse(false),
                existing.map(UssdSession::getStatus).orElse(null));

        // Une session n'est valide que si elle est présente, non expirée, ET ACTIVE.
        boolean isValid = existing.isPresent()
                && !existing.get().isExpired()
                && existing.get().getStatus() == SessionStatus.ACTIVE;

        if (isValid) {
            log.debug("Session found and active for key={}", id);
            return new SessionResult(existing.get(), false);
        }

        if (existing.isPresent()) {
            log.info("Session invalid (expired or terminated), deleting. key={}", id);
            sessionRepository.deleteById(id);
        }

        log.info("Creating new session for msisdn={}, sessionId={}",
                key.msisdn(), key.sessionId());

        UssdSession session = UssdSession.createNew(key, ttlMinutes);
        UssdSession saved = sessionRepository.save(session);
        return new SessionResult(saved, true);
    }

    /**
     * Sauvegarde la session.
     */
    public UssdSession save(UssdSession session) {
        return sessionRepository.save(session);
    }
}