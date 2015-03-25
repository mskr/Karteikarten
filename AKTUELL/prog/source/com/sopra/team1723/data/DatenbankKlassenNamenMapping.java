package com.sopra.team1723.data;

import java.util.HashMap;
import java.util.Map;

public class DatenbankKlassenNamenMapping
{
    public static final Map<Klassenfeld, Klassenfeld> matching = new HashMap<Klassenfeld, Klassenfeld>();
    static{
        // Mapped die Bezeichnungen der Klassen und Attribute auf die entsprechenden Bezeichnungen der
        // Tabellen und Spalten in der Datenbank
        matching.put(new Klassenfeld("Veranstaltung", "titel"), new Klassenfeld("veranstaltung","Titel"));
        matching.put(new Klassenfeld("Benutzer", "vorname"), new Klassenfeld("benutzer","Vorname"));
        matching.put(new Klassenfeld("Benutzer", "nachname"), new Klassenfeld("benutzer","Nachname"));
    }
    
    public static final Map<String, String> matchTabelleKlasse = new HashMap<String, String>();
    static{
        // Mapped einen Tabellennamen aus der Datenbank auf den entsprechenden Klassennamen
        matchTabelleKlasse.put("veranstaltung", "Veranstaltung");
        matchTabelleKlasse.put("benutzer","Benutzer");
    }
    
}