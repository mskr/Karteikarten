package com.sopra.team1723.data;

import java.util.Calendar;


public class BenachrEinlModerator extends Benachrichtigung{
    private Veranstaltung veranstaltung;
    private Boolean angenommen;
    public BenachrEinlModerator(int id, String inhalt, Calendar erstelldaum, int benutzer,
            boolean gelesen, Veranstaltung veranstaltung, boolean angenommen)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.veranstaltung = veranstaltung;
        this.angenommen = angenommen;
    }
    
    public BenachrEinlModerator(int benutzer,  Veranstaltung veranstaltung){
        this.inhalt = "Sie werden zur Veranstaltung " + veranstaltung.getTitel() + " als "
                + "Moderator eingeladen";
        this.erstelldaum = Calendar.getInstance();
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