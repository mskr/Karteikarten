package com.sopra.team1723.data;

import java.util.Calendar;


public class BenachrNeuerKommentar extends Benachrichtigung{
    private int kommentarID;
    
    public BenachrNeuerKommentar(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            int kommentarID)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.kommentarID = kommentarID;
    }

    public int getKommentarId()
    {
        return kommentarID;
    }
}