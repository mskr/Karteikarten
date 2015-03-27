package com.sopra.team1723.ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;

/**
 * Verwaltet die Verastaltungen
 */
public class VeranstaltungServlet extends ServletController {

    /**
     * Verwaltet die Verastaltungen
     */
    public VeranstaltungServlet() {
    }

    /**
     * Ein Dozent erstellt eine Veranstaltung und weist ihr verschiedene
     * Eigenschaften zu. Die Veranstaltung bekommt eine individuelle ID.
     * Auerdem wird gepruft ob es bereits eine Veranstaltung mit dem
     * selben gibt.
     * @param request 
     * @param response 
     * @return
     */
    private boolean veranstaltungErstellen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der VeranstaltungsID die Informationen
     * bezuglich der Veranstaltung gelesen und zuruckgegeben.
     * Falls es diese Veranstaltung in der Datenbank nicht gibt, gibt die
     * Methode false zuruck.
     * @param request 
     * @param response 
     * @return
     */
    private boolean veranstaltungenAnzeigen(HttpServletRequest request, HttpServletResponse response) 
    {
        JSONObject jo;
        String mode = request.getParameter(ParamDefines.LeseVeranstMode);
        if(isEmpty(mode))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        
        if(mode.equals(ParamDefines.LeseVeranstModeAlle))
        {
            // TODO Test
           List<Veranstaltung> verAnst = new ArrayList<Veranstaltung>();
           // TODO Vieles Klären!
           Veranstaltung s = new Veranstaltung("Informatik für Anfänger", "Dies ist ein Beispieltext", "Informatik", "WiSe 15", "1234", true, true, "Max Mustermann", new ArrayList<>(), true);
           Veranstaltung s2 = new Veranstaltung("Informatik für Anfänger", "Dies ist ein Beispieltext", "Informatik", "WiSe 15", "1234", true, true, "Karl-Heinz", new ArrayList<>(), true);
           Veranstaltung s3 = new Veranstaltung("Informatik für Anfänger", "Dies ist ein Beispieltext", "Informatik", "WiSe 15", "1234", true, true, "Peter Mayer", new ArrayList<>(), true);
           verAnst.add(s);
           verAnst.add(s2);
           verAnst.add(s3);
           jo = JSONConverter.toJsonVeranstList(verAnst);
           outWriter.print(jo);
           return true;
        }
        else if(mode.equals(ParamDefines.LeseVeranstModeMeine))
        {
            // TODO dbManager.leseVer
            
        }
        else if(mode.equals(ParamDefines.LeseVeranstModeSemester))
        {
            // TODO dbManager.leseVer
            
        }
        else if(mode.equals(ParamDefines.LeseVeranstModeStudiengang))
        {
            // TODO dbManager.leseVer
            
        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der VeranstaltungsID die Informationen
     * bezuglich der Veranstaltung gelesen und zuruckgegeben.
     * Der Benutzer kann diese nun andern und die geanderten Daten
     * werden in der Datenbank abgespeichert.
     * @param request 
     * @param response 
     * @return
     */
    private boolean veranstaltungBearbeiten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der VeranstaltungsID die
     * gewunschte Veranstaltung geloscht. Falls es diese Veranstaltung
     * nicht gibt, gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean veranstaltungLoeschen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Ein Benutzer kann sich in die gewunschte Veranstaltung einschreiben.
     * Seine ID wird dann in die betreende Datenbank bezuglich der
     * Veranstaltung geschrieben.Falls es diese Veranstaltung nicht gibt,
     * gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean veranstaltungEinschreiben(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Ein Benutzer kann sich in die gewunschte Veranstaltung ausschreiben.
     * Seine ID wird dann von der betreende Datenbank bezuglich
     * der Veranstaltung geloscht.Falls es diese Veranstaltung nicht gibt,
     * oder er noch nicht eingeschrieben ist,gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean veranstaltungAuschreiben(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	super.doPost(req, resp);
        
        // Ist beim ServletController schon eine Antowrt zurückgegeben worden?
        if(!doProcessing())
            return;
        
        if(aktuelleAction.equals(ParamDefines.ActionLeseVeranst))
        {
            veranstaltungenAnzeigen(req,resp);
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
    }
}