package com.sopra.team1723.ctrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.sopra.team1723.data.*;

/**
 * Verwaltet die Notizen
 */
@WebServlet("/BenutzerServlet")
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	super.doPost(req, resp);
    }
}