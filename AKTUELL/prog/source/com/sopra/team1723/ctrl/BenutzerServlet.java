package com.sopra.team1723.ctrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.*;
import com.sopra.team1723.data.*;

/**
 * Steuert den Login-/Logout- und den Registrierungsvorgang. AuÃŸerdem steuert dieses Servlet den Reset des Passworts.
 */
@WebServlet("/BenutzerServlet")
public class BenutzerServlet extends ServletController {

    /**
     * Steuert den Login-/Logout- und den Registrierungsvorgang. AuÃŸerdem steuert dieses Servlet den Reset des Passworts.
     */
    public BenutzerServlet() {
    }
    
    /**
     * Aus der üubergebenen Request werden folgende Emailadresse und
     * Passwort als Parameter ausgelesen. Dann werden sie an die Datenbank
     * weitergegeben. Wenn die Anmeldedaten korrekt sind, wird der
     * Benutzer aus der Datenbank mit der Emailadresse als Identifkator
     * gelesen. Lege neue Session an und schreibe die Benutzerattribute
     * hinein. Schreibt via Printwriter die Benutzerdaten als JSON in die
     * Response. Gibt "true" zurück, wenn bei diesem Ablauf keine Fehler
     * auftreten, und "false"bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean login(HttpServletRequest request, HttpServletResponse response) 
    {
        String email = request.getParameter(requestEmail);
        String passwort = request.getParameter(requestPassword);
        
        if(isEmpty(email) || isEmpty(passwort))
        {
            return false;
        }
        
        if(dbManager == null)
            return false;
        
        // Zugangsdaten richtig?
        if(dbManager.pruefeLogin(email, passwort))
        {
            aktuelleSession.setAttribute(sessionAttributeEMail, email);
            return true;
        }
        
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
        // Schon ausgeloggt?
        if(!pruefeLogin(aktuelleSession))
            return true;
        
        // Session für ungültig erklären
        aktuelleSession.invalidate();
        
         return true;
    }

    /**
     * Die Daten des Benutzers werden aus der Request gelesen. Diese
     * werden geprüuft und dann wird daraus ein Benutzerobjekt erschaffen.
     * Dieses wird an den Datenbankmanager weitergegeben, welcher
     * dann den Benutzer in der DB anlegt. Gibt "true" zurüuck, wenn bei
     * diesem Ablauf keine Fehler auftreten, und "false" bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean registrieren(HttpServletRequest request, HttpServletResponse response) {
        
        String email = request.getParameter(requestEmail);
        String passwort = request.getParameter(requestPassword);
        String vorname = request.getParameter(requestVorname);
        String nachname = request.getParameter(requestNachname);
        String studienGang = request.getParameter(requestStudiengang);
        String martikelNrStr = request.getParameter(requestMatrikelNr);
        
        if(isEmpty(email) || isEmpty(passwort) || isEmpty(vorname)||isEmpty(nachname)
                || isEmpty(studienGang) || isEmpty(martikelNrStr))
            return false;
        
        int matrikelNr = Integer.valueOf(martikelNrStr);
        
        Benutzer nutzer = new Benutzer(email,
                vorname,
                nachname,
                matrikelNr,
                studienGang,
                passwort);
        
        return dbManager.schreibeBenutzer(nutzer);
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
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO hier ist die einzige Ausnahme wo mann nicht die überschriebe Funktion aufruft.
        // super.doPost(req, resp);
        
        // aktuelle Session holen
        aktuelleSession = req.getSession();
        outWriter = resp.getWriter();
        
        // Hole die vom client angefragte Aktion
        String action = req.getParameter(requestAction);
        
        if(isEmpty(action))
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        System.out.println("Action: " + action);
        
        // Anfrage weiterleiten an verantwortliche Funktion
        JSONObject jo  = null;

        System.out.println(JSONConverter.jsonErrorNoError);
        
        if(action.equals(requestActionLogin))
        {
            if(login(req, resp))
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
            }
            else
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorLoginFailed);
            }
        }
        else if(action.equals(requestActionLogout))
        {
            if(logout(req, resp))
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
            }
            else
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorLogoutFailed);
            }
        }
        else if(action.equals(requestActionRegister))
        {
            if(registrieren(req, resp))
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
            }
            else
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorRegisterFailed);
            }
        }
        else if(action.equals(requestActionResetPasswort))
        {
            if(passwortReset(req, resp))
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorPwResetFailed);
            }
            else
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
            }
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
        }
        outWriter.print(jo);
        
    }

}