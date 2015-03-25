package com.sopra.team1723.ctrl;

import java.util.List;

import org.json.simple.*;

import com.sopra.team1723.data.Benutzer;
import com.sopra.team1723.data.ErgebnisseSuchfeld;

public class JSONConverter
{
    /**
     * JSON-Feld-Werte
     */
    
    static private final String jsonErrorTxt = "error";
    static public final String jsonErrorNoError = "noerror";                        // Kein Fehler

    // Alle bekannten Fehlertypen
    static public final String jsonErrorSystemError = "systemerror";                // Ein Interner Fehler ist aufgetreten
    static public final String jsonErrorInvalidParam = "invalidparam";              // Allgemeiner Fehler. Die Übergebene Parameter sind unbekannt oder es fehlen Parameter
    static public final String jsonErrorNotLoggedIn = "notloggedin";                // Der Benutzer ist nicht eingeloggt und hat deshalb nicht die benötigen Rechte
    static public final String jsonErrorLoginFailed = "loginfailed";                // Login ist fehlgeschlagen. Email existiert nicht oder Passwort ist falsch.  
    static public final String jsonErrorEmailAlreadyInUse = "emailalreadyinuse";    // Fehler beim Registieren. Email-Adresse schon vergeben
    static public final String jsonErrorPwResetFailed = "pwresetfailed";            // Fehler beim Zurücksetzen des Passworts
    static public final String jsonErrorSessionExpired = "sessionexpired";          // Fehler beim Zurücksetzen des Passworts

    // Benutzer 
    static public final String jsonEmail = "email";
    static public final String jsonVorname = "vorname";
    static public final String jsonNachname = "nachname";
    static public final String jsonMatrikelNr = "matrikelnr";
    static public final String jsonNutzerstatus = "nutzerstatus";
    static public final String jsonStudiengang = "studiengang";
    static public final String jsonPasswort = "pass";
    static public final String jsonNotifyVeranstAenderung = "notifyVeranstAenderung";
    static public final String jsonNotifyKarteikartenAenderung = "notifyKarteikartenAenderung";
    static public final String jsonNotifyKommentare = "notifyKommentare";
    static public final String jsonProfilBildPfad = "profilBildPfad";
    
    // ArrayResults
    static public final String jsonArrResult = "arrResult";
    static public final String jsonStrResult = "strResult";
       
    
    /**
     * Einfache Bestätigung von Aktionen oder Error
     * @param erroText 
     * @return
     */
    static JSONObject toJsonError(String erroText)
    {
        JSONObject jo = new JSONObject();
        
        jo.put(jsonErrorTxt, erroText);
       
        return jo;
    }
    
    /**
     * Verpackt die Daten eines Benutzer Objekts in
     * ein JSON Objekt und gibt dieses zurueck.
     * Das Passwort wird aus Sicherheitsgruenden 
     * nicht in das JSON Objekt gepackt.
     * @param benutzer
     * @return JSONObject mit den Benutzerdaten
     */
    static JSONObject toJson(Benutzer benutzer, boolean full) 
    {
        JSONObject jo = new JSONObject();
        
        jo.put(jsonErrorTxt, jsonErrorNoError);
        jo.put(jsonEmail, benutzer.geteMail());
        jo.put(jsonVorname, benutzer.getVorname());
        jo.put(jsonNachname, benutzer.getNachname());
        jo.put(jsonMatrikelNr, new Integer(benutzer.getMatrikelnummer()).toString());
        jo.put(jsonNutzerstatus, benutzer.getNutzerstatus().name());
        jo.put(jsonStudiengang, benutzer.getStudiengang());
        jo.put(jsonProfilBildPfad, benutzer.getProfilBildPfad());
        
        if(full)
        {
            jo.put(jsonNotifyVeranstAenderung, benutzer.isNotifyVeranstAenderung());
            jo.put(jsonNotifyKarteikartenAenderung, benutzer.isNotifyKarteikartenAenderung());
            jo.put(jsonNotifyKommentare, benutzer.getNotifyKommentare().name());
        }
        return jo;
    }
    /**
     * 
     * @param strings
     * @return JSONObject mit einer Liste von Strings
     */
    static JSONObject toJson(List<String> strings) 
    {
        JSONObject jo = new JSONObject();
        
        jo.put(jsonErrorTxt, jsonErrorNoError);
        JSONArray array = new JSONArray();
        
        for(String s: strings)
            array.add(s);
        
        jo.put(jsonArrResult, array);
        
        return jo;
    }
    
    static JSONObject toJsonSuchfeld(List<ErgebnisseSuchfeld> suchtreffer) 
    {
        JSONObject jo = new JSONObject();
        
        jo.put(jsonErrorTxt, jsonErrorNoError);
        JSONArray array = new JSONArray();
        
        for(ErgebnisseSuchfeld ergs: suchtreffer)
            array.add(ergs.text);
        
        jo.put(jsonArrResult, array);
        
        return jo;
    }
    
    /**
     * 
     * @param strings
     * @return JSONObject mit NoError und einem String
     */
    static JSONObject toJson(String txt) 
    {
        JSONObject jo = new JSONObject();  
        jo.put(jsonErrorTxt, jsonErrorNoError);      
        jo.put(jsonStrResult, txt);
        
        return jo;
    }
}
