package com.sopra.team1723.data;

import java.util.Calendar;

public class BenachrProfilGeaendert extends Benachrichtigung{
    // Speichert welcher Admin das Profil geändert hat. Höchstwahrscheinlich braucht man diese Info nicht
    private Benutzer admin;
    public BenachrProfilGeaendert(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            Benutzer admin)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.admin = admin;
    }
    public Benutzer getAdmin()
    {
        return admin;
    }
}