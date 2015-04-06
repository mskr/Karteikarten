package com.sopra.team1723.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.*;

import com.sopra.team1723.data.BenachrEinlModerator;
import com.sopra.team1723.data.BenachrKarteikAenderung;
import com.sopra.team1723.data.BenachrNeuerKommentar;
import com.sopra.team1723.data.BenachrProfilGeaendert;
import com.sopra.team1723.data.BenachrVeranstAenderung;
import com.sopra.team1723.data.Benachrichtigung;
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
            
            boolean zugangsPasswortGesetzt;
            if (v.getZugangspasswort() == null)
                zugangsPasswortGesetzt = false;
            else
                zugangsPasswortGesetzt = true;
                
            o.put(ParamDefines.KennwortGesetzt, zugangsPasswortGesetzt);
            
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
    
    /**
     * 
     * @param strings
     * @return JSONObject mit NoError und einem String
     */
    static JSONObject toJsonBenachrichtigungen(List<Benachrichtigung> benachrichtigungen) 
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);
        
        JSONArray array = new JSONArray();
        for(Benachrichtigung ben: benachrichtigungen)
        {
            JSONObject j = new JSONObject();

            j.put(ParamDefines.Id, ben.getId());
            j.put(ParamDefines.benInhalt, ben.getInhalt());
            j.put(ParamDefines.benGelesen, ben.isGelesen());
            SimpleDateFormat d = new SimpleDateFormat("hh:mm dd.MM.yyyy");
            String s = d.format(ben.getErstelldaum().getTime());
            j.put(ParamDefines.benErstelldaum, s);            
            
            if (ben instanceof BenachrProfilGeaendert)
            {
                BenachrProfilGeaendert b = (BenachrProfilGeaendert) ben;
                j.put(ParamDefines.benType, ParamDefines.benTypeProfil);
            }
            else  if (ben instanceof BenachrEinlModerator)
            {
                BenachrEinlModerator b = (BenachrEinlModerator) ben;
                j.put(ParamDefines.benType, ParamDefines.benTypeModerator);
                j.put(ParamDefines.benVeranst, toJson(b.getVeranstaltung()));
            }
            else  if (ben instanceof BenachrKarteikAenderung)
            {
                BenachrKarteikAenderung b = (BenachrKarteikAenderung) ben;
                j.put(ParamDefines.benType, ParamDefines.benTypeKarteikarte);
//                j.put(ParamDefines.benKarteikarte,toJson( b.getKarteikarte()));
            }
            else  if (ben instanceof BenachrNeuerKommentar)
            {
                BenachrNeuerKommentar b = (BenachrNeuerKommentar) ben;
                j.put(ParamDefines.benType, ParamDefines.benTypeKommentar);
//                j.put(ParamDefines.benKommentar, toJson(b.getKommentar()));
            }
            else  if (ben instanceof BenachrVeranstAenderung)
            {
                BenachrVeranstAenderung b = (BenachrVeranstAenderung) ben;
                j.put(ParamDefines.benType, ParamDefines.benTypeVeranstaltung);
                j.put(ParamDefines.benVeranst, toJson(b.getVeranstaltung()));
            }
            array.add(j);
        }
        jo.put(ParamDefines.jsonArrResult, array);
        
        return jo;
    }
    
    // Um die Semester sortieren zu können, wird die ID noch mitgeliefert. Es werden IDs und Name des Semesters
    // in einer Map gespeichert. Die Methode übergibt an JavaScript keine Map sondern ein Array, bei dem
    // in jedem Eintrag sich das Paar aus ID und Name des Semesters befinden.
    static JSONObject toJsonSemesterMap(Map<Integer, String> semester) 
    {
        JSONObject jo = new JSONObject();  
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);
        
        JSONArray array = new JSONArray();

        for(Integer key : semester.keySet())
        {
            JSONObject o = new JSONObject();

            o.put(ParamDefines.Id, key);
            o.put(ParamDefines.Semester, semester.get(key));
            
            
            array.add(o);
        }

        jo.put(ParamDefines.jsonArrResult, array);

        return jo;
    }
}
