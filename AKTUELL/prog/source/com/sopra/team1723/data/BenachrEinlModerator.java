package com.sopra.team1723.data;

import java.util.Calendar;


public class BenachrEinlModerator extends Benachrichtigung{
    private Veranstaltung veranstaltung;
    private boolean angenommen;
    public BenachrEinlModerator(int id, String inhalt, Calendar erstelldaum, int benutzer,
            boolean gelesen, Veranstaltung veranstaltung, boolean angenommen)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.veranstaltung = veranstaltung;
        this.angenommen = angenommen;
    }
    public Veranstaltung getVeranstaltung()
    {
        return veranstaltung;
    }
    public boolean isAngenommen()
    {
        return angenommen;
    }
    
    
}