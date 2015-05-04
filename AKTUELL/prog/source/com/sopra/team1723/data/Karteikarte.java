package com.sopra.team1723.data;

import java.util.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.IjsonObject;
import com.sopra.team1723.ctrl.ParamDefines;

/**
 * 
 */
public class Karteikarte implements IjsonObject {

    
    

    public Karteikarte(String titel,  String inhalt, KarteikartenTyp typ, int veranstaltung)
    {
        super();
        this.id = -1;
        this.titel = titel;
        this.aenderungsdatum = Calendar.getInstance();
        this.inhalt = inhalt;
        this.typ = typ;
        this.veranstaltung = veranstaltung;
        this.bewertung = 0;
    }
    
    

    public Karteikarte(int id, String titel, Calendar aenderungsdatum, String inhalt, KarteikartenTyp typ,
            int veranstaltung, int bewertung)
    {
        super();
        this.id = id;
        this.titel = titel;
        this.aenderungsdatum = aenderungsdatum;
        this.inhalt = inhalt;
        this.typ = typ;
        this.veranstaltung = veranstaltung;
        this.bewertung = bewertung;
    }



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
    private Calendar aenderungsdatum;

    /**
     * 
     */
    private String inhalt;

    /**
     * 
     */
    private KarteikartenTyp typ;
    
    
    
    private int veranstaltung;
    
    private int bewertung;
        
        

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitel()
    {
        return titel;
    }

    public void setTitel(String titel)
    {
        this.titel = titel;
    }

    public Calendar getAenderungsdatum()
    {
        return aenderungsdatum;
    }

    public void setAenderungsdatum(Calendar aenderungsdatum)
    {
        this.aenderungsdatum = aenderungsdatum;
    }

    public String getInhalt()
    {
        return inhalt;
    }

    public void setInhalt(String inhalt)
    {
        this.inhalt = inhalt;
    }


    public KarteikartenTyp getTyp()
    {
        return typ;
    }

    public void setTyp(KarteikartenTyp typ)
    {
        this.typ = typ;
    }

    public int getVeranstaltung()
    {
        return veranstaltung;
    }

    public void setVeranstaltung(int veranstaltung)
    {
        this.veranstaltung = veranstaltung;
    }

    public int getBewertung()
    {
        return bewertung;
    }

    public void setBewertung(int bewertung)
    {
        bewertung = bewertung;
    }



    @Override
    public JSONObject toJSON(boolean full)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.Klasse, ParamDefines.KlasseKarteikarte);
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        jo.put(ParamDefines.Id, this.getId());
        jo.put(ParamDefines.Titel, this.getTitel());
        jo.put(ParamDefines.Type, this.getTyp().name());
        jo.put(ParamDefines.Veranstaltung, this.getVeranstaltung());
        if(full)
        {
            jo.put(ParamDefines.Aenderungsdatum, this.getAenderungsdatum());
            jo.put(ParamDefines.Bewertung, this.getBewertung());
            jo.put(ParamDefines.Inhalt, this.getInhalt());
        }
        return jo;
    }
    
    

    
}