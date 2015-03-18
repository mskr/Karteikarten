package com.sopra.team1723.data;

/**
 * 
 */
public enum Nutzerstatus {
    STUDENT,
    DOZENT,
    ADMIN;

    /**
     * Liefert den Wert des Enums als String in Kleinbuchstaben.
     */
    @Override
    public String toString() {
        switch(this) {
            case STUDENT: return "student";
            case DOZENT: return "dozent";
            case ADMIN: return "admin";
            default: return "nicht_belegt";
        }
    }
}