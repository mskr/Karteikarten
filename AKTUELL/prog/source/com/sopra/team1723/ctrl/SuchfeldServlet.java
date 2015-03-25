package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;







import com.sopra.team1723.data.*;


/**

 */
public class SuchfeldServlet extends ServletController {

     
    /**
     * 
     */
    public SuchfeldServlet() {
    }
    
    public void sucheBenutzerUndVeranstaltungen(HttpServletRequest req, HttpServletResponse resp){
        String suchmuster = req.getParameter(requestSuchmuster);
        
        ArrayList<Klassenfeld> zuDurchsuchendeFelder = new ArrayList<Klassenfeld>();
        zuDurchsuchendeFelder.add(new Klassenfeld("Benutzer","vorname"));
        zuDurchsuchendeFelder.add(new Klassenfeld("Benutzer","nachname"));
        zuDurchsuchendeFelder.add(new Klassenfeld("Veranstaltung","titel"));
        
        List<ErgebnisseSuchfeld> ergebnisse = dbManager.durchsucheDatenbank(suchmuster, zuDurchsuchendeFelder);
        
        JSONObject jo = JSONConverter.toJsonSuchfeld(ergebnisse);
        outWriter.print(jo);
    }
  
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);

        // Ist beim ServletController schon eine Antowrt zurückgegeben worden?
        if(!doProcessing())
            return;

        if(aktuelleAction.equals(requestActionSucheBenVeranst))
        {
            sucheBenutzerUndVeranstaltungen(req, resp);
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