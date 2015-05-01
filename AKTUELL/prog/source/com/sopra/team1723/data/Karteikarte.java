package com.sopra.team1723.data;

import java.util.*;

/**
 * 
 */
public class Karteikarte {

    
    

    public Karteikarte(String titel,  String inhalt, KarteikartenTyp typ, int veranstaltung)
    {
        super();
        this.id = -1;
        this.titel = titel;
        this.aenderungsdatum = Calendar.getInstance();
        this.inhalt = inhalt;
        this.typ = typ;
        this.veranstaltung = veranstaltung;
        this.bewertung = 0;
    }
    
    

    public Karteikarte(int id, String titel, Calendar aenderungsdatum, String inhalt, KarteikartenTyp typ,
            int veranstaltung, int bewertung)
    {
        super();
        this.id = id;
        this.titel = titel;
        this.aenderungsdatum = aenderungsdatum;
        this.inhalt = inhalt;
        this.typ = typ;
        this.veranstaltung = veranstaltung;
        this.bewertung = bewertung;
    }



    /**
     * 
     */
    public Karteikarte() {
    }

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String titel;

    /**
     * 
     */
    private Calendar aenderungsdatum;

    /**
     * 
     */
    private String inhalt;

    /**
     * 
     */
    private KarteikartenTyp typ;
    
    
    
    private int veranstaltung;
    
    private int bewertung;
        
        

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitel()
    {
        return titel;
    }

    public void setTitel(String titel)
    {
        this.titel = titel;
    }

    public Calendar getAenderungsdatum()
    {
        return aenderungsdatum;
    }

    public void setAenderungsdatum(Calendar aenderungsdatum)
    {
        this.aenderungsdatum = aenderungsdatum;
    }

    public String getInhalt()
    {
        return inhalt;
    }

    public void setInhalt(String inhalt)
    {
        this.inhalt = inhalt;
    }


    public KarteikartenTyp getTyp()
    {
        return typ;
    }

    public void setTyp(KarteikartenTyp typ)
    {
        this.typ = typ;
    }

    public int getVeranstaltung()
    {
        return veranstaltung;
    }

    public void setVeranstaltung(int veranstaltung)
    {
        this.veranstaltung = veranstaltung;
    }

    public int getBewertung()
    {
        return bewertung;
    }

    public void setBewertung(int bewertung)
    {
        bewertung = bewertung;
    }
    
    

    
}