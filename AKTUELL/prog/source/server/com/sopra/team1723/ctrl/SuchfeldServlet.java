package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;

/**
 * Dieses Servlet verwaltet alle Suchfelder im System
 */
public class SuchfeldServlet extends ServletController
{

    /**
     * 
     */
    public SuchfeldServlet()
    {
    }

    /**
     * Diese Methode liest einen Suchstring aus den Request Parametern. Danach
     * veranlasst sie die Datenbank nach Benutzern mit ähnlichen Vornamen oder
     * Nachnamen oder nach Veranstaltungen mit ähnlichem Titel zu suchen.
     * 
     * @throws IOException
     */
    public void sucheBenutzerUndVeranstaltungen(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String suchmuster = req.getParameter(ParamDefines.Suchmuster);

        JSONObject jo = JSONConverter.toJsonSuchfeld(dbManager.durchsucheDatenbank(suchmuster));
        outWriter.print(jo);
    }

    /**
     * Sucht einen Benutzer
     * 
     * @param req
     * @param resp
     * @throws IOException
     */
    public void sucheBenutzer(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String suchmuster = req.getParameter(ParamDefines.Suchmuster);

        JSONObject jo = JSONConverter.toJsonSuchfeld(dbManager.durchsucheDatenbankBenutzer(suchmuster));

        outWriter.print(jo);
    }

    /**
     * Sucht einen studiengang
     * 
     * @param req
     * @param resp
     * @throws IOException
     */
    public void sucheStudiengang(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String suchmuster = req.getParameter(ParamDefines.Suchmuster);

        JSONObject jo = JSONConverter.toJsonSuchfeld(dbManager.durchsucheDatenbankStudiengang(suchmuster));
        outWriter.print(jo);

    }

    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        if (aktuelleAction.equals(ParamDefines.ActionSucheBenVeranst))
        {
            sucheBenutzerUndVeranstaltungen(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionSucheBenutzer))
        {
            sucheBenutzer(req, resp);
        }
        else if (aktuelleAction.equals(ParamDefines.ActionSucheStudiengang))
        {
            sucheStudiengang(req, resp);
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo = null;
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
        }
    }
}
