package com.sopra.team1723.data;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class BenachrKarteikAenderung extends Benachrichtigung{
    private Karteikarte karteikarte;
    
    public BenachrKarteikAenderung(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            Karteikarte karteikarte)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.karteikarte = karteikarte;
    }

    public BenachrKarteikAenderung(Karteikarte karteikarte, int bearbeiter)
    {
        this.karteikarte = karteikarte;
        this.inhalt = "Die Karteikarte " + karteikarte.getTitel() + " wurde bearbeitet.";
        this.erstelldaum = new GregorianCalendar();
        erstelldaum.setTimeInMillis(System.currentTimeMillis());
        this.id = -1;
        // hier nicht ein Benutzer, der die Benachrichtigung bekommt, sondern der, der sie verusacht hat
        this.benutzer = bearbeiter;
        this.gelesen = false;
    }
    
    public Karteikarte getKarteikarte()
    {
        return karteikarte;
    }
}