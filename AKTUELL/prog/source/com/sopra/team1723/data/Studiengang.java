package com.sopra.team1723.data;

import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.IjsonObject;
import com.sopra.team1723.ctrl.ParamDefines;

public class Studiengang implements IjsonObject
{
    private String name;
    
    
    
    public Studiengang(String name)
    {
        super();
        this.name = name;
    }



    public String getName()
    {
        return name;
    }



    public void setName(String name)
    {
        this.name = name;
    }



    @Override
    public JSONObject toJSON(boolean full)
    {
        // Ignore parameter full
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.Klasse, ParamDefines.KlasseStudieng);
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);    
        jo.put(ParamDefines.Studiengang, this.getName());   

        return jo;
    }
}
