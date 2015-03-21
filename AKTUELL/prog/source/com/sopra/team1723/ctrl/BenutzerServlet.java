package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

import com.sopra.team1723.ctrl.*;
import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.DbFalseLoginDataException;
import com.sopra.team1723.exceptions.DbUniqueConstraintException;


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
        JSONObject jo = null;
        
        String email = request.getParameter(requestEmail);
        String passwort = request.getParameter(requestPassword);
        
        
        if(isEmptyAndRemoveSpaces(email) || isEmptyAndRemoveSpaces(passwort))
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorLoginFailed);
            outWriter.print(jo);
            return false;
        }
        
        // Schon eingeloggt ?  Dann ist alles gut
        if(pruefeLogin(aktuelleSession))
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        
        // Zugangsdaten richtig?
        try
        {
            dbManager.pruefeLogin(email, passwort);
            aktuelleSession.setAttribute(sessionAttributeEMail, email);
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
        }
        catch (DbFalseLoginDataException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorLoginFailed);
        }

        outWriter.print(jo);
        return true;
    }

    /**
     * Löscht die übergebene HttpSession via invalidate() und somit alle
     * temporär server-seitig gespeicherten Daten und Parameter. Gibt
     * "true" zurück, wenn bei diesem Ablauf keine Fehler auftreten, und
     * "false" bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean logout(HttpServletRequest request, HttpServletResponse response) 
    {
        JSONObject jo = null;
        
        // Noch eingeloggt?
        if(pruefeLogin(aktuelleSession))
        {
            // Session für ungültig erklären
            aktuelleSession.invalidate();
        }

        jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
        outWriter.print(jo);
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
    private boolean registrieren(HttpServletRequest request, HttpServletResponse response) 
    {
        JSONObject jo = null;
        
        String email = request.getParameter(requestEmail);
        String passwort = request.getParameter(requestPassword);
        String vorname = request.getParameter(requestVorname);
        String nachname = request.getParameter(requestNachname);
        String studienGang = request.getParameter(requestStudiengang);
        String matrikelNrStr = request.getParameter(requestMatrikelNr);
        
        if(isEmptyAndRemoveSpaces(email) || isEmptyAndRemoveSpaces(passwort) || isEmptyAndRemoveSpaces(vorname)||isEmptyAndRemoveSpaces(nachname)
                || isEmptyAndRemoveSpaces(studienGang) || isEmptyAndRemoveSpaces(matrikelNrStr))
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        
        // Remove HTML-Tags
        email = Jsoup.parse(email).text();
        //passwort = Jsoup.parse(passwort).text();
        vorname = Jsoup.parse(vorname).text();
        nachname = Jsoup.parse(nachname).text();
        studienGang = Jsoup.parse(studienGang).text();
        matrikelNrStr = Jsoup.parse(matrikelNrStr).text();
        
        // Hier fliegt die Exception wenn der Nutzer keine Zahl als Matrikelnummer eingibt.
        int matrikelNr;
        try {
            matrikelNr = Integer.valueOf(matrikelNrStr);
        } 
        catch(NumberFormatException e) 
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        
        Benutzer nutzer = new Benutzer(email,
                vorname,
                nachname,
                matrikelNr,
                studienGang,
                passwort);
        
        try
        {
            dbManager.schreibeBenutzer(nutzer);
        }
        catch (DbUniqueConstraintException e)
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorEmailAlreadyInUse);
            outWriter.print(jo);
            return false;
        }
        catch (SQLException e)
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorRegisterFailed);
            outWriter.print(jo);
            return false;
        }
        jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
        outWriter.print(jo);
        return true;
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
        // TODO
//        JSONObject jo = null;
//        String eMail = request.getParameter(requestEmail);
//        // TODO Reicht uns wirklich die eMail-Adresse?!
//        if(eMail == null)
//        {
//            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
//            outWriter.print(jo);
//            return false;
//        }
//        
//        // TODO
//        String generiertesPW = "1234";
//        
//        Benutzer user = dbManager.leseBenutzer(eMail);
//        String altesPasswort =  user.getKennwort();
//        
//        System.out.println("Update Benutzer("+ user.geteMail() +") mit geändertem Paswort (" + generiertesPW + ") in der DB.");
//        // In Datenbank speichern
//        if(!dbManager.passwortAendern(eMail, generiertesPW))
//        {
//            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorPwResetFailed);
//            outWriter.print(jo);
//            return false;
//        }
//
//        System.out.println("Sende Mail an Benutzer("+ user.geteMail() +") mit geändertem Paswort (" + generiertesPW + ").");
//        // Sende Bestätigungs-EMail
//        if(!sendePasswortResetMail(user.geteMail(), generiertesPW))
//        {
//            // Änderung rückgängig machen
//            user.setKennwort(altesPasswort);
//            dbManager.bearbeiteBenutzer(user);
//            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorPwResetFailed);
//            outWriter.print(jo);
//            return false;
//        }
//
//        jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
//        outWriter.print(jo);
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


    /**
     * Gibt alle gespeicherten Studiengänge zum Client zurück
     * @param request 
     * @param response 
     * @return
     */
    private boolean leseStudiengaenge(HttpServletRequest request, HttpServletResponse response) 
    {
        List<String> list = dbManager.leseStudiengaenge();
        if(list != null)
        {
            JSONObject jo = JSONConverter.toJson(list);
            outWriter.print(jo);
            return true;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
    }
  
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        // TODO hier ist die einzige Ausnahme wo mann nicht die überschriebe Funktion aufruft.
        // super.doPost(req, resp);
       
        // aktuelle Session holen
        aktuelleSession = req.getSession();
        outWriter = resp.getWriter();
        
        if(dbManager == null)
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }
        
        // Hole die vom client angefragte Aktion
        String action = req.getParameter(requestAction);
        
        if(isEmptyAndRemoveSpaces(action))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        System.out.println("Action: " + action);
        
        // Anfrage weiterleiten an verantwortliche Funktion               
        if(action.equals(requestActionLogin))
        {
            login(req, resp);
        }
        else if(action.equals(requestActionLogout))
        {
            logout(req, resp);
        }
        else if(action.equals(requestActionRegister))
        {
            registrieren(req, resp);
        }
        else if(action.equals(requestActionResetPasswort))
        {
            passwortReset(req, resp);
        }
        else if(action.equals(requestActionGetStudiengaenge))
        {
            leseStudiengaenge(req, resp);
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo  = null;
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            System.out.println("Antwort: " + jo.toJSONString());
            outWriter.print(jo);
        }
    }

}
