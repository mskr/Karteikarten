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
    
    public BenachrEinlModerator(String inhalt, Calendar erstelldatum, int benutzer,
            Veranstaltung veranstaltung){
        this.inhalt = inhalt;
        this.erstelldaum = erstelldatum;
        this.benutzer = benutzer;
        this.veranstaltung = veranstaltung;
        this.id = -1;
        this.gelesen = false;
        this.angenommen = false;
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