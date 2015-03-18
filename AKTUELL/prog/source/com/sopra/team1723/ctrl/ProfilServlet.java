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
     * Liest aus der Request die ID des Benutzer aus und schickt damit
     * eine Anfrage an den Datenbankmanager, der die Profildaten aus
     * der DB liest. Diese Methode gibt dann via Printwriter die Proldatenpackage com.sopra.team1723.ctrl;

     * aus. Gibt "trueuruck, wenn bei diesem Ablauf keine Fehler
     * auftreten, und "false"bei Fehlern.
     * @param request 
     * @param response 
     * @return
     */
    private boolean profilAnzeigen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
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
     * Loschen? siehe Benutzerservlet selbe Methode.
     * @param request 
     * @param response 
     * @return
     */
    private boolean passwortAendern(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Liest aus der Request ID und Verikationsparameter fur diese
     * Handlung aus.Wenn Benutzer existiert, rufe Methode zum Loschen
     * von Benutzern des Datenbankmanagers auf. Gibt "trueuruck,
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	super.doPost(req, resp);
    	
    	// Ist beim ServletController schon eine Antowrt zurückgegeben worden?
    	if(!doProcessing())
    	    return;
    	
    	if(aktuelleAction.equals(requestActionGetBenutzer))
        {
    	    JSONObject jo = JSONConverter.toJsonBenutzer(aktuellerBenutzer);
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