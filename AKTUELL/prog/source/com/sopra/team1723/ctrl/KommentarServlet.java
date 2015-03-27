package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.sopra.team1723.data.*;

/**
 * Verwaltet Kommentare
 */
public class KommentarServlet extends ServletController {

    /**
     * Verwaltet Kommentare
     */
    public KommentarServlet() {
    }

    /**
     * Aus der Request werden folgende Parameter ausgelesen: Der
     * Inhalt("content"), Das Erstelldatum("dateofbirth"),Die ErstellerID("
     * creatorID") und die VaterId("fatherID")Dies wird an den Datenbankmanager
     * weitergegeben.
     * @param request 
     * @param response 
     * @return
     */
    private boolean kommentarErstellen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Request werden folgende Parameter ausgelesen: Die Kommentarid("
     * commentID"). Dies wird an den Datenbankmanager
     * weitergegeben.
     * @param request 
     * @param response 
     * @return
     */
    private boolean kommentareAnzeigen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Request werden folgende Parameter ausgelesen: Die kommentarid("
     * commentID"), die Nuterid( UserID") und die Bewertung("
     * commentrating") Dies wird an den Datenbankmanager weitergegeben.
     * @param request 
     * @param response 
     * @return
     */
    private boolean kommentarBewerten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Request werden folgende Parameter ausgelesen: Die Kommentarid("
     * commentID"). Dies wird an den Datenbankmanager
     * weitergegeben.
     * @param request 
     * @param response 
     * @return
     */
    private boolean kommentarLoeschen(HttpServletRequest request, HttpServletResponse response) {
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