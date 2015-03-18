/**
 * @author Andreas
 * 
 */


package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

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
    protected final String sessionAttributeEMail = "eMail";
    // TODO: Alle benutzer attribute speichern oder nur eMail? Was passiert wenn Benutzer geändert wird ?!
    
    /**
     *  Request Parameter
     */
    protected final String requestAction = "action";
    protected final String requestActionLogin = "login";
    protected final String requestActionLogout = "logout";
    protected final String requestActionRegister = "registrieren";
    protected final String requestActionResetPasswort = "resetPasswort";
    protected final String requestActionGetBenutzer = "getBenutzer";

    protected final String requestEmail = "email";
    protected final String requestPassword = "pass";
    protected final String requestVorname = "vorname";
    protected final String requestNachname = "nachname";
    protected final String requestMatrikelNr = "matrikelNr";
    protected final String requestStudiengang = "studienGang";
    protected final String requestNutzerstatus = "nutzerStatus";

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
        if(session.getAttribute(sessionAttributeEMail) == null)
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
        String eMail = (String) session.getAttribute(sessionAttributeEMail);
        
        if(eMail == null)
            return null;
        
        return dbManager.leseBenutzer(eMail);
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
        // aktuelle Session holen
        aktuelleSession = req.getSession();
        outWriter = resp.getWriter();
        
        if(dbManager == null)
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }

        // Ist der Benutzer eingeloggt ?
        if(pruefeLogin(aktuelleSession))
        {
            // Wenn ja, dann stelle allen Servlets den Benutzer zur Verfügung
            aktuellerBenutzer = leseBenutzer(aktuelleSession);
        }
        // wenn nicht eingeloggt fehlermeldung zurückgeben an Aufrufer
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNotLoggedIn);
            outWriter.print(jo);
        }
        
        // Hole die vom client angefragte Aktion
        aktuelleAction = req.getParameter(requestAction);
        
        if(isEmptyAndRemoveSpaces(aktuelleAction))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
    }
    
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
}