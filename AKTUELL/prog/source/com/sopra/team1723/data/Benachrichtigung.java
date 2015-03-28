package com.sopra.team1723.data;

import java.util.*;

/**
 * 
 */
public abstract class Benachrichtigung {

    /**
     * 
     */
    public Benachrichtigung() {
    }

    /**
     * 
     */
    protected int id;

    /**
     * 
     */
    protected String inhalt;

    public int getId()
    {
        return id;
    }

    public String getInhalt()
    {
        return inhalt;
    }

    public Calendar getErstelldaum()
    {
        return erstelldaum;
    }

    public int getBenutzer()
    {
        return benutzer;
    }

    public boolean isGelesen()
    {
        return gelesen;
    }

    /**
     * 
     */
    protected Calendar erstelldaum;

    /**
     * Benutzer zu dem diese Benachrichtigung gehört
     */
    protected int benutzer;

    /**
     * 
     */
    protected boolean gelesen;

    public Benachrichtigung(int id, String inhalt, Calendar erstelldaum, int benutzer, boolean gelesen)
    {
        this.id = id;
        this.inhalt = inhalt;
        this.erstelldaum = erstelldaum;
        this.benutzer = benutzer;
        this.gelesen = gelesen;
    }
    
    

}