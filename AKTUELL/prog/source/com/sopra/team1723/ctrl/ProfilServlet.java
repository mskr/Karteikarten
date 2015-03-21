package com.sopra.team1723.ctrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;

/**
 * Steuert das anzeigen und bearbeiten eines Profils.
 */
@WebServlet("/ProfilServlet")
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
        try
        {
            NotifyKommentare eNotifyKommentare = NotifyKommentare.valueOf(notifyKommentare);
        } catch (IllegalArgumentException e) 
        {
            jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        
        // Prüfen ob dies ein Admin ist und gegebenfalls alle Parameter holen
        if(aktuellerBenutzer.getNutzerstatus()==Nutzerstatus.ADMIN)
        {
            String studienGang = request.getParameter(requestStudiengang);
            String matrikelNrStr = request.getParameter(requestMatrikelNr);
            String nutzerstatus = request.getParameter(requestNutzerstatus);
            
            // Alle Parameter angegeben?
            if(isEmptyAndRemoveSpaces(studienGang) || isEmptyAndRemoveSpaces(matrikelNrStr)||isEmptyAndRemoveSpaces(nutzerstatus))
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
                outWriter.print(jo);
                return false;
            }
            // Matrikelnummer konvertieren
            int nMatrikelNr = 0;
            try
            {
                nMatrikelNr = Integer.parseInt(matrikelNrStr);
            }
            catch (NumberFormatException e)
            {
                jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
                outWriter.print(jo);
                return false;
            }
            
            //Benutzer user = new Benutzer(email,vorname,nachname,nMatrikelNr,studienGang,aktuellerBenutzer);
            
            
            // Wenn ja, dann neues Benutzerobjekt speichern
        }
        // Normaler benutzer Speichern
        else
        {
            
        }
        
        
        
        return true;
    }

    /**
     * Löschen? siehe Benutzerservlet selbe Methode.
     * @param request 
     * @param response 
     * @return
     */
    private boolean passwortAendern(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
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
            JSONObject jo = JSONConverter.toJson(aktuellerBenutzer);
            outWriter.print(jo);
            return;
        }
    	else if(aktuelleAction.equals(requestActionAendereProfil))
        {
    	    profilBearbeiten(req,resp);
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