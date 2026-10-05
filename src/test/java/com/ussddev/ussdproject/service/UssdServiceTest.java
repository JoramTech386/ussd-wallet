package com.ussddev.ussdproject.service;

import com.ussddev.ussdproject.model.SessionKey;
import com.ussddev.ussdproject.model.SessionResult;
import com.ussddev.ussdproject.model.UssdSession;
import com.ussddev.ussdproject.model.dto.Inbound;
import com.ussddev.ussdproject.model.dto.OutBound;
import com.ussddev.ussdproject.model.enums.ProcessState;
import com.ussddev.ussdproject.model.enums.SessionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UssdServiceTest {

 @Mock
 private SessionService sessionService;

 @Mock
 private MenuService menuService;

 @Mock
 private BusinessService businessService;

 @InjectMocks
 private UssdService ussdService;

 private UssdSession session;

 @BeforeEach
 void setUp() {
  session = UssdSession.builder()
          .id("699112233:abc123")
          .msisdn("699112233")
          .sessionId("abc123")
          .menuLevel("0")
          .status(SessionStatus.ACTIVE)
          .temporaryData(new HashMap<>())
          .build();
 }

 @Test
 void traiter_quandChoix1_devraitConsulterSoldeEtTerminer() {
  // Étant donné
  Inbound inbound = Inbound.builder()
          .msisdn("699112233")
          .sessionId("abc123")
          .message("1")
          .provider("mtn")
          .build();

  when(sessionService.getOrCreate(any(SessionKey.class)))
          .thenReturn(new SessionResult(session, false));   // ← session existante

  when(businessService.getSolde("699112233"))
          .thenReturn(new BigDecimal("5000"));

  when(menuService.buildMessageSolde(session, new BigDecimal("5000")))
          .thenReturn("Votre solde est de 5000 FCFA");

  // Lorsque
  OutBound result = ussdService.traiter(inbound);

  // Alors
  assertThat(result.getMessage()).isEqualTo("Votre solde est de 5000 FCFA");
  assertThat(result.getCommand()).isEqualTo(ProcessState.END);

  verify(businessService).getSolde("699112233");
  verify(sessionService).save(session);
 }
}