package com.sopra.team1723.ctrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.sopra.team1723.ctrl.*;
import com.sopra.team1723.data.*;

/**
 * Steuert den Login-/Logout- und den Registrierungsvorgang. Außerdem steuert dieses Servlet den Reset des Passworts.
 */
@WebServlet("/BenutzerServlet")
public class BenutzerServlet extends HttpServlet {

    /**
     * Steuert den Login-/Logout- und den Registrierungsvorgang. Außerdem steuert dieses Servlet den Reset des Passworts.
     */
    public BenutzerServlet() {
    }

    /**
     * 
     */
    private IDatenbankmanager dbManager;

    /**
     * Aus der ubergebenen Request werden folgende Emailadresse und
     * Passwort als Parameter ausgelesen. Dann werden sie an die Datenbank
     * weitergegeben.Wenn die Anmeldedaten korrekt sind, wird der
     * Benutzer aus der Datenbank mit der Emailadresse als Identikator
     * gelesen. Lege neue Session an und schreibe die Benutzerattribute
     * hinein. Schreibt via Printwriter die Benutzerdaten als JSON in die
     * Response. Gibt "trueuruck, wenn bei diesem Ablauf keine Fehler
     * auftreten, und "false"bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean login(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Loscht die ubergebene HttpSession via invalidate() und somit alle
     * temporar server-seitig gespeicherten Daten und Parameter.Gibt
     * "trueuruck, wenn bei diesem Ablauf keine Fehler auftreten, und
     * "false"bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean logout(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Die Daten des Benutzers werden aus der Request gelesen. Diese
     * werden gepruft und dann wird daraus ein Benutzerobjekt erschaffen.
     * Dieses wird an den Datenbankmanager weitergegeben, welcher
     * dann den Benutzer in der DB anlegt. Gibt "trueuruck, wenn bei
     * diesem Ablauf keine Fehler auftreten, und "false"bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean registrieren(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der ubergebenen Request werden die Parameter zum
     * zurucksetzen des Passworts herausgelesen. Anschlieend wird ein
     * neues Passwort generiert. Dieses wird in die DB geschrieben. Gibt
     * "trueuruck, wenn bei diesem Ablauf keine Fehler auftreten, und
     * "false"bei Fehlern
     * @param request 
     * @param response 
     * @return
     */
    private boolean passwortReset(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Uberpruft anhand der ubergebenen Session, ob ein Nutzer bereits
     * eingeloggt ist oder nicht. Gibt "true" zuruck, wenn die Session diverse
     * Parameter beinhaltet und wenn nicht "false".
     * @param session 
     * @param response 
     * @return
     */
    private boolean istEingeloggt(HttpSession session, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Das in passwortReset(rq, resp) generierte Passwort, wird an
     * die Emailadresse des Benutzers, der sein Passwort zurucksetzen
     * mochte, gesendet. Gibt "trueuruck, wenn bei diesem Ablauf keine
     * Fehler auftreten, und "false"bei Fehlern.
     * @param generiertesPW 
     * @return
     */
    private boolean sendePasswortResetMail(String generiertesPW) {
        // TODO implement here
        return false;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	super.doPost(req, resp);
    }

}