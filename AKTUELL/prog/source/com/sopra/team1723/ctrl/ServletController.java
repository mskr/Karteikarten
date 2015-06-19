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

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;

/**
 * Abstrakte Oberklasse, die die Login-Überprüfung übernimmt und gegebenenfalls
 * an das Benutzer-Servlet weiterleitet oder einen Fehler an den Aufrufer
 * zurückgibt.
 */
public abstract class ServletController extends HttpServlet
{
    /**
     * Session Attribute, die verwendet werden
     */
    protected final static String sessionAttributeUserID            = "UserID";
    protected final static String sessionAttributeGewähltesSemester = "gewähltesSemester";
    protected final static String sessionAttributeDbManager         = "dbManager";
    protected final static String sessionAttributeaktuellerBenutzer = "aktuellerBenutzer";
    public final static String    sessionAttributePDFExporter       = "pdfExporter";

    // Timeout für die Session.
    protected final int           sessionTimeoutSek                 = 60 * 20;                   // 20
                                                                                                  // Minuten

    // DebugMode = true -> Server printed log nachrichrichten in der Konsole
    public static final boolean   DEBUGMODE                         = false;

    /**
     * DateiPfade
     */
    public final static String    dirFiles                          = "files/";
    public final static String    dirProfilBilder                   = dirFiles + "profilBilder/";
    public final static String    dirKKBild                         = dirFiles + "images/";
    public final static String    dirKKVideo                        = dirFiles + "videos/";

    /**
     * Abstrakte Oberklasse, die die Login-Überprüfung übernimmt und
     * gegebenenfalls an das Benutzer-Servlet weiterleitet oder einen Fehler an
     * den Aufrufer zurückgibt.
     */
    public ServletController()
    {

    }

    /**
     * Prüft, ob die id des Benutzers in der aktuellen Session vorhanden ist und
     * ob der Benutzer somit eingeloggt ist.
     * 
     * @return
     */
    protected boolean pruefeLogin(HttpSession session)
    {
        IDatenbankmanager dbManager = (IDatenbankmanager) session.getAttribute(sessionAttributeDbManager);

        if (dbManager == null)
            return false;

        // Benutzer nicht eingeloggt?
        if (session.getAttribute(sessionAttributeUserID) == null)
            return false;

        // Attribut ist gesetzt, Benutzer muss eingeloggt sein
        return true;
    }

    /**
     * liest das Benutzerobjekt aus der Datenbank aus.
     * 
     * @param session
     * @return
     */
    private Benutzer leseBenutzer(HttpSession session)
    {
        Integer userID = (Integer) session.getAttribute(sessionAttributeUserID);

        if (userID == null)
            return null;

        IDatenbankmanager dbManager = (IDatenbankmanager) session.getAttribute(sessionAttributeDbManager);

        if (dbManager == null)
            return null;

        return dbManager.leseBenutzer(userID);
    }

    /**
     * Gibt alle Parameter aus, die am Request hängen. Für Debugzwecke.
     * 
     * @param req
     */
    protected void printAllParameters(HttpServletRequest req)
    {
        Enumeration<String> parameterNames = req.getParameterNames();
        System.out.println("URL: " + req.getRequestURI());
        System.out.println("Empfangene Parameter: ");
        while (parameterNames.hasMoreElements())
        {
            String paramName = parameterNames.nextElement();
            System.out.print(paramName + " :");

            String[] paramValues = req.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; i++)
            {
                String paramValue = paramValues[i];
                System.out.print(" " + paramValue + ", ");
            }
            System.out.println();

        }
    }

    /**
     * Liefert das Aktuelle Semester als Text zurück
     * 
     * @return
     */
    protected String leseAktuellesSemester()
    {
        Calendar aktDatum = Calendar.getInstance();

        int aktYear = aktDatum.get(Calendar.YEAR);
        ParamDefines.WiSeBeginn.set(Calendar.YEAR, aktYear);
        ParamDefines.WiSeEnde.set(Calendar.YEAR, aktYear);
        if (aktDatum.after(ParamDefines.WiSeBeginn))
            return "WiSe" + aktYear + "/" + String.valueOf(aktYear + 1).substring(2);
        else if (aktDatum.before(ParamDefines.WiSeEnde))
            return "WiSe" + String.valueOf(aktYear - 1) + "/" + String.valueOf(aktYear).substring(2);
        else
            return "SoSe" + String.valueOf(aktYear);
    }

    /**
     * Get requests an post weiterleiten
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    /**
     * Behandelt alle requests, die im system ankommen. (Außer login)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // Encoding UTF-8
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        // Output: json-Objekte
        resp.setContentType("application/json");

        if (DEBUGMODE)
            printAllParameters(req);

        try
        {
            // Zuerst vorverarbeitung der Parameter und zugangsprüfung
            if (preProcessRequest(req, resp))
            {
                // Wenn erfolgreich
                // Hole die vom client angefragte Aktion
                String action = req.getParameter(ParamDefines.Action);

                if (isEmptyAndRemoveSpaces(action))
                {
                    // Sende Error zurück
                    PrintWriter outWriter = resp.getWriter();
                    JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return;
                }
                // Anfrage an die Sub-Klasse weiterleiten.
                processRequest(action, req, resp);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            PrintWriter outWriter = resp.getWriter();
            outWriter.print(jo);
        }
    }

    /**
     * Abstrakte methode, die von allen Servlets (Außer login) implementiert
     * werden muss.
     * 
     * @param aktuelleAction
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected abstract void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException;

    /**
     * Vorverarbeitung der Anfragen
     * 
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    protected boolean preProcessRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException
    {
        // Bisschen platz zwischen den Requests
        if (DEBUGMODE)
        {
            System.out.println();
            System.out.println();
        }

        PrintWriter outWriter = resp.getWriter();

        // Prüfen ob session abgelaufen ist
        if (req.getRequestedSessionId() != null && !req.isRequestedSessionIdValid() && req.getSession(false) != null
                && !req.getSession(false).isNew())
        {
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
        if (s.isNew())
            s.setMaxInactiveInterval(sessionTimeoutSek);

        IDatenbankmanager dbManager = (IDatenbankmanager) s.getAttribute(sessionAttributeDbManager);
        // Neuen Datenbankmanager erzeugen, falls für diese session noch keiner
        // erzeugt wurde
        if (dbManager == null)
        {
            try
            {
                dbManager = new Datenbankmanager();
                if (DEBUGMODE)
                    System.out.println("Erzeuge neuen Db-Manager.");
                s.setAttribute(sessionAttributeDbManager, dbManager);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                dbManager = null;
                System.err
                        .println("Es Konnte keine Verbindung zur Datenbank hergestellt werden oder ein unerwarteter Fehler ist aufgetreten!");
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return false;
            }
        }

        // Ist der Benutzer eingeloggt ?
        if (pruefeLogin(s))
        {
            // Wenn ja, dann stelle allen Servlets den Benutzer zur Verfügung
            Benutzer b = leseBenutzer(s);
            if (b == null)
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

        if (s.getAttribute(sessionAttributeGewähltesSemester) == null)
            s.setAttribute(sessionAttributeGewähltesSemester, leseAktuellesSemester());

        return true;
    }

    /**
     * Diese Funktion prüft, ob der übergebene String leer ist und ob entfernt
     * automatisch alle Leerzeichen am Anfang und am Ende.
     * 
     * @param txt
     * @return
     */
    boolean isEmptyAndRemoveSpaces(String txt)
    {
        if (txt == null)
            return true;

        if (txt.equals(""))
            return true;

        // Falls doch inhalt drin ist, trim alle leerzeichen/zeilen weg
        txt.trim();

        return false;
    }

    /**
     * Diese Funktion prüft, ob der übergebene String leer ist.
     * 
     * @param txt
     * @return
     */
    boolean isEmpty(String txt)
    {
        if (txt == null)
            return true;

        if (txt.equals(""))
            return true;

        return false;
    }
}
