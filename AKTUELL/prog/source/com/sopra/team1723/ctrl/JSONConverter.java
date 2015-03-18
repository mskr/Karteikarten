package com.sopra.team1723.ctrl;

import org.json.simple.*;

import com.sopra.team1723.data.Benutzer;

public class JSONConverter
{
    /**
     * JSON-Feld-Werte
     */
    
    static private final String jsonErrorTxt = "error";
    // Alle bekannten Fehlertypen
    static public final String jsonErrorNoError = "noerror";                        // Kein Fehler
    static public final String jsonErrorSystemError = "systemerror";                // Ein Interner Fehler ist aufgetreten
    static public final String jsonErrorInvalidParam = "invalidparam";              // Allgemeiner Fehler. Die Übergebene Parameter sind unbekannt oder es fehlen Parameter
    static public final String jsonErrorNotLoggedIn = "notloggedin";                // Der Benutzer ist nicht eingeloggt und hat deshalb nicht die benötigen Rechte
    static public final String jsonErrorLoginFailed = "loginfailed";                // Login ist fehlgeschlagen. Email existiert nicht oder Passwort ist falsch.  
    static public final String jsonErrorRegisterFailed = "registerfailed";          // Allgemeiner Fehler beim Registreren
    static public final String jsonErrorEmailAlreadyInUse = "emailalreadyinuse";    // Fehler beim Registieren. Email-Adresse schon vergeben
    static public final String jsonErrorPwResetFailed = "pwresetfailed";            // Fehler beim Zurücksetzen des Passworts
     
    
    /**
     * Einfache Bestätigung von Aktionen oder Error
     * @param erroText 
     * @return
     */
    static JSONObject toJsonError(String erroText)
    {
        JSONObject jo = new JSONObject();
        
        // TODO HashMap parametrisieren?
        jo.put(jsonErrorTxt, erroText);
       
        return jo;
    }
    
    /**
     * Verpackt die Daten eines Benutzer Objekts in
     * ein JSON Objekt und gibt dieses zurueck.
     * Das Passwort wird aus Sicherheitsgruenden 
     * nicht in das JSON Objekt gepackt.
     * Die Reihenfolge der Daten darf nicht veraendert werden,
     * da der Benutzer beim Client sonst falsch gelesen wird!
     * @param benutzer
     * @return JSONObject mit den Benutzerdaten
     */
    static JSONObject toJsonBenutzer(Benutzer benutzer) 
    {
        JSONObject jo = new JSONObject();
        
        // TODO HashMap parametrisieren?
        jo.put(jsonErrorTxt, jsonErrorNoError);
        jo.put("email", benutzer.geteMail());
        jo.put("vorname", benutzer.getVorname());
        jo.put("nachname", benutzer.getNachname());
        jo.put("matrikelnr", new Integer(benutzer.getMatrikelnummer()).toString());
        jo.put("nutzerstatus", benutzer.getNutzerstatus().toString());
        jo.put("studiengang", benutzer.getStudiengang());
        
        return jo;
    }
}
