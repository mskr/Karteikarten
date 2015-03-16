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
    private int id;

    /**
     * 
     */
    private String inhalt;

    /**
     * 
     */
    private Date erstelldaum;

    /**
     * 
     */
    private int verweisID;

    /**
     * 
     */
    private BenachrichtigungsTyp verweisTyp;

}