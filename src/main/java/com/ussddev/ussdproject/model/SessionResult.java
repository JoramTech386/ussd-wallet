package com.ussddev.ussdproject.model;

/**
 * Résultat de getOrCreate : la session + un drapeau indiquant
 * si elle vient d'être créée (nouvelle ou expirée).
 */
public record SessionResult(UssdSession session, boolean created) {
}
