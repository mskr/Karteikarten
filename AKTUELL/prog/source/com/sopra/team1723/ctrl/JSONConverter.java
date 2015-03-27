package com.sopra.team1723.ctrl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.*;

import com.sopra.team1723.data.Benutzer;
import com.sopra.team1723.data.ErgebnisseSuchfeld;
import com.sopra.team1723.data.Veranstaltung;

@SuppressWarnings("unchecked")
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
        jo.put(ParamDefines.Id, benutzer.getId());
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
        
        for(ErgebnisseSuchfeld ergs: suchtreffer){
            JSONObject j = new JSONObject();
            j.put(ParamDefines.SuchfeldErgText, ergs.text);
            j.put(ParamDefines.SuchfeldErgKlasse, ergs.klasse);
            j.put(ParamDefines.SuchfeldErgId, ergs.id);
            array.add(j);
        }
        
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
    
    /**
     * 
     * @param strings
     * @return JSONObject mit NoError und einem String
     */
    static JSONObject toJsonVeranstList(List<Veranstaltung> veranstList, List<Boolean> angemeldet) 
    {
        JSONObject jo = new JSONObject();  
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);
        
        JSONArray array = new JSONArray();

        Iterator<Boolean> it = angemeldet.iterator();
        for(Veranstaltung v: veranstList)
        {
            JSONObject o = new JSONObject();

            o.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);      
            o.put(ParamDefines.Titel, v.getTitel());   
            o.put(ParamDefines.Id, v.getId()); 
            o.put(ParamDefines.Beschr, v.getBeschreibung()); 
            o.put(ParamDefines.Semester, v.getSemester()); 
            o.put(ParamDefines.BewertungenErlauben, v.isBewertungenErlaubt()); 
            o.put(ParamDefines.ModeratorKkBearbeiten, v.isModeratorKarteikartenBearbeiten()); 
            o.put(ParamDefines.KommentareErlauben,v.isKommentareErlaubt());
            o.put(ParamDefines.Ersteller,JSONConverter.toJson(v.getErsteller(),true));
            o.put(ParamDefines.AnzTeilnehmer, v.getAnzTeilnehmer());
            o.put(ParamDefines.Angemeldet, it.next());
            
            array.add(o);
        }

        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }
    
    /**
     * 
     * @param strings
     * @return JSONObject mit NoError und einem String
     */
    static JSONObject toJson(Veranstaltung veranst) 
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);    
        jo.put(ParamDefines.Id, veranst.getId());   
        jo.put(ParamDefines.Titel, veranst.getTitel()); 
        jo.put(ParamDefines.Beschr, veranst.getBeschreibung()); 
        jo.put(ParamDefines.Semester, veranst.getSemester()); 
        jo.put(ParamDefines.BewertungenErlauben, veranst.isBewertungenErlaubt()); 
        jo.put(ParamDefines.ModeratorKkBearbeiten, veranst.isModeratorKarteikartenBearbeiten()); 
        jo.put(ParamDefines.KommentareErlauben,veranst.isKommentareErlaubt());
        jo.put(ParamDefines.Ersteller,JSONConverter.toJson(veranst.getErsteller(),true));
        
        
        return jo;
    }
}
