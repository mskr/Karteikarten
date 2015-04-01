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
Dieses Servlet verwaltet alle Suchfelder im System
 */
public class SuchfeldServlet extends ServletController {


    /**
     * 
     */
    public SuchfeldServlet() {
    }

    /**
     * Diese Methode liest einen Suchstring aus den Request Parametern. Danach veranlasst sie die Datenbank nach Benutzern mit
     * ähnlichen Vornamen oder Nachnamen oder nach Veranstaltungen mit ähnlichem Titel zu suchen.
     * @throws IOException 
     */
    public void sucheBenutzerUndVeranstaltungen(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        
        String suchmuster = req.getParameter(ParamDefines.Suchmuster);

        // Da die Methode durchsucheDatenbank() sehr flexibel ist, muss man angeben nach welchen Feldern man die db
        // durchsuchen will. Ein Feld wird eindeutig durch die Klasse und den Attributnamen bestimmt.
        // Zur Kapselung dieser beiden Werte dient die Klasse Klassenfeld. Da die Tabellen und Felder in der db
        // anders heißen werden die Klassenfeldobjekte in durchsucheDatenbank() auf die entsprechenden Bezeichnungen
        // in der db gemapped. Dafür ist die Klasse DatenbankKlassenNamenMapping zuständig.
        ArrayList<Klassenfeld> zuDurchsuchendeFelder = new ArrayList<Klassenfeld>();
        zuDurchsuchendeFelder.add(new Klassenfeld("Benutzer","vorname"));
        zuDurchsuchendeFelder.add(new Klassenfeld("Benutzer","nachname"));
        zuDurchsuchendeFelder.add(new Klassenfeld("Veranstaltung","titel"));

        // Das Ergebnis der Suche wird in einer Liste gespeichert. Die Liste enthält Objekte vom Typ ErgebnisseSuchfeld.
        // Mit diesen Objekten kann auf den konkreten Benutzer bzw. Veranstaltung geschlossen werden
        List<ErgebnisseSuchfeld> ergebnisse = dbManager.durchsucheDatenbank(suchmuster, zuDurchsuchendeFelder);

        if(ergebnisse == null)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        } 
        else
        {
            JSONObject jo = JSONConverter.toJsonSuchfeld(ergebnisse);
            outWriter.print(jo);
        }
    }

    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        
        if(aktuelleAction.equals(ParamDefines.ActionSucheBenVeranst))
        {
            sucheBenutzerUndVeranstaltungen(req, resp);
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo  = null;
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
        }
    }
}
