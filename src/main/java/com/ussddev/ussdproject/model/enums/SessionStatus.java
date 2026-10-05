package com.ussddev.ussdproject.model.enums;
/**
 * Statut d'une session USSD.
 *
 * ACTIVE : la session est en cours d'utilisation.
 * TERMINATED : l'utilisateur a quitté, ou l'action a mis fin à la session.
 *
 * Toute session non-ACTIVE est traitée comme "à recréer" par SessionService.
 */
public enum SessionStatus {
    ACTIVE,
    TERMINATED
}
