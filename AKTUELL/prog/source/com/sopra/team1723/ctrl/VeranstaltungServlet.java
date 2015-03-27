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
            List<Veranstaltung> verAnst = new ArrayList<Veranstaltung>();
            verAnst = dbManager.leseAlleVeranstaltungen();
            if(verAnst != null)
                jo = JSONConverter.toJsonVeranstList(verAnst);
            else
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return true;
        }
        else if(mode.equals(ParamDefines.LeseVeranstModeMeine))
        {
            List<Veranstaltung> verAnst = new ArrayList<Veranstaltung>();
            verAnst = dbManager.leseVeranstaltungen(aktuellerBenutzer.getId());
            if(verAnst != null)
                jo = JSONConverter.toJsonVeranstList(verAnst);
            else
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return true;

        }
        else if(mode.equals(ParamDefines.LeseVeranstModeSemester))
        {
            List<Veranstaltung> verAnst = new ArrayList<Veranstaltung>();
            //TODO Wie speichern wir das aktuelle Semester?
            verAnst = dbManager.leseVeranstaltungenSemester("SoSe2015");
            if(verAnst != null)
                jo = JSONConverter.toJsonVeranstList(verAnst);
            else
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return true;

        }
        else if(mode.equals(ParamDefines.LeseVeranstModeStudiengang))
        {
            List<Veranstaltung> verAnst = new ArrayList<Veranstaltung>();
            verAnst = dbManager.leseVeranstaltungenStudiengang(dbManager.leseBenutzer(request.getSession().getAttribute
                    (sessionAttributeEMail).toString()).getStudiengang());
            if(verAnst != null)
                jo = JSONConverter.toJsonVeranstList(verAnst);
            else
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return true;

        }
        else
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return false;
        }
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