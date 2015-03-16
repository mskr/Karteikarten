package com.sopra.team1723.ctrl;

import org.json.simple.*;

public class JSONConverter
{
    /**
     * JSON-Feld-Werte
     */
    
    static private final String jsonErrorTxt = "ERROR";
    // Alle bekannten Fehlertypen
    static public final String jsonErrorNoError = "NOERROR";
    static public final String jsonErrorNotLoggedIn = "NOTLOGGEDIN";
    static public final String jsonErrorLoginFailed = "LOGINFAILED";
    static public final String jsonErrorLogoutFailed = "LOGOUTFAILED";
    static public final String jsonErrorRegisterFailed = "REGISTERFAILED";
    static public final String jsonErrorPwResetFailed = "PWRESETFAILED";
    static public final String jsonErrorInvalidParam = "INVALIDPARAM";
    
    
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
