package com.sopra.team1723.ctrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.sopra.team1723.data.*;

/**
 * Abstrakte Oberklasse, die die Login-Überprüfung übernimmt und gegebenenfalls an das Benutzer-Servlet weiterleitet oder einen Fehler an den Aufrufer zurückgibt.
 */
@WebServlet("/ServletController")
public class ServletController extends HttpServlet {

    /**
     * Abstrakte Oberklasse, die die Login-Überprüfung übernimmt und gegebenenfalls an das Benutzer-Servlet weiterleitet oder einen Fehler an den Aufrufer zurückgibt.
     */
    public ServletController() {
    }

    /**
     * Aktuell angemeldeter Benutzer. Null, falls Benutzer nicht angemeldet ist.
     */
    protected Benutzer aktuellerBenutzer;

    /**
     * 
     */
    private IDatenbankmanager dbManager;

    /**
     * Prüft, ob die eMail-Adresse des Benutzers in der aktuellen Session vorhanden ist und ob der Benutzer somit eingeloggt ist.
     * @return
     */
    private boolean pruefeLogin() {
        // TODO implement here
        return false;
    }

    /**
     * liest das Benutzerobjekt aus der Session aus.
     * @param session 
     * @return
     */
    private Benutzer leseBenutzer(HttpSession session) {
        // TODO implement here
        return null;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    }
    
}