package com.sopra.team1723.data;

import java.util.*;

/**
 * 
 */
public class Notiz {    
    
    public Notiz(int id, String inhalt, int ersteller, int karteikarte)
    {
        super();
        this.id = id;
        this.inhalt = inhalt;
        this.ersteller = ersteller;
        this.karteikarte = karteikarte;
    }
    
    

    public Notiz(String inhalt, int ersteller, int karteikarte)
    {
        super();
        this.id = -1;
        this.inhalt = inhalt;
        this.ersteller = ersteller;
        this.karteikarte = karteikarte;
    }



    /**
     * 
     */
    public Notiz() {
    }
    
    

    public int getId()
    {
        return id;
    }

    public String getInhalt()
    {
        return inhalt;
    }

    public int getErsteller()
    {
        return ersteller;
    }

    public int getKarteikarte()
    {
        return karteikarte;
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
    private int ersteller;

    /**
     * 
     */
    private int karteikarte;

}