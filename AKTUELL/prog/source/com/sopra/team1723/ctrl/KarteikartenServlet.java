package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.sopra.team1723.data.*;
import com.sopra.team1723.data.Karteikarte.AttributTyp;
import com.sopra.team1723.data.Karteikarte.BeziehungsTyp;

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
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if(!pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, request, response) &&
                aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        Karteikarte Kk = dbManager.leseKarteikarte(karteikartenID);
        Kk.setHatBewertet(dbManager.hatKarteikarteBewertet(karteikartenID  , aktuellerBenutzer.getId()));
        jo = Kk.toJSON(true);

        outWriter.print(jo);


    }

    private void getKarteikarteVorgaenger(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if(aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN &&
                !pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, request, response))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        // TODO
        Map<Integer,Karteikarte> Kks = dbManager.leseVorgaenger(karteikartenID, 5);
        List<Karteikarte> kk = new ArrayList<Karteikarte>(Kks.values());
        for(Karteikarte k :kk)
            k.setHatBewertet(dbManager.hatKarteikarteBewertet(k.getId()  , aktuellerBenutzer.getId()));

        jo =  JSONConverter.toJson(kk, true);
        outWriter.print(jo);
    }
    private void getKarteikarteNachfolger(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        if(aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN &&
                !pruefeFuerVeranstDerKarteikEingeschrieben(karteikartenID, request, response))
        {
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }

        Map<Integer,Karteikarte> Kks = dbManager.leseNachfolger(karteikartenID, 5);
        List<Karteikarte> kk = new ArrayList<Karteikarte>(Kks.values());
        for(Karteikarte k :kk){
            System.out.println(k.getId());
            k.setHatBewertet(dbManager.hatKarteikarteBewertet(k.getId()  , aktuellerBenutzer.getId()));
        }
        jo =  JSONConverter.toJson(kk, true);
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
     * @throws IOException 
     */
    private void karteikarteBewerten(HttpServletRequest request, HttpServletResponse response, int bewertung) throws IOException {
        HttpSession aktuelleSession = request.getSession();
        PrintWriter outWriter = response.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        String karteikIdString = request.getParameter(ParamDefines.Id);


        if(isEmpty(karteikIdString))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        int karteikId;
        try
        {
            karteikId = Integer.parseInt(karteikIdString);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        if(aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN &&
                !pruefeFuerVeranstDerKarteikEingeschrieben(karteikId, request, response))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
            outWriter.print(jo);
            return;
        }
        if(dbManager.bewerteKarteikarte(karteikId, bewertung, aktuellerBenutzer.getId()))
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            outWriter.print(jo);
            return;
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
        }

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

        try{    
            String titel = req.getParameter(ParamDefines.Titel);
            String inhalt = "";
            String uploadedID = "";

            KarteikartenTyp kkTyp;
            String typ = req.getParameter(ParamDefines.Type);
            System.out.println("KAREIKARTEN TYPE: "+ typ);
            if(typ.equals("mp4")){
                kkTyp = KarteikartenTyp.VIDEO;
                uploadedID = req.getParameter(ParamDefines.UploadID);
            }
            else if(typ.equals("jpg") || typ.equals("png")){
                kkTyp = KarteikartenTyp.BILD;
                uploadedID = req.getParameter(ParamDefines.UploadID);
            }
            else if(typ.equals("")){
                kkTyp = KarteikartenTyp.TEXT;
                inhalt = req.getParameter(ParamDefines.Inhalt);
            }
            else
                throw new Exception();

            int veranstaltung = Integer.parseInt(req.getParameter(ParamDefines.Veranstaltung));
            System.out.println("UPLOADED ID: "+ uploadedID);
            int vaterKK = Integer.parseInt(req.getParameter(ParamDefines.VaterKK));
            int ueberliegendeBruderKK = Integer.parseInt(req.getParameter(ParamDefines.BruderKK));

            if(vaterKK == -1 && ueberliegendeBruderKK == -1)
                throw new Exception();

            boolean[] bAttribute = new boolean[AttributTyp.values().length];

            String[] attribute = req.getParameter(ParamDefines.Attribute).split(",");

            for(int i=0; i<attribute.length; ++i){
                bAttribute[i] = Boolean.valueOf(attribute[i]);
            }

            if(!istModeratorDozentOderAdmin(aktuellerBenutzer,veranstaltung,dbManager))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
                outWriter.print(jo);
                return;
            }


            Karteikarte karteikarte = new Karteikarte(titel,inhalt,kkTyp,veranstaltung,bAttribute[0],bAttribute[1],bAttribute[2],
                    bAttribute[3], bAttribute[4], bAttribute[5], bAttribute[6], bAttribute[7], bAttribute[8], bAttribute[9]);   

            int kkID = dbManager.schreibeKarteikarte(karteikarte, vaterKK, ueberliegendeBruderKK);

            if(kkTyp == KarteikartenTyp.VIDEO || kkTyp == KarteikartenTyp.BILD){
                String relativerPfad = "";
                String relativerNeuerPfad = "";
                if(kkTyp == KarteikartenTyp.VIDEO){
                    relativerPfad = dirKKVideo + uploadedID + ".mp4";
                    relativerNeuerPfad = dirKKVideo + kkID + ".mp4";
                }
                else{
                    relativerPfad = dirKKBild + uploadedID + ".png";
                    relativerNeuerPfad = dirKKBild + kkID + ".png";
                }

                ServletContext servletContext;
                String contextPath;

                servletContext = getServletContext();
                contextPath = servletContext.getRealPath(File.separator);

                String absolutePath = contextPath + relativerPfad;
                String absoluteNeuerPfad = contextPath + relativerNeuerPfad;
                File oldName = new File(absolutePath);
                File newName = new File(absoluteNeuerPfad);
                System.out.println("alter Filename: "+ absolutePath);
                System.out.println("neuer Filename: "+ absoluteNeuerPfad);
               try{
            	   if(!oldName.renameTo(newName)){
            		   System.out.println("RENAME DIDNT WORK");

            		   throw new Exception();
            	   }
               }
               catch(SecurityException e){
            	   	e.printStackTrace();
            	   	System.out.println("SECURITY EXCEPTION");
               		jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
               		outWriter.print(jo);
               }
                	
            }


            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            jo.put(ParamDefines.Id, kkID);  // Schicke die karteikarten id zurück!

        } catch(IllegalArgumentException e){
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);

        } catch(Exception e){
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
        }

        outWriter.print(jo);
    }
    private void erstelleUeberschrift(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;

        try{    
            String titel = req.getParameter(ParamDefines.Titel);
            String inhalt ="";
            KarteikartenTyp kkTyp = KarteikartenTyp.TEXT;
            String typ = req.getParameter(ParamDefines.Type);
            int veranstaltung = Integer.parseInt(req.getParameter(ParamDefines.Veranstaltung));
            int vaterKK = Integer.parseInt(req.getParameter(ParamDefines.VaterKK));
            int ueberliegendeBruderKK = Integer.parseInt(req.getParameter(ParamDefines.BruderKK));

            if(vaterKK == -1 && ueberliegendeBruderKK == -1)
                throw new Exception();

            boolean[] bAttribute = new boolean[AttributTyp.values().length];

            String[] attribute = req.getParameter(ParamDefines.Attribute).split(",");

            for(int i=0; i<attribute.length; ++i){
                bAttribute[i] = Boolean.valueOf(attribute[i]);
            }

            if(!istModeratorDozentOderAdmin(aktuellerBenutzer,veranstaltung,dbManager))
            {
                jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
                outWriter.print(jo);
                return;
            }


            Karteikarte karteikarte = new Karteikarte(titel,inhalt,kkTyp,veranstaltung,bAttribute[0],bAttribute[1],bAttribute[2],
                    bAttribute[3], bAttribute[4], bAttribute[5], bAttribute[6], bAttribute[7], bAttribute[8], bAttribute[9]);   

            int kkID = dbManager.schreibeKarteikarte(karteikarte, vaterKK, ueberliegendeBruderKK);



            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNoError);
            jo.put(ParamDefines.Id, kkID);  // Schicke die karteikarten id zurück!

        } catch(IllegalArgumentException e){
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);

        } catch(Exception e){
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
        }

        outWriter.print(jo);
    }

    private boolean istModeratorDozentOderAdmin(Benutzer aktuellerBenutzer, int veranstaltung, IDatenbankmanager dbManager){
        if(aktuellerBenutzer.getId() != dbManager.leseVeranstaltung(veranstaltung).getErsteller().getId() &&
                !(dbManager.istModerator(aktuellerBenutzer.getId(), veranstaltung)) &&
                !(aktuellerBenutzer.getNutzerstatus() == Nutzerstatus.ADMIN)){
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            Karteikarte kk = dbManager.leseKarteikarte(karteikarte);
            if(kk == null) {
                return false;
            }
            return dbManager.angemeldet(aktuellerBenutzer.getId(), kk.getVeranstaltung());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            // Nichts zurückgeben!
            //            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            //            outWriter.print(jo);

        }
        return false;
    }

    private void verweisHinzufuegen(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;

        try{
            int verweisQuelleId = Integer.parseInt(req.getParameter(ParamDefines.KKVerweisQuelleId));
            int verweisZielId = Integer.parseInt(req.getParameter(ParamDefines.KKVerweisZielId));
            BeziehungsTyp verweisTyp = BeziehungsTyp.valueOf(req.getParameter(ParamDefines.Type));

            dbManager.connectKk(verweisQuelleId, verweisZielId, verweisTyp, null);
        }    
        catch(NumberFormatException e){
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        } 
        catch(IllegalArgumentException e){
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        catch(Exception e){
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }

    }
    private void exportSkript(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        HttpSession aktuelleSession = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) aktuelleSession.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) aktuelleSession.getAttribute(sessionAttributeDbManager);

        JSONObject jo = null;

        ServletContext servletContext = getServletContext();
        String contextPath = servletContext.getRealPath(File.separator);

        int vnId = -1;
        try
        {
            vnId = Integer.parseInt(req.getParameter(ParamDefines.Id));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }

        Veranstaltung v = dbManager.leseVeranstaltung(vnId);
        
        PDFExporter pexp = new PDFExporter(contextPath + "/pdfExports", contextPath);
        appendKKExportRecursive(dbManager,pexp,0,v.getErsteKarteikarte());
        pexp.startConvertToPDFFile();
//        System.out.println("PDF erzeugt: "+ filename);
    }
    private void appendKKExportRecursive(IDatenbankmanager dbManager, PDFExporter pexp, int depth, int vaterKkId)
    {
        Map<Integer, Tupel<Integer, String>>  kkList = dbManager.leseKindKarteikarten(vaterKkId);
        // Vater hinzufügen
        Karteikarte k = dbManager.leseKarteikarte(vaterKkId);
        pexp.appendKarteikarte(k, depth);
        
        int i = 0;
        for(; i< kkList.size();i++)
        {
            Tupel<Integer, String> tu =  kkList.get(i);
            appendKKExportRecursive(dbManager,pexp,depth+1,tu.x);
        }

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
        else if(aktuelleAction.equals(ParamDefines.ActionErstelleUeberschrift))
        {
            erstelleUeberschrift(req,resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionGetKarteikartenNachfolger))
        {
            getKarteikarteNachfolger(req,resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionGetKarteikartenVorgaenger))
        {
            getKarteikarteVorgaenger(req,resp);
        } 
        else if(aktuelleAction.equals(ParamDefines.ActionVoteKarteikarteUp))
        {
            karteikarteBewerten(req, resp, 1);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionVoteKarteikarteDown))
        {
            karteikarteBewerten(req,resp,-1);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionVerweisHinzufuegen)){
            verweisHinzufuegen(req, resp);
        }
        else if(aktuelleAction.equals(ParamDefines.ActionExportSkript)){
            exportSkript(req, resp);
        }
        else
        {
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);

        }
    }

}
