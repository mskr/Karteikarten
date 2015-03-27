/**
 * @author Andreas
 * 
 */


package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;

/**
 * Abstrakte Oberklasse, die die Login-Überprüfung übernimmt und gegebenenfalls an das 
 * Benutzer-Servlet weiterleitet oder einen Fehler an den Aufrufer zurückgibt.
 */
public class ServletController extends HttpServlet 
{
    /**
     *  Session Attribute, die verwendet werden
     */
//    protected final String sessionAttributeEMail = "eMail";
    protected final String sessionAttributeUserID = "UserID";
    // TODO: Alle benutzer attribute speichern oder nur eMail? Was passiert wenn Benutzer geändert wird ?!
    
    protected final int sessionTimeoutSek = 60*20;          // 20 Minuten
    
    /**
     *  DateiPfade
     */
    public final static String dirFiles = "files/";             
    public final static String dirProfilBilder = dirFiles + "profilBilder/";
    

    private boolean doProcessing = false;
    
    /**
     * Abstrakte Oberklasse, die die Login-Überprüfung übernimmt und gegebenenfalls an das 
     * Benutzer-Servlet weiterleitet oder einen Fehler an den Aufrufer zurückgibt.
     */
    public ServletController() 
    {
        try
        {
            dbManager = new Datenbankmanager();
        }
        catch (Exception e)
        {
            dbManager= null;
            System.err.println("Es Konnte keine Verbindung zur Datenbank hergestellt werden oder ein unerwarteter Fehler ist aufgetreten!");
        }
    }

    /**
     * Aktuell angemeldeter Benutzer. Null, falls Benutzer nicht angemeldet ist.
     */
    protected Benutzer aktuellerBenutzer = null;

    /**
     * 
     */
    protected IDatenbankmanager dbManager = null;
    
    /**
     *  Aktuelle Session.
     */
    protected HttpSession aktuelleSession = null;
    
    /**
     *  Print Writer über den Daten an den Client geschrieben werden können
     */
    protected PrintWriter outWriter = null;
    
    /**
     * Hier steht die vom ServletController ausgelesene Aktion, die der Client ausführen will.
     */
    protected String aktuelleAction = null;
    

    /**
     * Prüft, ob die eMail-Adresse des Benutzers in der aktuellen Session vorhanden ist und ob der Benutzer somit eingeloggt ist.
     * @return
     */
    protected boolean pruefeLogin(HttpSession session) 
    {
        if(dbManager == null)
            return false;
        
        // Benutzer nicht eingeloggt?
        if(session.getAttribute(sessionAttributeUserID) == null)
            return false;
        
        // Attribut ist gesetzt, Benutzer muss eingeloggt sein
        return true; 
    }

    /**
     * liest das Benutzerobjekt aus der Datenbank aus.
     * @param session 
     * @return
     */
    private Benutzer leseBenutzer(HttpSession session) 
    {        
        String userID = (String) session.getAttribute(sessionAttributeUserID);
        
        if(userID == null)
            return null;
        
        int id = Integer.parseInt(userID);
        
        return dbManager.leseBenutzer(id);
    }
    /**
     * Gibt alle Parameter aus, die am Request hängen
     * @param req
     */
    private void printAllParameters(HttpServletRequest req)
    {
        Enumeration<String> parameterNames = req.getParameterNames();
        System.out.println("URL: " + req.getRequestURI());
        System.out.println("Empfangene Parameter: ");
        while (parameterNames.hasMoreElements()) 
        {
            String paramName = parameterNames.nextElement();
            System.out.print(paramName + " :");

            String[] paramValues = req.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                System.out.print(" " + paramValue + ", ");
            }
            System.out.println();

        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
    	doPost(req, resp);
    }

    @Override
    /**
     * Login Prüfen und  Benutzerobjet laden
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        doProcessing = false;
        aktuelleSession = null;
        aktuelleAction = null;
        aktuellerBenutzer = null;
        resp.setContentType("text/json");
        outWriter = resp.getWriter();

        // Bisschen platz zwischen den Requests
        System.out.println();
        System.out.println();
        
        // Prüfen ob session abgelaufen ist
        if (req.getRequestedSessionId() != null && 
                !req.isRequestedSessionIdValid() && 
                req.getSession(false) != null && 
                !req.getSession(false).isNew()) 
        {
            // TODO Manchmal wird eine Session als abgelaufen gemeldet, wenn sich der Nutzer normal ausgeloggt hat.
            System.out.println("Session " + req.getRequestedSessionId() + " ist abgelaufen!");
            // Session is expired
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSessionExpired);
            outWriter.print(jo);
            
            // Neue Session erzeugen
            aktuelleSession = req.getSession();
            aktuelleSession.setMaxInactiveInterval(sessionTimeoutSek);
            return;
        }

        aktuelleSession = req.getSession();
        if(aktuelleSession.isNew())
            aktuelleSession.setMaxInactiveInterval(sessionTimeoutSek);

        printAllParameters(req);
        
        if(dbManager == null)
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }

        // Ist der Benutzer eingeloggt ?
        if(pruefeLogin(aktuelleSession))
        {
            // Wenn ja, dann stelle allen Servlets den Benutzer zur Verfügung
            aktuellerBenutzer = leseBenutzer(aktuelleSession);
            if(aktuellerBenutzer == null)
            {
                // Sende Nack mit ErrorText zurück
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return;
            }
        }
        // wenn nicht eingeloggt fehlermeldung zurückgeben an Aufrufer
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotLoggedIn);
            outWriter.print(jo);
            return;
        }
        // Hole die vom client angefragte Aktion
        aktuelleAction = req.getParameter(ParamDefines.Action);

        if(isEmptyAndRemoveSpaces(aktuelleAction))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        doProcessing = true;
    }
    /**
     * Gibt "True" zurück, wenn Aufruf von super.doPost(...) keinen Error an den Client zurückgegeben hat.
     * Wenn dies der fall ist, dann sollte der Aufrufer eine Antwort an den Client schicken
     * @return
     */
    protected boolean doProcessing()
    {
        return doProcessing;
    }

    /**
     * Diese Funktion prüft, ob der übergebene String leer ist und 
     * ob entfernt automatisch alle Leerzeichen am Anfang und am Ende.
     * @param txt
     * @return
     */
    boolean isEmptyAndRemoveSpaces(String txt)
    {
        if(txt == null)
            return true;
        
        if(txt.equals(""))
            return true;
        
        // Falls doch inhalt drin ist, trim alle leerzeichen/zeilen weg
        txt.trim();
        
        return false;
    }
    /**
     * Diese Funktion prüft, ob der übergebene String leer ist.
     * @param txt
     * @return
     */
    boolean isEmpty(String txt)
    {
        if(txt == null)
            return true;
        
        if(txt.equals(""))
            return true;
        
        return false;
    }
}