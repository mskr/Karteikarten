package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.Benachrichtigung;
import com.sopra.team1723.data.Benutzer;

public class BenachrichtigungsServlet extends ServletController
{

    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException
    {
        JSONObject jo = null;
        
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        
        if(aktuelleAction.equals(ParamDefines.ActionLeseBenachrichtungen))
        {
            List<Benachrichtigung> benachrichtigungen = dbManager.leseBenachrichtigungen(aktuellerBenutzer.getId(),20);  
            jo = JSONConverter.toJsonBenachrichtigungen(benachrichtigungen);
            outWriter.print(jo);
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
        }
        
    }

}
