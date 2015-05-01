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
        else if(aktuelleAction.equals(ParamDefines.ActionMarkiereBenGelesen) || aktuelleAction.equals(ParamDefines.ActionEinlModeratorAnnehmen)
                || aktuelleAction.equals(ParamDefines.ActionEinlModeratorAnnehmen))
        {
            String benIDStr = req.getParameter(ParamDefines.Id);
            if(isEmpty(benIDStr))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
            }
            else{
                int benID = 0;
                try
                {
                    benID = Integer.parseInt(benIDStr);
                }
                catch (NumberFormatException e)
                {
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return;
                }
                
                if(!dbManager.markiereBenAlsGelesen(benID, aktuellerBenutzer.getId())){
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                    outWriter.print(jo);
                }
                    
                
                if(aktuelleAction.equals(ParamDefines.ActionEinlModeratorAnnehmen)){
                    if(!dbManager.einladungModeratorAnnehmen(benID, aktuellerBenutzer.getId())){
                        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                        outWriter.print(jo);
                        return;
                    }
                } 
                else if(aktuelleAction.equals(ParamDefines.ActionEinlModeratorAnnehmen)){
                    if(!dbManager.einladungModeratorAblehnen(benID, aktuellerBenutzer.getId())){
                        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                        outWriter.print(jo);
                        return;
                    }
                }
                
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
                outWriter.print(jo);
            }
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
        }
    }
}
