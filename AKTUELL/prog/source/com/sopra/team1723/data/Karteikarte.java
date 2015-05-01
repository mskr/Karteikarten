package com.sopra.team1723.data;

import java.util.*;

/**
 * 
 */
public class Karteikarte {

    
    

    public Karteikarte(int id, String titel, Date aenderungsdatum, String inhalt, KarteikartenTyp typ, int veranstaltung)
    {
        super();
        this.id = id;
        this.titel = titel;
        this.aenderungsdatum = aenderungsdatum;
        this.inhalt = inhalt;
        this.typ = typ;
        this.veranstaltung = veranstaltung;
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
    private Date aenderungsdatum;

    /**
     * 
     */
    private String inhalt;

    /**
     * 
     */
    private KarteikartenTyp typ;
    
    
    
    private int veranstaltung;
        
        

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

    public Date getAenderungsdatum()
    {
        return aenderungsdatum;
    }

    public void setAenderungsdatum(Date aenderungsdatum)
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
    

    
}