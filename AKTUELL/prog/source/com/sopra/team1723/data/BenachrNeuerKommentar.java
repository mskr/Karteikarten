package com.sopra.team1723.data;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class BenachrNeuerKommentar extends Benachrichtigung{
    private int kommentarID;
    
    public BenachrNeuerKommentar(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            int kommentarID)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.kommentarID = kommentarID;
    }

    
    public BenachrNeuerKommentar(int benutzer, Karteikarte k, int kommentarID)
    {
        super(-1, "Es wurde eine neuer Kommentar zur Karteikarte \""+k.getTitel()+"\" verfasst.", new GregorianCalendar(), benutzer, false);
        this.kommentarID = kommentarID;
    }

    
    public int getKommentarId()
    {
        return kommentarID;
    }
}