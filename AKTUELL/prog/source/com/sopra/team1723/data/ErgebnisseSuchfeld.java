package com.sopra.team1723.data;

// Dient zur Kapselung der Rückgabedaten der Funktion durchsucheDatenbank() im Datenbankmanager.
// Dabei wird der zum Suchbegriff ähnliche Text in der Variablen text gespeichert. Mit der 
// Variablen Klasse und id weiß man zu welchem Objekt die Variable text gehört
// 
public class ErgebnisseSuchfeld {
    public String text;
    public int id;
    public String klasse;
    
    public ErgebnisseSuchfeld(String text, int id, String klasse)
    {
        super();
        this.text = text;
        this.id = id;
        this.klasse = klasse;
    }
}