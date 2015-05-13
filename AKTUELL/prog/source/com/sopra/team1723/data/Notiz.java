package com.sopra.team1723.data;

import java.util.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.IjsonObject;
import com.sopra.team1723.ctrl.ParamDefines;

/**
 * 
 */
public class Notiz implements IjsonObject{    
    
    public Notiz(int id, String inhalt, int ersteller, int karteikarte)
    {
        super();
        this.id = id;
        this.inhalt = inhalt;
        this.ersteller = ersteller;
        this.karteikarte = karteikarte;
    }
    
    

    public Notiz(String inhalt, int ersteller, int karteikarte)
    {
        super();
        this.id = -1;
        this.inhalt = inhalt;
        this.ersteller = ersteller;
        this.karteikarte = karteikarte;
    }



    /**
     * 
     */
    public Notiz() {
    }
    
    

    public int getId()
    {
        return id;
    }

    public String getInhalt()
    {
        return inhalt;
    }

    public int getErsteller()
    {
        return ersteller;
    }

    public int getKarteikarte()
    {
        return karteikarte;
    }

    public void setInhalt(String inhalt)
    {
        this.inhalt = inhalt;
    }

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String inhalt;

    /**
     * 
     */
    private int ersteller;

    /**
     * 
     */
    private int karteikarte;

    @Override
    public JSONObject toJSON(boolean full)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        jo.put(ParamDefines.Id, getId());
        jo.put(ParamDefines.Inhalt, getInhalt());
        return jo;
    }

}