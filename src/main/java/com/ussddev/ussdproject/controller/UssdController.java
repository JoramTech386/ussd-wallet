package com.ussddev.ussdproject.controller;

import com.ussddev.ussdproject.model.dto.Inbound;
import com.ussddev.ussdproject.model.dto.OutBound;
import com.ussddev.ussdproject.service.UssdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/ussd")
@RequiredArgsConstructor
public class UssdController {

    private final UssdService ussdService;

    @PostMapping
    public OutBound handleUssd(@RequestBody Inbound inbound) {
        log.info("Requête USSD reçue: msisdn={}, provider={}",
                inbound.getMsisdn(), inbound.getProvider());

        try {
            return ussdService.traiter(inbound);
        } catch (Exception e) {
            log.error("Erreur lors du traitement USSD", e);
            return OutBound.endWith("Une erreur est survenue. Veuillez réessayer.");
        }
    }
}
