package com.sopra.team1723.data;

import java.util.*;

/**
 * 
 */
public abstract class Karteikarte {

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
    private Set<AttributTyp> attribute;

    /**
     * 
     */
    private KarteikartenTyp typ;

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

    public Set<AttributTyp> getAttribute()
    {
        return attribute;
    }

    public void setAttribute(Set<AttributTyp> attribute)
    {
        this.attribute = attribute;
    }

    public KarteikartenTyp getTyp()
    {
        return typ;
    }

    public void setTyp(KarteikartenTyp typ)
    {
        this.typ = typ;
    }

    
}