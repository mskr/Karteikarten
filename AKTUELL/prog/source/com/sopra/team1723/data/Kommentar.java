package com.sopra.team1723.data;

import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.IjsonObject;
import com.sopra.team1723.ctrl.ParamDefines;

/**
 * 
 */
public class Kommentar implements IjsonObject{

    private int id;
    private String inhalt;
    private Calendar erstelldatum;
    private Benutzer ersteller;
    private int vaterID;
    private int karteikartenID;
    private int bewertung;
    private boolean hatBewertet;
    private int antwortAnz;

    public Kommentar(int id, String inhalt, Calendar erstelldatum, 
            Benutzer ersteller, int bewertung, boolean hatBewertet, 
            int vaterID, int karteikartenID, int antwortAnz)
    {
        this.id = id;
        this.inhalt = inhalt;
        this.erstelldatum = erstelldatum;
        this.ersteller = ersteller;
        this.bewertung = bewertung;
        this.vaterID = vaterID;
        this.karteikartenID = karteikartenID;
        this.bewertung = bewertung;
        this.hatBewertet = hatBewertet;
        this.antwortAnz = antwortAnz;
    }
    
    public Kommentar(String inhalt, Benutzer ersteller, int vaterID, int karteikartenID, int antwortAnz)
    {
        this(-1,inhalt,new GregorianCalendar(),ersteller,0,false, vaterID, karteikartenID, antwortAnz);
    }
    public int getId()
    {
        return id;
    }

    public String getInhalt()
    {
        return inhalt;
    }

    public Calendar getErstelldatum()
    {
        return erstelldatum;
    }

    public Benutzer getErsteller()
    {
        return ersteller;
    }

    public int getVaterID()
    {
        return vaterID;
    }

    public int getKarteikartenID()
    {
        return karteikartenID;
    }

    public int getBewertung()
    {
        return bewertung;
    }

    public boolean isHatBewertet()
    {
        return hatBewertet;
    }
    public int getAntwortAnz()
    {
        return antwortAnz;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON(boolean full)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);    
        jo.put(ParamDefines.Id, this.getId());   
        jo.put(ParamDefines.Inhalt, this.getInhalt()); 
        
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        jo.put(ParamDefines.ErstellDatum, f.format(this.getErstelldatum())); 
        
        if(getVaterID() != -1)
            jo.put(ParamDefines.KommentarVaterId, this.getVaterID()); 
        if(getKarteikartenID() != -1)
            jo.put(ParamDefines.KommentarKKid, this.getKarteikartenID()); 
        jo.put(ParamDefines.AntwortCount, this.getAntwortAnz()); 
        
        jo.put(ParamDefines.Ersteller,this.getErsteller().toJSON(false));
        return jo;
    }

}