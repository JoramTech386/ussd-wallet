package com.ussddev.ussdproject.service;

import com.ussddev.ussdproject.model.SessionKey;
import com.ussddev.ussdproject.model.SessionResult;
import com.ussddev.ussdproject.model.UssdSession;
import com.ussddev.ussdproject.model.enums.SessionStatus;
import com.ussddev.ussdproject.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    // On injecte le TTL car @Value ne fonctionne pas en test unitaire pur
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sessionService, "ttlMinutes", 2L);
    }

    @Test
    void getOrCreate_quandSessionInexistante_devraitCreerUneNouvelleSession() {
        // Étant donné
        SessionKey key = new SessionKey("699112233", "abc");

        when(sessionRepository.findById("699112233:abc"))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any(UssdSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Lorsque
        SessionResult result = sessionService.getOrCreate(key);

        // Alors
        assertThat(result.created()).isTrue();
        assertThat(result.session().getMsisdn()).isEqualTo("699112233");
        assertThat(result.session().getSessionId()).isEqualTo("abc");
        assertThat(result.session().getMenuLevel()).isEqualTo("0");
        assertThat(result.session().getStatus()).isEqualTo(SessionStatus.ACTIVE);
        assertThat(result.session().isExpired()).isFalse();

        verify(sessionRepository).save(any(UssdSession.class));
    }

    @Test
    void getOrCreate_quandSessionActiveExistante_devraitRetournerLaSessionSansCreation() {
        // Étant donné
        SessionKey key = new SessionKey("699112233", "abc");
        UssdSession existante = UssdSession.createNew(key, 2L);
        existante.setStatus(SessionStatus.ACTIVE);

        when(sessionRepository.findById("699112233:abc"))
                .thenReturn(Optional.of(existante));

        // Lorsque
        SessionResult result = sessionService.getOrCreate(key);

        // Alors
        assertThat(result.created()).isFalse();
        assertThat(result.session().getId()).isEqualTo("699112233:abc");
        assertThat(result.session().getStatus()).isEqualTo(SessionStatus.ACTIVE);

        // On vérifie que save n'a PAS été appelé
        verify(sessionRepository, never()).save(any(UssdSession.class));
    }
}