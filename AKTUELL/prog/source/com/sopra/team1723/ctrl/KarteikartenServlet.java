package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;

/**
 * Verwaltet die Karteikarten
 */
public class KarteikartenServlet extends ServletController {

    /**
     * Verwaltet die Karteikarten
     */
    public KarteikartenServlet() {
    }
    /**
     * Aus der Datenbank wird mit Hilfe der KarteikartenID die Informationen
     * bezuglich der Karteikarte gelesen und zuruckgegeben. Falls
     * es diese Karteikarte in der Datenbank nicht gibt, gibt die Methode
     * false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private void getKarteikarteByID(HttpServletRequest request, HttpServletResponse response) throws IOException{
    	 HttpSession aktuelleSession = request.getSession();
         PrintWriter outWriter = response.getWriter();
         Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
         IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

         JSONObject jo;

         int karteikartenID = -1;
         try{
        	 karteikartenID = Integer.parseInt(request.getParameter(ParamDefines.Id));
         }    
         catch(NumberFormatException e){
             jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
             outWriter.print(jo);
             return;
         }

         if(!pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, request, response) &&
                 aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN){
             jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
             outWriter.print(jo);
         }
         Karteikarte Kk = dbManager.leseKarteikarte(karteikartenID);
         jo = Kk.toJSONDateFix(true);
         outWriter.print(jo);

         
    }

    /**
     * Aus der Datenbank wird die gewunschte Karteikarte gelesen. Der
     * Benutzer kann diese nun mit einer Zahl bewerten. die Bewertung
     * wird dann in der Datenbank gespeichert.Falls es diese Karteikarte
     * in der Datenbank nicht gibt, gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean karteikarteBewerten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der KarteikartenID die Informationen
     * bezuglich der Karteikarte gelesen und zuruckgegeben. Der
     * Benutzer kann diese nun andern und die geanderten Daten werden
     * in der Datenbank abgespeichert.Falls es diese Karteikarte in der
     * Datenbank nicht gibt, gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean karteikarteBearbeiten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * Aus der Datenbank wird mit Hilfe der KarteikartenID die
     * gewunschte Karteikarte geloscht. Falls es diese Karteikarte nicht
     * gibt, gibt die Methode false zuruck
     * @param request 
     * @param response 
     * @return
     */
    private boolean karteikarteLoeschen(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * @param request 
     * @param response 
     * @return
     */
    private boolean exportiereKarteikarten(HttpServletRequest request, HttpServletResponse response) {
        // TODO implement here
        return false;
    }

    /**
     * @param vater 
     * @param dateiname 
     * @param bilder 
     * @param notizen 
     * @param kommentare 
     * @param querverweise 
     * @return
     */
    private File exportiereKarteikarten(Karteikarte vater, String dateiname, boolean bilder, boolean notizen, boolean kommentare, boolean querverweise) {
        // TODO implement here
        return null;
    }
    
    /**
     * Ein Benutzer erstellt eine Karteikarte und weist ihr verschiedene
     * Eigenschaften zu. Die Karteikarte bekommt eine individuelle ID.
     * Auerdem wird gepruft ob es bereits eine Karteikarte mit dem selben
     * Inhalt gibt.
     * @param request 
     * @param response 
     * @return
     * @throws IOException 
     */
    private void erstelleKarteikarte(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        
        JSONObject jo = null;
        
        String titel = req.getParameter(ParamDefines.Titel);
        String inhalt = req.getParameter(ParamDefines.Inhalt);
        try{    
            KarteikartenTyp typ = KarteikartenTyp.valueOf(req.getParameter(ParamDefines.Type));
            int veranstaltung = Integer.valueOf(req.getParameter(ParamDefines.Veranstaltung));
            
            Karteikarte karteikarte = new Karteikarte(titel,inhalt,typ,veranstaltung);
            
            if(!istModeratorDozentOderAdmin(aktuellerBenutzer,veranstaltung,dbManager))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
                outWriter.print(jo);
                return;
            }
            dbManager.schreibeKarteikarte(karteikarte);
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            
        } catch(IllegalArgumentException e){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            
        } catch(SQLException e){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
        }

        outWriter.print(jo);
    }
    
    private boolean istModeratorDozentOderAdmin(Benutzer aktuellerBenutzer, int veranstaltung, IDatenbankmanager dbManager){
        if(aktuellerBenutzer.getId() != dbManager.leseVeranstaltung(veranstaltung).getErsteller().getId() &&
                !(dbManager.istModerator(aktuellerBenutzer.getId(), veranstaltung)) &&
                !(aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)){
            return false;
        }
        return true;
    }

    private void leseKarteikarte(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);
        
        JSONObject jo = null;
        int karteikarteId = -1;
        
        try{
            karteikarteId = Integer.parseInt(req.getParameter(ParamDefines.Id));
        }    
        catch(NumberFormatException e){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        if(!pruefeFuerVeranstDerKarteikEingeschrieben(karteikarteId, req, resp) &&
                aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        
        Karteikarte karteikarte = dbManager.leseKarteikarte(karteikarteId);
        if(karteikarte == null){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }
        
        outWriter.print(karteikarte.toJSON(true));

    }
    
    private void leseKarteikartenKinder(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;
        int vaterKarteikarte = -1;
        try{
            vaterKarteikarte = Integer.parseInt(req.getParameter(ParamDefines.Id));
        }    
        catch(NumberFormatException e){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if(!pruefeFuerVeranstDerKarteikEingeschrieben(vaterKarteikarte, req, resp) &&
                aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        Map<Integer,Tupel<Integer,String>> kindKarteikarten = dbManager.leseKindKarteikarten(vaterKarteikarte);
        
        if(kindKarteikarten == null){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }

        jo = JSONConverter.toJsonKarteikarten(kindKarteikarten);
        outWriter.print(jo);

    }

    private boolean pruefeFuerVeranstDerKarteikEingeschrieben(int karteikarte, HttpServletRequest req, HttpServletResponse resp) throws IOException{
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        try
        {
            return dbManager.angemeldet(aktuellerBenutzer.getId(),dbManager.leseKarteikarte(karteikarte).getVeranstaltung());
        }
        catch (SQLException e)
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }
        return false;
    }


    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp) throws ServletException,
    IOException
    {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);


        if(aktuelleAction.equals(ParamDefines.ActionGetKarteikartenKinder))
        {
            leseKarteikartenKinder(req,resp);
        } 
        else if(aktuelleAction.equals(ParamDefines.ActionGetKarteikarteByID))
        {
        	getKarteikarteByID(req,resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionErstelleKarteikarte))
        {
            erstelleKarteikarte(req,resp);
        }
        

    }

}
