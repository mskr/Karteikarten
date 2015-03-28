/**
 * @author Andreas
 * 
 */


package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    protected final String sessionAttributeGewähltesSemester = "gewähltesSemester";
    protected final String sessionAttributeDbManager = "dbManager";
    protected final String sessionAttributeaktuelleAction = "aktuelleAction";
    protected final String sessionAttributeaktuellerBenutzer = "aktuellerBenutzer";

    protected final int sessionTimeoutSek = 60*20;          // 20 Minuten

    /**
     *  DateiPfade
     */
    public final static String dirFiles = "files/";             
    public final static String dirProfilBilder = dirFiles + "profilBilder/";

    /**
     * Abstrakte Oberklasse, die die Login-Überprüfung übernimmt und gegebenenfalls an das 
     * Benutzer-Servlet weiterleitet oder einen Fehler an den Aufrufer zurückgibt.
     */
    public ServletController() 
    {
    }

    //    /**
    //     * Aktuell angemeldeter Benutzer. Null, falls Benutzer nicht angemeldet ist.
    //     */
    //    protected Benutzer aktuellerBenutzer = null;
    //
    //    /**
    //     * 
    //     */
    //    protected String gewähltesSemester = null;
    //    
    //    /**
    //     * 
    //     */
    //    protected IDatenbankmanager dbManager = null;
    //
    //    /**
    //     *  Aktuelle Session.
    //     */
    //    protected HttpSession aktuelleSession = null;
    //
    //    /**
    //     *  Print Writer über den Daten an den Client geschrieben werden können
    //     */
    //    protected PrintWriter outWriter = null;
    //
    //    /**
    //     * Hier steht die vom ServletController ausgelesene Aktion, die der Client ausführen will.
    //     */
    //    protected String aktuelleAction = null;


    /**
     * Prüft, ob die eMail-Adresse des Benutzers in der aktuellen Session vorhanden ist und ob der Benutzer somit eingeloggt ist.
     * @return
     */
    protected boolean pruefeLogin(HttpSession session) 
    {
        IDatenbankmanager dbManager = (IDatenbankmanager) session.getAttribute(sessionAttributeDbManager);

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
        Integer userID = (Integer) session.getAttribute(sessionAttributeUserID);

        if(userID == null)
            return null;

        IDatenbankmanager dbManager = (IDatenbankmanager) session.getAttribute(sessionAttributeDbManager);

        if(dbManager == null)
            return null;

        return dbManager.leseBenutzer(userID);
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

    protected String leseAktuellesSemester(){
        Calendar aktDatum = Calendar.getInstance();

        int aktYear = aktDatum.get(Calendar.YEAR);
        Calendar WiSeBegin = Calendar.getInstance();
        Calendar WiSeEnde = Calendar.getInstance();
        WiSeBegin.set(Calendar.MONTH, ParamDefines.WiSeMonatBeginn);
        WiSeBegin.set(Calendar.DAY_OF_MONTH, ParamDefines.WiSeTagBeginn);
        WiSeEnde.set(Calendar.MONTH, ParamDefines.WiSeMonatEnde);
        WiSeEnde.set(Calendar.DAY_OF_MONTH, ParamDefines.WiSeTagEnde);
        if(aktDatum.after(WiSeBegin))
            return "WiSe"+aktYear+"/"+String.valueOf(aktYear+1).substring(2);
        else if (aktDatum.before(WiSeEnde))
            return "WiSe"+String.valueOf(aktYear-1)+"/"+String.valueOf(aktYear).substring(2);
        else
            return "SoSe"+String.valueOf(aktYear);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        try{
            if(preProcessRequest(req, resp))
                processRequest(req, resp);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            PrintWriter outWriter = resp.getWriter();
            outWriter.print(jo);
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {}

    protected boolean preProcessRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        // Bisschen platz zwischen den Requests
        System.out.println();
        System.out.println();

        PrintWriter outWriter = resp.getWriter();
        resp.setContentType("text/json");

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
            HttpSession s = req.getSession();
            s.setMaxInactiveInterval(sessionTimeoutSek);
            return false;
        }

        HttpSession s = req.getSession();
        if(s.isNew())
            s.setMaxInactiveInterval(sessionTimeoutSek);

        printAllParameters(req);

        IDatenbankmanager dbManager = (IDatenbankmanager) s.getAttribute(sessionAttributeDbManager);
        if(dbManager == null)
        {
            try
            {
                dbManager = new Datenbankmanager();
                System.out.println("Erzeuge neuen Db-Manager.");
                s.setAttribute(sessionAttributeDbManager, dbManager);
            }
            catch (Exception e)
            {
                dbManager = null;
                System.err.println("Es Konnte keine Verbindung zur Datenbank hergestellt werden oder ein unerwarteter Fehler ist aufgetreten!");
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return false;
            }
        }

        // Ist der Benutzer eingeloggt ?
        if(pruefeLogin(s))
        {
            // Wenn ja, dann stelle allen Servlets den Benutzer zur Verfügung
            Benutzer b = leseBenutzer(s);
            if(b == null)
            {
                // Sende Nack mit ErrorText zurück
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return false;
            }
            s.setAttribute(sessionAttributeaktuellerBenutzer, b);
        }
        // wenn nicht eingeloggt fehlermeldung zurückgeben an Aufrufer
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotLoggedIn);
            outWriter.print(jo);
            return false;
        }
        // Hole die vom client angefragte Aktion
        String action = req.getParameter(ParamDefines.Action);

        if(isEmptyAndRemoveSpaces(action))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        s.setAttribute(sessionAttributeaktuelleAction, action);

        if(s.getAttribute(sessionAttributeGewähltesSemester)== null)
            s.setAttribute(sessionAttributeGewähltesSemester, leseAktuellesSemester());

        return true;
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