package com.sopra.team1723.data;

import java.util.*;

/**
 * 
 */
public class Kommentar {

    /**
     * 
     */
    public Kommentar() {
    }

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String inhalt;

    /**
     * 
     */
    private Date erstelldatum;

    /**
     * 
     */
    private int erstellerID;

    /**
     * 
     */
    private int vaterID;

    /**
     * 
     */
    private boolean unterhaltungBeendet;

    /**
     * 
     */
    private int karteikartenID;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getInhalt()
    {
        return inhalt;
    }

    public void setInhalt(String inhalt)
    {
        this.inhalt = inhalt;
    }

    public Date getErstelldatum()
    {
        return erstelldatum;
    }

    public void setErstelldatum(Date erstelldatum)
    {
        this.erstelldatum = erstelldatum;
    }

    public int getErstellerID()
    {
        return erstellerID;
    }

    public void setErstellerID(int erstellerID)
    {
        this.erstellerID = erstellerID;
    }

    public int getVaterID()
    {
        return vaterID;
    }

    public void setVaterID(int vaterID)
    {
        this.vaterID = vaterID;
    }

    public boolean isUnterhaltungBeendet()
    {
        return unterhaltungBeendet;
    }

    public void setUnterhaltungBeendet(boolean unterhaltungBeendet)
    {
        this.unterhaltungBeendet = unterhaltungBeendet;
    }

    public int getKarteikartenID()
    {
        return karteikartenID;
    }

    public void setKarteikartenID(int karteikartenID)
    {
        this.karteikartenID = karteikartenID;
    }
    

}