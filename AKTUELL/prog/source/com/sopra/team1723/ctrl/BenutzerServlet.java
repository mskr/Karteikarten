package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
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
     * @ParamDefines. request 
     * @ParamDefines. response 
     * @return
     * @throws IOException 
     */
    private boolean login(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        HttpSession aktuelleSession = request.getSession();
        String aktuelleAction = (String) aktuelleSession.getAttribute(sessionAttributeaktuelleAction);
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        JSONObject jo = null;
        
        String email = request.getParameter(ParamDefines.Email);
        String passwort = request.getParameter(ParamDefines.Password);
        
        
        if(isEmptyAndRemoveSpaces(email) || isEmptyAndRemoveSpaces(passwort))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorLoginFailed);
            outWriter.print(jo);
            return false;
        }
        
        // Schon eingeloggt ?  Dann ist alles gut
        if(pruefeLogin(aktuelleSession))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return true;
        }
        
        // Zugangsdaten richtig?
        try
        {
            dbManager.pruefeLogin(email, passwort);
            aktuellerBenutzer = dbManager.leseBenutzer(email);
            if(aktuellerBenutzer != null)
            {
                aktuelleSession.setAttribute(sessionAttributeUserID, aktuellerBenutzer.getId());
                aktuelleSession.setAttribute(sessionAttributeGewähltesSemester, leseAktuellesSemester());
                aktuelleSession.setAttribute(sessionAttributeaktuellerBenutzer, aktuellerBenutzer);
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            }
            else{
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
        }
        catch (DbFalseLoginDataException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorLoginFailed);
        }

        outWriter.print(jo);
        return true;
    }

    /**
     * Löscht die übergebene HttpSession via invalidate() und somit alle
     * temporär server-seitig gespeicherten Daten und Parameter. Gibt
     * "true" zurück, wenn bei diesem Ablauf keine Fehler auftreten, und
     * "false" bei Fehlern.
     * @ParamDefines. request 
     * @ParamDefines. response 
     * @return
     * @throws IOException 
     */
    private boolean logout(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        HttpSession aktuelleSession = request.getSession();
        String aktuelleAction = (String) aktuelleSession.getAttribute(sessionAttributeaktuelleAction);
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        JSONObject jo = null;
        
        // Noch eingeloggt?
        if(pruefeLogin(aktuelleSession))
        {
            // Session für ungültig erklären
            aktuelleSession.invalidate();
        }

        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
        outWriter.print(jo);
         return true;
    }

    /**
     * Die Daten des Benutzers werden aus der Request gelesen. Diese
     * werden geprüuft und dann wird daraus ein Benutzerobjekt erschaffen.
     * Dieses wird an den Datenbankmanager weitergegeben, welcher
     * dann den Benutzer in der DB anlegt. Gibt "true" zurüuck, wenn bei
     * diesem Ablauf keine Fehler auftreten, und "false" bei Fehlern.
     * @ParamDefines. request 
     * @ParamDefines. response 
     * @return
     * @throws IOException 
     */
    private boolean registrieren(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {

        HttpSession s = request.getSession();
        String aktuelleAction = (String) s.getAttribute(sessionAttributeaktuelleAction);
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) s.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) s.getAttribute(sessionAttributeDbManager);
        
        JSONObject jo = null;
        
        String email = request.getParameter(ParamDefines.Email);
        String passwort = request.getParameter(ParamDefines.Password);
        String vorname = request.getParameter(ParamDefines.Vorname);
        String nachname = request.getParameter(ParamDefines.Nachname);
        String studienGang = request.getParameter(ParamDefines.Studiengang);
        String matrikelNrStr = request.getParameter(ParamDefines.MatrikelNr);
        
        if(isEmptyAndRemoveSpaces(email) || isEmpty(passwort) || isEmptyAndRemoveSpaces(vorname)||isEmptyAndRemoveSpaces(nachname)
                || isEmptyAndRemoveSpaces(studienGang) || isEmptyAndRemoveSpaces(matrikelNrStr))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
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
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
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
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorEmailAlreadyInUse);
            outWriter.print(jo);
            return false;
        }
        catch (SQLException e)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
        outWriter.print(jo);
        return true;
    }

    /**
     * Aus der übergebenen Request werden die Parameter zum
     * zurücksetzen des Passworts herausgelesen. Anschließend wird ein
     * neues Passwort generiert. Dieses wird in die DB geschrieben. Gibt
     * "true" zurück, wenn bei diesem Ablauf keine Fehler auftreten, und
     * "false" bei Fehlern.
     * @ParamDefines. request 
     * @ParamDefines. response 
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
//            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
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
//            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorPwResetFailed);
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
//            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorPwResetFailed);
//            outWriter.print(jo);
//            return false;
//        }
//
//        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
//        outWriter.print(jo);
        return true;

    }

    /**
     * Das in passwortReset(rq, resp) generierte Passwort, wird an
     * die Emailadresse des Benutzers, der sein Passwort zurücksetzen
     * möchte, gesendet. Gibt "true" zurück, wenn bei diesem Ablauf keine
     * Fehler auftreten, und "false" bei Fehlern.
     * @ParamDefines. generiertesPW 
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
     * @ParamDefines. request 
     * @ParamDefines. response 
     * @return
     * @throws IOException 
     */
    private boolean leseStudiengaenge(HttpServletRequest request, HttpServletResponse response) throws IOException 
    {
        IDatenbankmanager dbManager = (IDatenbankmanager) request.getSession().getAttribute(sessionAttributeDbManager);
        PrintWriter outWriter = null;
        outWriter = response.getWriter();
        
        List<String> list = dbManager.leseStudiengaenge();
        if(list != null)
        {
            JSONObject jo = JSONConverter.toJson(list);
            outWriter.print(jo);
            return true;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
    }
  
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        // aktuelle Session holen
        HttpSession aktuelleSession = req.getSession();
        resp.setContentType("text/json");
        PrintWriter outWriter = resp.getWriter();

        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        if(dbManager == null)
        {
            try
            {
                dbManager = new Datenbankmanager();
                aktuelleSession.setAttribute(sessionAttributeDbManager, dbManager);
            }
            catch (Exception e)
            {
                dbManager = null;
                System.err.println("Es Konnte keine Verbindung zur Datenbank hergestellt werden oder ein unerwarteter Fehler ist aufgetreten!");
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return;
            }
        }
        
        // Hole die vom client angefragte Aktion
        String action = req.getParameter(ParamDefines.Action);
        
        if(isEmptyAndRemoveSpaces(action))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        // Anfrage weiterleiten an verantwortliche Funktion               
        if(action.equals(ParamDefines.ActionLogin))
        {
            login(req, resp);
        }
        else if(action.equals(ParamDefines.ActionLogout))
        {
            logout(req, resp);
        }
        else if(action.equals(ParamDefines.ActionRegister))
        {
            registrieren(req, resp);
        }
        else if(action.equals(ParamDefines.ActionResetPasswort))
        {
            passwortReset(req, resp);
        }
        else if(action.equals(ParamDefines.ActionGetStudiengaenge))
        {
            leseStudiengaenge(req, resp);
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo  = null;
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            System.out.println("Antwort: " + jo.toJSONString());
            outWriter.print(jo);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException
    {}

}
