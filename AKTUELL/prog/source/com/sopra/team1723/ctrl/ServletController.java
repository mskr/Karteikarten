/**
 * @author Andreas
 * 
 */


package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.*;

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
    protected final String sessionAttributeEMail = "eMail";
    // TODO: Alle benutzer attribute speichern oder nur eMail? Was passiert wenn Benutzer geändert wird ?!
    
    protected final int sessionTimeoutSek = 60*20;          // 20 Minuten
    
    /**
     *  Request Parameter
     */
    protected final String requestAction = "action";
    protected final String requestActionLogin = "login";
    protected final String requestActionLogout = "logout";
    protected final String requestActionRegister = "registrieren";
    protected final String requestActionResetPasswort = "resetPasswort";
    protected final String requestActionGetBenutzer = "getBenutzer";
    protected final String requestActionGetOtherBenutzer = "getOtherBenutzer";
    protected final String requestActionGetStudiengaenge = "getStudiengaenge";
    protected final String requestActionAenderePasswort = "aenderePasswort";
    protected final String requestActionAendereProfil = "aendereProfil";

    protected final String requestEmail = "email";
    protected final String requestEmailNew = "emailNew";
    protected final String requestPassword = "pass";
    protected final String requestPasswordNew = "passNew";
    protected final String requestVorname = "vorname";
    protected final String requestNachname = "nachname";
    protected final String requestMatrikelNr = "matrikelnr";
    protected final String requestStudiengang = "studiengang";
    protected final String requestNutzerstatus = "nutzerstatus";
    protected final String requestNotifyVeranstAenderung = "notifyVeranstAenderung";
    protected final String requestNotifyKarteikartenAenderung = "notifyKarteikartenAenderung";
    protected final String requestNotifyKommentare = "notifyKommentare";

    private boolean doPorcessing = false;
    
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
        doPorcessing = false;
        aktuelleSession = null;
        aktuelleAction = null;
        aktuellerBenutzer = null;
        outWriter = resp.getWriter();
        
        // Prüfen ob session abgelaufen ist
        if (req.getRequestedSessionId() != null && 
                !req.isRequestedSessionIdValid() && 
                req.getSession(false) != null && 
                !req.getSession(false).isNew()) 
        {
            // TODO Manchmal wird eine Session als abgelaufen gemeldet, wenn sich der Nutzer normal ausgeloggt hat.
            System.out.println("Session " + req.getRequestedSessionId() + " ist abgelaufen!");
            // Session is expired
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSessionExpired);
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
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
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
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
                outWriter.print(jo);
                return;
            }
        }
        // wenn nicht eingeloggt fehlermeldung zurückgeben an Aufrufer
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNotLoggedIn);
            outWriter.print(jo);
            return;
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
        doPorcessing = true;
        System.out.println("Action: " + aktuelleAction);
    }
    /**
     * Gibt "True" zurück, wenn Aufruf von super.doPost(...) keinen Error an den Client zurückgegeben hat.
     * Wenn dies der fall ist, dann sollte der Aufrufer eine Antwort an den Client schicken
     * @return
     */
    protected boolean doProcessing()
    {
        return doPorcessing;
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