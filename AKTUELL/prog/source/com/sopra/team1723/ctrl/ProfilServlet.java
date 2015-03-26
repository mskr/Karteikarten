package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.crypto.AEADBadTagException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

import com.sopra.team1723.data.*;
import com.sopra.team1723.exceptions.DbFalseLoginDataException;
import com.sopra.team1723.exceptions.DbUniqueConstraintException;

/**
 * Steuert das anzeigen und bearbeiten eines Profils.
 */
public class ProfilServlet extends ServletController {

    /**
     * Steuert das anzeigen und bearbeiten eines Profils.
     */
    public ProfilServlet() {
    }
    /**
     * Aus der Request werden die neuen Proldaten ausgelesen. Diese
     * werden gepruft und wenn diese Prufung erfolgreich war, werden
     * sie uber den Datenbankmanager in der DB geupdated. Gibt "true-
     * uruck, wenn bei diesem Ablauf keine Fehler auftreten, und "false"
     * bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean profilBearbeiten(HttpServletRequest request, HttpServletResponse response) {

        String email = request.getParameter(ParamDefines.Email);
        String emailNew = request.getParameter(ParamDefines.EmailNew);
        String vorname = request.getParameter(ParamDefines.Vorname);
        String nachname = request.getParameter(ParamDefines.Nachname);
        String notifyVeranstAenderung = request.getParameter(ParamDefines.NotifyVeranstAenderung);
        String notifyKarteikartenAenderung = request.getParameter(ParamDefines.NotifyKarteikartenAenderung);
        String notifyKommentare = request.getParameter(ParamDefines.NotifyKommentare);

        JSONObject jo = null;
        // Alle Parameter angegeben?
        if(isEmptyAndRemoveSpaces(email) || isEmptyAndRemoveSpaces(emailNew) || isEmptyAndRemoveSpaces(vorname)||isEmptyAndRemoveSpaces(nachname)
                || isEmptyAndRemoveSpaces(notifyVeranstAenderung) || isEmptyAndRemoveSpaces(notifyKarteikartenAenderung)
                || isEmptyAndRemoveSpaces(notifyKommentare))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        // HTML entfernen!
        email = Jsoup.parse(email).text();
        vorname = Jsoup.parse(vorname).text();
        nachname = Jsoup.parse(nachname).text();
        
        // Eigene eMail oder andere ?
        if(!email.equalsIgnoreCase(aktuellerBenutzer.geteMail()))
        {
            // Andere eMail. Bin ich also Admin?
            if(aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
                return false;
            }
        }
        
        // Boolean konvertieren
        boolean bNotifyVeranstAenderung = false;
        if(notifyVeranstAenderung.equals("true"))
        {
            bNotifyVeranstAenderung = true;
        }
        else if(notifyVeranstAenderung.equals("false"))
        {
            bNotifyVeranstAenderung = false;
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        boolean bNotifyKarteikartenAenderung = false;
        if(notifyKarteikartenAenderung.equals("true"))
        {
            bNotifyKarteikartenAenderung = true;
        }
        else if(notifyKarteikartenAenderung.equals("false"))
        {
            bNotifyKarteikartenAenderung = false;
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        // Enum konvertieren
        NotifyKommentare eNotifyKommentare;
        try
        {
            eNotifyKommentare = NotifyKommentare.valueOf(notifyKommentare);
        } 
        catch (IllegalArgumentException e) 
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        Benutzer benutzer;
        // Sowohl wenn der Admin ein Profil ändert, als auch der Benutzer sein eigenes, müssen diese Exceptions abgefangen werden
        try{

            // Prüfen ob dies ein Admin ist und gegebenfalls alle Parameter holen
            if(aktuellerBenutzer.getNutzerstatus()==Nutzerstatus.ADMIN)
            {
                String studiengang = request.getParameter(ParamDefines.Studiengang);
                String matrikelNrStr = request.getParameter(ParamDefines.MatrikelNr);
                String nutzerstatus = request.getParameter(ParamDefines.Nutzerstatus);

                // Alle Parameter angegeben?
                if(isEmptyAndRemoveSpaces(studiengang) || isEmptyAndRemoveSpaces(matrikelNrStr) || isEmptyAndRemoveSpaces(nutzerstatus))
                {
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return false;
                }
                studiengang = Jsoup.parse(studiengang).text();
                matrikelNrStr = Jsoup.parse(matrikelNrStr).text();
                
                // Matrikelnummer konvertieren
                int nMatrikelNr = 0;
                Nutzerstatus eNutzerstatus;
                try
                {
                    nMatrikelNr = Integer.parseInt(matrikelNrStr);
                    eNutzerstatus = Nutzerstatus.valueOf(nutzerstatus);
                }
                catch (IllegalArgumentException e)
                {
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return false;
                }
                
                benutzer = new Benutzer(emailNew, vorname, nachname, nMatrikelNr, studiengang, eNutzerstatus, 
                        bNotifyVeranstAenderung, bNotifyKarteikartenAenderung, eNotifyKommentare);
                
                dbManager.bearbeiteBenutzerAdmin(email, benutzer);
            }
            // Normaler benutzer Speichern
            else
            {
                benutzer = new Benutzer(emailNew, vorname, nachname, bNotifyVeranstAenderung, 
                        bNotifyKarteikartenAenderung, eNotifyKommentare);
                dbManager.bearbeiteBenutzer(email, benutzer);
            }
        }
        catch(SQLException e)
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        } 
        catch(DbUniqueConstraintException e){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorEmailAlreadyInUse);
            outWriter.print(jo);
            return false;
        }
        
        jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
        outWriter.print(jo);


        return true;
    }

    /**
     * Löschen? siehe Benutzerservlet selbe Methode.
     * @param request 
     * @param response 
     * @return
     */
    private boolean passwortAendern(HttpServletRequest request, HttpServletResponse response) {
        String neuesPasswort = request.getParameter(ParamDefines.PasswordNew);
        String altesPasswort = request.getParameter(ParamDefines.Password);
        // Nutze diese Methode auch, wenn ein Admin die Email eines anderen Benutzers aendert
        String benutzerMail = request.getParameter(ParamDefines.Email);
        
        JSONObject jo = null;
        
        try
        {
            // Eigenes Profil oder nicht ? 
            if(!benutzerMail.equalsIgnoreCase(aktuellerBenutzer.geteMail()))
            {
                // Nicht eigenes Profil. Ist der Benuzer als Admin ?
                if(aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
                {
                    // wenn nein, dann error
                    jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return false;
                }
            }
            // Eigenes Profil
            else
            {
                // Nochmal mit richtiger groß und kleinschreibung holen
                benutzerMail = aktuellerBenutzer.geteMail();
                // Prüfen ob altes Passwort richtig
                dbManager.pruefeLogin(benutzerMail, altesPasswort);
            }
            // neues Passwort angegeben ?
            if(isEmpty(neuesPasswort))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
                return false;
            }
            // TODO Vllt weglassen
            if(benutzerMail == null)
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return false;
            }
            // Passwort ändern
            if(dbManager.passwortAendern(benutzerMail, neuesPasswort))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
                outWriter.print(jo);
                return false;
            }
            else
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return false;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
        catch (DbFalseLoginDataException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorLoginFailed);
            outWriter.print(jo);
            return false;
        }
            
        
    }

    /**
     * Liest aus der Request ID und Verifikationsparameter für diese
     * Handlung aus. Wenn Benutzer existiert, rufe Methode zum Lüschen
     * von Benutzern des Datenbankmanagers auf. Gibt "true" zurück,
     * wenn bei diesem Ablauf keine Fehler auftreten, und "false"bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean benutzerLoeschen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);

        // Ist beim ServletController schon eine Antowrt zurückgegeben worden?
        if(!doProcessing())
            return;

        if(aktuelleAction.equals(ParamDefines.ActionGetBenutzer))
        {
            JSONObject jo = JSONConverter.toJson(aktuellerBenutzer,true);
            outWriter.print(jo);
            return;
        }
        else if(aktuelleAction.equals(ParamDefines.ActionGetOtherBenutzer))
        {
            String eMail = req.getParameter(ParamDefines.Email);
            Benutzer b = dbManager.leseBenutzer(eMail);

            
            if(b == null)
            {
                JSONObject jo  = null;
                // TODO Evntl detailliertere Fehlermeldung, z.B. userNotFound einfuehren?
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
                return;
            }
            else
            {
                // TODO Testen!
                JSONObject jo = JSONConverter.toJson(b,aktuellerBenutzer.getNutzerstatus() == Nutzerstatus.ADMIN);
                outWriter.print(jo);
                return;
            }
        }
        else if(aktuelleAction.equals(ParamDefines.ActionAendereProfil))
        {
            profilBearbeiten(req,resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionAenderePasswort))
        {
            passwortAendern(req, resp);
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo  = null;
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
        }
    }
}