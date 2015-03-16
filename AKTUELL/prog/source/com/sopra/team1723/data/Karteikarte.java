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

}