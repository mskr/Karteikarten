package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.sql.SQLException;

import javax.crypto.AEADBadTagException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

import com.sopra.team1723.data.*;
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

        String email = request.getParameter(requestEmail);
        String vorname = request.getParameter(requestVorname);
        String nachname = request.getParameter(requestNachname);
        String notifyVeranstAenderung = request.getParameter(requestNotifyVeranstAenderung);
        String notifyKarteikartenAenderung = request.getParameter(requestNotifyKarteikartenAenderung);
        String notifyKommentare = request.getParameter(requestNotifyKommentare);

        JSONObject jo = null;
        // Alle Parameter angegeben?
        if(isEmptyAndRemoveSpaces(email) || isEmptyAndRemoveSpaces(vorname)||isEmptyAndRemoveSpaces(nachname)
                || isEmptyAndRemoveSpaces(notifyVeranstAenderung) || isEmptyAndRemoveSpaces(notifyKarteikartenAenderung)
                || isEmptyAndRemoveSpaces(notifyKommentare))
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        email = Jsoup.parse(email).text();
        vorname = Jsoup.parse(vorname).text();
        nachname = Jsoup.parse(nachname).text();
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
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
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
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
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
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }

        Benutzer benutzer;
        // Sowohl wenn der Admin ein Profil ändert, als auch der Benutzer sein eigenes, müssen diese Exceptions abgefangen werden
        try{

            // Prüfen ob dies ein Admin ist und gegebenfalls alle Parameter holen
            if(aktuellerBenutzer.getNutzerstatus()==Nutzerstatus.ADMIN)
            {
                String studiengang = request.getParameter(requestStudiengang);
                String matrikelNrStr = request.getParameter(requestMatrikelNr);
                String nutzerstatus = request.getParameter(requestNutzerstatus);

                // Alle Parameter angegeben?
                if(isEmptyAndRemoveSpaces(studiengang) || isEmptyAndRemoveSpaces(matrikelNrStr)||isEmptyAndRemoveSpaces(nutzerstatus))
                {
                    jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
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
                    jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
                    outWriter.print(jo);
                    return false;
                }

                benutzer = new Benutzer(email, vorname, nachname, nMatrikelNr, studiengang, eNutzerstatus, 
                        bNotifyVeranstAenderung, bNotifyKarteikartenAenderung, eNotifyKommentare);
                String alteMail = aktuelleSession.getAttribute(sessionAttributeEMail).toString();
                if(alteMail == null){
                    jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
                    outWriter.print(jo);
                    return false;
                }
                dbManager.bearbeiteBenutzer(alteMail, benutzer);
            }
            // Normaler benutzer Speichern
            else
            {
                benutzer = new Benutzer(email, vorname, nachname, bNotifyVeranstAenderung, 
                        bNotifyKarteikartenAenderung, eNotifyKommentare);
                dbManager.bearbeiteBenutzer(aktuelleSession.getAttribute(sessionAttributeEMail).toString(), benutzer);
            }
        }
        catch(SQLException e)
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        } 
        catch(DbUniqueConstraintException e){
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorEmailAlreadyInUse);
            outWriter.print(jo);
            return false;
        }
        
        jo = JSONConverter.toJsonError(JSONConverter.jsonErrorNoError);
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
        String neuesPasswort = request.getParameter(requestPassword);
        JSONObject jo = null;
        if(neuesPasswort.contains(" ") || isEmptyAndRemoveSpaces(neuesPasswort))
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        String benutzerMail = aktuelleSession.getAttribute(sessionAttributeEMail).toString();
        if(benutzerMail == null){
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
            outWriter.print(jo);
            return false;
        }
        if(dbManager.passwortAendern(benutzerMail, neuesPasswort))
            return true;
        else
            return false;
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

        if(aktuelleAction.equals(requestActionGetBenutzer))
        {
            JSONObject jo = JSONConverter.toJson(aktuellerBenutzer,true);
            outWriter.print(jo);
            return;
        }
        else if(aktuelleAction.equals(requestActionGetOtherBenutzer))
        {
            String eMail = req.getParameter(requestEmail);
            Benutzer b = dbManager.leseBenutzer(eMail);
            if(b == null)
            {
                JSONObject jo  = null;
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
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
        else if(aktuelleAction.equals(requestActionAendereProfil))
        {
            profilBearbeiten(req,resp);
        }
        else if(aktuelleAction.equals(requestActionAenderePasswort))
        {
            passwortAendern(req, resp);
        }
        else
        {
            // Sende Nack mit ErrorText zurück
            JSONObject jo  = null;
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
        }
    }
}