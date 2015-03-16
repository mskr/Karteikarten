package com.sopra.team1723.ctrl;

import java.util.*;

/**
 * Abstrakte Oberklasse, die die Login-Überprüfung übernimmt und gegebenenfalls an das Benutzer-Servlet weiterleitet oder einen Fehler an den Aufrufer zurückgibt.
 */
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
    private Benutzer leseBenutzer(Session session) {
        // TODO implement here
        return null;
    }

}