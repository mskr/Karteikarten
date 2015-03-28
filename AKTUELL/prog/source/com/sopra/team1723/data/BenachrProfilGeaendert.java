package com.sopra.team1723.data;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class BenachrProfilGeaendert extends Benachrichtigung{
    // Speichert welcher Admin das Profil geändert hat. Höchstwahrscheinlich braucht man diese Info nicht
    private Benutzer admin;
    public BenachrProfilGeaendert(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            Benutzer admin)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.admin = admin;
    }
    public BenachrProfilGeaendert(GregorianCalendar erstelldaum, int benutzer, Benutzer admin)
    {
       this.id = -1;
       this.inhalt = "";
       this.erstelldaum = erstelldaum;
       this.benutzer = benutzer;
       this.admin = admin;
       this.gelesen = false;
    }
    public Benutzer getAdmin()
    {
        return admin;
    }
}