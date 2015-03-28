package com.sopra.team1723.data;

import java.util.Calendar;


public class BenachrNeuerKommentar extends Benachrichtigung{
    private Kommentar kommentar;
    
    public BenachrNeuerKommentar(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            Kommentar kommentar)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.kommentar = kommentar;
    }

    public Kommentar getKommentar()
    {
        return kommentar;
    }
}