package com.sopra.team1723.data;

import java.util.HashMap;
import java.util.Map;

public class DatenbankKlassenNamenMapping
{
    public static final Map<Klassenfeld, Klassenfeld> matching = new HashMap<Klassenfeld, Klassenfeld>();
    static{
        matching.put(new Klassenfeld("Veranstaltung", "titel"), new Klassenfeld("veranstaltung","Titel"));
        matching.put(new Klassenfeld("Benutzer", "vorname"), new Klassenfeld("benutzer","Vorname"));
        matching.put(new Klassenfeld("Benutzer", "nachname"), new Klassenfeld("benutzer","Nachname"));
    }
    
}