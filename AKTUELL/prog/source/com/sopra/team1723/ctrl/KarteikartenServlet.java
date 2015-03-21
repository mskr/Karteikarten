package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.sopra.team1723.data.*;

/**
 * Verwaltet die Karteikarten
 */
public class KarteikartenServlet extends ServletController {

    /**
     * Verwaltet die Karteikarten
     */
    public KarteikartenServlet() {
    }

    /**
     * Ein Benutzer erstellt eine Karteikarte und weist ihr verschiedene
     * Eigenschaften zu. Die Karteikarte bekommt eine individuelle ID.
     * Auerdem wird gepruft ob es bereits eine Karteikarte mit dem selben
     * Inhalt gibt.
     * @param request 
     * @param response 
     * @return
     */
    private boolean karteikarteErstellen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der KarteikartenID die Informationen
     * bezuglich der Karteikarte gelesen und zuruckgegeben. Falls
     * es diese Karteikarte in der Datenbank nicht gibt, gibt die Methode
     * false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean karteikartenAnzeigen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird die gewunschte Karteikarte gelesen. Der
     * Benutzer kann diese nun mit einer Zahl bewerten. die Bewertung
     * wird dann in der Datenbank gespeichert.Falls es diese Karteikarte
     * in der Datenbank nicht gibt, gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean karteikarteBewerten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der KarteikartenID die Informationen
     * bezuglich der Karteikarte gelesen und zuruckgegeben. Der
     * Benutzer kann diese nun andern und die geanderten Daten werden
     * in der Datenbank abgespeichert.Falls es diese Karteikarte in der
     * Datenbank nicht gibt, gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean karteikarteBearbeiten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der KarteikartenID die
     * gewunschte Karteikarte geloscht. Falls es diese Karteikarte nicht
     * gibt, gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean karteikarteLoeschen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * @param request 
     * @param response 
     * @return
     */
    private boolean exportiereKarteikarten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * @param vater 
     * @param dateiname 
     * @param bilder 
     * @param notizen 
     * @param kommentare 
     * @param querverweise 
     * @return
     */
    private File exportiereKarteikarten(Karteikarte vater, String dateiname, boolean bilder, boolean notizen, boolean kommentare, boolean querverweise) {
        // TODO implement here
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	super.doPost(req, resp);
        
        // Ist beim ServletController schon eine Antowrt zurückgegeben worden?
        if(!doProcessing())
            return;
        
    }

}