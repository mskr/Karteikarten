package com.sopra.team1723.ctrl;

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

}