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
        // TODO implement here
        return false;
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