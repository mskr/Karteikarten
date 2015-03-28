package com.sopra.team1723.data;

import java.util.Calendar;


public class BenachrKarteikAenderung extends Benachrichtigung{
    private Karteikarte karteikarte;
    
    public BenachrKarteikAenderung(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            Karteikarte karteikarte)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.karteikarte = karteikarte;
    }

    public Karteikarte getKarteikarte()
    {
        return karteikarte;
    }
}