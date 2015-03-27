package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

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

    /**
     * Aus der ubergebenen Request wird der Inhalt und die ID des Erstellers
     * gelesen. Die Methode zum Notizen schreiben des Datenbankmanagers
     * wird aufgerufen. Wenn der Ablauf fehlerfrei verlief,
     * returned die Methode "true", wenn nicht "false".
     * @param request 
     * @param response HttpServletResponse 
     * @return
     */
    private boolean notizErstellen(HttpServletRequest request, HttpServletResponse response ) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der ubergebenen Request wird die ID der Notiz gelesen. Die
     * Methode zum Notizen lesen des Datenbankmanagers wird aufgerufen.
     * Returned den Inhalt via Printwriter.Wenn der Ablauf fehlerfrei
     * verlief, returned die Methode "true", wenn nicht "false".
     * @param request 
     * @param response HttpServletResponse 
     * @return
     */
    private boolean notizAnzeigen(HttpServletRequest request, HttpServletResponse response ) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der ubergebenen Request wird der neue Inhalt, die ID der
     * Notiz und die ID des Erstellers gelesen. Die Methode zum Notizen
     * andern des Datenbankmanagers wird aufgerufen. Wenn der Ablauf
     * fehlerfrei verlief, returned die Methode "true", wenn nicht "false".
     * @param request 
     * @param response HttpServletResponse 
     * @return
     */
    private boolean notizBearbeiten(HttpServletRequest request, HttpServletResponse response ) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der ubergebenen Request wird die ID der Notiz gelesen. Die
     * Methode zum Notizen loschen des Datenbankmanagers wird aufgerufen,
     * die Notiz wird so geloscht. Wenn der Ablauf fehlerfrei verlief,
     * returned die Methode "true", wenn nicht "false".
     * @param request 
     * @param response HttpServletResponse 
     * @return
     */
    private boolean notizLoeschen(HttpServletRequest request, HttpServletResponse response ) {
        // TODO implement here
        return false;
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException
    {
        HttpSession aktuelleSession = req.getSession();
        String aktuelleAction = (String) aktuelleSession.getAttribute(sessionAttributeaktuelleAction);
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        
    }
}