package com.sopra.team1723.ctrl;

import java.util.List;

import org.json.simple.*;

import com.sopra.team1723.data.Benutzer;
import com.sopra.team1723.data.ErgebnisseSuchfeld;

public class JSONConverter
{
    
    /**
     * Einfache Bestätigung von Aktionen oder Error
     * @param erroText 
     * @return
     */
    static JSONObject toJsonError(String erroText)
    {
        JSONObject jo = new JSONObject();
        
        jo.put(ParamDefines.jsonErrorTxt, erroText);
       
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
        
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);
        jo.put(ParamDefines.Email, benutzer.geteMail());
        jo.put(ParamDefines.Vorname, benutzer.getVorname());
        jo.put(ParamDefines.Nachname, benutzer.getNachname());
        jo.put(ParamDefines.MatrikelNr, new Integer(benutzer.getMatrikelnummer()).toString());
        jo.put(ParamDefines.Nutzerstatus, benutzer.getNutzerstatus().name());
        jo.put(ParamDefines.Studiengang, benutzer.getStudiengang());
        jo.put(ParamDefines.ProfilBildPfad, benutzer.getProfilBildPfad());
        
        if(full)
        {
            jo.put(ParamDefines.NotifyVeranstAenderung, benutzer.isNotifyVeranstAenderung());
            jo.put(ParamDefines.NotifyKarteikartenAenderung, benutzer.isNotifyKarteikartenAenderung());
            jo.put(ParamDefines.NotifyKommentare, benutzer.getNotifyKommentare().name());
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
        
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);
        JSONArray array = new JSONArray();
        
        for(String s: strings)
            array.add(s);
        
        jo.put(ParamDefines.jsonArrResult, array);
        
        return jo;
    }
    
    /**
     * Von den Ergebnissen der Suche, welche in der Klasse
     * ErgebnisseSuchfeld gekapselt sind, werden an die GUI
     * nur die ähnlichen Texte gegeben
     * @return JSONObject mit einer Liste von Strings
     */
    static JSONObject toJsonSuchfeld(List<ErgebnisseSuchfeld> suchtreffer) 
    {
        JSONObject jo = new JSONObject();
        
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);
        JSONArray array = new JSONArray();
        
        for(ErgebnisseSuchfeld ergs: suchtreffer)
            array.add(ergs.text);
        
        jo.put(ParamDefines.jsonArrSuchfeldResult, array);
        
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
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);      
        jo.put(ParamDefines.jsonStrResult, txt);
        
        return jo;
    }
}
