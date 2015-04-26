package com.sopra.team1723.data;

import java.util.Calendar;


public class BenachrVeranstAenderung extends Benachrichtigung{
    private Veranstaltung veranstaltung;
    
    public BenachrVeranstAenderung(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            Veranstaltung veranstaltung)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.veranstaltung = veranstaltung;
    }
    
    public BenachrVeranstAenderung(String inhalt, Calendar erstelldatum, Veranstaltung veranstaltung){
        this.inhalt = inhalt;
        this.erstelldaum = erstelldatum;
        this.veranstaltung = veranstaltung;
        this.id = -1;
        this.benutzer = -1;
        this.gelesen = false;
    }
    

    public Veranstaltung getVeranstaltung()
    {
        return veranstaltung;
    }
}