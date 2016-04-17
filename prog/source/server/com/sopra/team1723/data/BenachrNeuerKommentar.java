package com.sopra.team1723.data;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class BenachrNeuerKommentar extends Benachrichtigung{
    private int kommentarID;
    private Karteikarte karteikarte;
    
    public BenachrNeuerKommentar(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen,
            int kommentarID, Karteikarte karteikarte)
    {
        super(id, inhalt, erstelldaum, benutzer, gelesen);
        this.kommentarID = kommentarID;
        this.karteikarte = karteikarte;
    }

    
    public BenachrNeuerKommentar(int benutzer, Karteikarte k, Veranstaltung v, int kommentarID)
    {
        super(-1, "Es wurde eine neuer Kommentar in der Veranstaltung \""+ v.getTitel() +"\" zur Karteikarte \""+k.getTitel()+"\" verfasst.", new GregorianCalendar(), benutzer, false);
        this.kommentarID = kommentarID;
        this.karteikarte = k;
    }

    
    public int getKommentarId()
    {
        return kommentarID;
    }


    public Karteikarte getKarteikarte()
    {
        return karteikarte;
    }
    
    
}