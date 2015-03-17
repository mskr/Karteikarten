package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.*;
import com.sopra.team1723.data.*;


/**
 * Steuert den Login-/Logout- und den Registrierungsvorgang. Ausserdem steuert dieses Servlet den Reset des Passworts.
 */
@WebServlet("/BenutzerServlet")
public class BenutzerServlet extends ServletController {

    /**
     * Steuert den Login-/Logout- und den Registrierungsvorgang. Ausserdem steuert dieses Servlet den Reset des Passworts.
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
        String matrikelNrStr = request.getParameter(requestMatrikelNr);
        
        if(isEmpty(email) || isEmpty(passwort) || isEmpty(vorname)||isEmpty(nachname)
                || isEmpty(studienGang) || isEmpty(matrikelNrStr))
            return false;
        
        // Hier fliegt die Exception wenn der Nutzer keine Zahl als Matrikelnummer eingibt.
        int matrikelNr;
        try {
            matrikelNr = Integer.valueOf(matrikelNrStr);
        } catch(NumberFormatException e) {
            return false;
        }
        
        Benutzer nutzer = new Benutzer(email,
                vorname,
                nachname,
                matrikelNr,
                studienGang,
                passwort);
        
        return dbManager.schreibeBenutzer(nutzer);
    }

    /**
     * Aus der übergebenen Request werden die Parameter zum
     * zurücksetzen des Passworts herausgelesen. Anschließend wird ein
     * neues Passwort generiert. Dieses wird in die DB geschrieben. Gibt
     * "true" zurück, wenn bei diesem Ablauf keine Fehler auftreten, und
     * "false" bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean passwortReset(HttpServletRequest request, HttpServletResponse response) 
    {
        String eMail = request.getParameter(requestEmail);
        // TODO Reicht uns wirklich die eMail-Adresse?!
        if(eMail == null)
            return false;
        
        // TODO
        String generiertesPW = "1234";
        
        Benutzer user = dbManager.leseBenutzer(eMail);
        
        // Neues Passwort setzen
        user.setKennwort(generiertesPW);
        
        System.out.println("Update Benutzer("+ user.geteMail() +") mit geändertem Paswort (" + generiertesPW + ") in der DB.");
        // In Datenbank speichern
        if(!dbManager.bearbeiteBenutzer(user))
            return false;

        System.out.println("Sende Mail an Benutzer("+ user.geteMail() +") mit geändertem Paswort (" + generiertesPW + ").");
        // Sende Bestätigungs-EMail
        if(!sendePasswortResetMail(user.geteMail(), generiertesPW))
            return false;
        
        return true;
    }

    /**
     * Das in passwortReset(rq, resp) generierte Passwort, wird an
     * die Emailadresse des Benutzers, der sein Passwort zurücksetzen
     * möchte, gesendet. Gibt "true" zurück, wenn bei diesem Ablauf keine
     * Fehler auftreten, und "false" bei Fehlern.
     * @param generiertesPW 
     * @return
     */
    private boolean sendePasswortResetMail(String eMail, String generiertesPW) 
    {
        // Code von : http://www.tutorialspoint.com/java/java_sending_email.htm
        
        // Recipient's email ID needs to be mentioned.
        String to = eMail;

        // Sender's email ID needs to be mentioned
        String from = "noreply@team1723.com";

        // Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
           // Create a default MimeMessage object.
           MimeMessage message = new MimeMessage(session);

           // Set From: header field of the header.
           message.setFrom(new InternetAddress(from));

           // Set To: header field of the header.
           message.addRecipient(Message.RecipientType.TO,
                                    new InternetAddress(to));

           // Set Subject: header field
           message.setSubject("Ihr Passwort wurde zurückgesetzt!");

           // Send the actual HTML message, as big as you like
           message.setText("<h1>Passwort-Reset</h1> <p> Ihr neues Passwort lautet: \"" + generiertesPW + "\"</p>",
                              "text/html" );

           // Send message
           Transport.send(message);
           System.out.println("Reset-eMail gesendet an "+ eMail);
           
        }catch (MessagingException mex) 
        {
           mex.printStackTrace();
           System.err.println("Möglicherweise ist der Mail-Server aus oder Senden von localhost ist blockiert. (Mercury richtg konfigurieren.)");
           return false;
        }
        return true;
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