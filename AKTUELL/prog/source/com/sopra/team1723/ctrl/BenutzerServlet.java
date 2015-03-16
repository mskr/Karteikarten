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
        String eMail = request.getParameter(requestEmail);
        String pass = request.getParameter(requestPassword);
        
        if(eMail == null || pass == null)
        {
            return false;
        }
        
        if(dbManager == null)
            return false;
        
        // Zugangsdaten richtig?
        if(dbManager.pruefeLogin(eMail, pass))
        {
            aktuelleSession.setAttribute(sessionAttributeEMail, eMail);
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
        
        if(action == null)
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        // Anfrage weiterleiten an verantwortliche Funktion
        if(action.equals(requestActionLogin))
        {
            if(login(req, resp))
            {
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
                outWriter.print(jo);
            }
            else
            {
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorLoginFailed);
                outWriter.print(jo);
            }
        }
        else if(action.equals(requestActionLogout))
        {
            if(logout(req, resp))
            {
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
                outWriter.print(jo);
            }
            else
            {
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorLogoutFailed);
                outWriter.print(jo);
            }
        }
        else if(action.equals(requestActionRegister))
        {
            if(registrieren(req, resp))
            {
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
                outWriter.print(jo);
            }
            else
            {
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorRegisterFailed);
                outWriter.print(jo);
            }
        }
        else if(action.equals(requestActionResetPasswort))
        {
            if(passwortReset(req, resp))
            {
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorPwResetFailed);
                outWriter.print(jo);
            }
            else
            {
                JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
                outWriter.print(jo);
            }
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
        }
        
    }

}