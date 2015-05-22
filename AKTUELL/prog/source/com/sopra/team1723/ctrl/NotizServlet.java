package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;

/**
 * Verwaltet die Notizen
 */
public class NotizServlet extends ServletController {

    /**
     * Verwaltet die Notizen
     */
    public NotizServlet() {
    }


    private boolean notizLesen(HttpServletRequest request, HttpServletResponse response ) throws IOException {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        
        String kkIdStr = request.getParameter(ParamDefines.Id);

        if(isEmpty(kkIdStr))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int kkId;
        try
        {
            kkId = Integer.parseInt(kkIdStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        Notiz n = dbManager.leseNotiz(aktuellerBenutzer.getId(), kkId);
        
        // Wenn keine Notiz existiert, leerstring zurückgeben
        JSONObject jo;
        if(n == null)
        {
            n = new Notiz("",aktuellerBenutzer.getId(),kkId);
        }

        jo = n.toJSON(true);
        outWriter.print(jo);
        
        
        return true;
    }

    private boolean notizSpeichern(HttpServletRequest request, HttpServletResponse response ) throws IOException {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        
        String kkIdStr = request.getParameter(ParamDefines.Id);
        String inhalt =  request.getParameter(ParamDefines.Inhalt);
        if(isEmpty(kkIdStr) || inhalt == null)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        int kkId;
        try
        {
            kkId = Integer.parseInt(kkIdStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        // Prüfen ob Notiz exisitert
        Notiz n = dbManager.leseNotiz(aktuellerBenutzer.getId(), kkId);
        
        // Wenn keine Notiz existiert, neue erzeugen
        JSONObject jo;
        if(n == null )
        {
            if(inhalt.equals(""))
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            else
            {
                n = new Notiz(inhalt,aktuellerBenutzer.getId(),kkId);
                if(dbManager.schreibeNotiz(n))
                    jo = n.toJSON(true);
                else
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            }
        }
        // Andernfalls bearbeiten
        else
        {
            if(inhalt.equals(""))
            {
                if( dbManager.loescheNotiz(n.getId()))
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
                else
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            }
            else
            {
                n.setInhalt(inhalt);
                if(dbManager.bearbeiteNotiz(n))
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
                else
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            }
        }

        outWriter.print(jo);
        return true;
    }

    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        if(aktuelleAction.equals(ParamDefines.ActionSpeichereNotiz))
        {
            notizSpeichern(req, resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionLeseNotiz))
        {
            notizLesen(req, resp);
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
        }  
    }
}