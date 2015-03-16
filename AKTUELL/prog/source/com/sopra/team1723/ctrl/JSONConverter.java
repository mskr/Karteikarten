package com.sopra.team1723.ctrl;

import org.json.simple.*;

public class JSONConverter
{
    /**
     * JSON-Feld-Werte
     */
    
    static private final String jsonErrorTxt = "error";
    // Alle bekannten Fehlertypen
    static public final String jsonErrorNoError = "noerror";
    static public final String jsonErrorNotLoggedIn = "notloggedin";
    static public final String jsonErrorLoginFailed = "loginfailed";
    static public final String jsonErrorLogoutFailed = "logoutfailed";
    static public final String jsonErrorRegisterFailed = "registerfailed";
    static public final String jsonErrorPwResetFailed = "pwresetfailed";
    static public final String jsonErrorInvalidParam = "invalidparam";
     
    
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
}
