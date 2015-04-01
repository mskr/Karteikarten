package com.sopra.team1723.ctrl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;

import com.sopra.team1723.data.Benutzer;

@MultipartConfig
public class FileUploadServlet extends ServletController
{
    
    protected final int profilBildHeigth = 150;             // Pixel
    protected final int profilBildWidth = profilBildHeigth;
    
    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        HttpSession s = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) s.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) s.getAttribute(sessionAttributeDbManager);
        
        if(!aktuelleAction.equals(ParamDefines.ActionUploadProfilBild))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        Part uploadedFile = req.getPart(ParamDefines.UploadFile);
        String fileName = getFileName(uploadedFile);
        InputStream contentStream = uploadedFile.getInputStream();

        ServletContext servletContext = getServletContext();
        String contextPath = servletContext.getRealPath(File.separator);
        String fileExt = FilenameUtils.getExtension(fileName);
        
        if(fileExt == null || 
                (!fileExt.equalsIgnoreCase("jpg") && 
                !fileExt.equalsIgnoreCase("jpeg") &&
                !fileExt.equalsIgnoreCase("png")  &&
                !fileExt.equalsIgnoreCase("bmp")))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        // Bild neu Skalieren und speichern
        BufferedImage originalImage = ImageIO.read(contentStream);
        
        // Bild zuschneiden
        if(originalImage.getWidth() > originalImage.getHeight())
        {
            int diff = originalImage.getWidth()-originalImage.getHeight();
            // Links und rechts abschneiden
            originalImage = originalImage.getSubimage(diff/2, 0, originalImage.getWidth()-diff, originalImage.getHeight());
        }
        else  if(originalImage.getWidth() < originalImage.getHeight())
        {
            int diff = originalImage.getHeight()-originalImage.getWidth();
            // oben und unten abschneiden
            originalImage = originalImage.getSubimage(0, diff/2, originalImage.getWidth(), originalImage.getHeight()-diff);
        }
        
        // Bild skalieren
        BufferedImage scaledImage = new BufferedImage(profilBildWidth, profilBildHeigth, BufferedImage.TYPE_INT_ARGB);
        Graphics g = scaledImage.createGraphics();
        g.drawImage(originalImage, 0, 0, profilBildWidth, profilBildHeigth, null);
        g.dispose();

        String dateiName = System.currentTimeMillis() + ".png";
        String relativerPfad = dirProfilBilder + dateiName;
        String absolutePath = contextPath + relativerPfad;
        ImageIO.write(scaledImage, "png", new File(absolutePath));
        
        if(!dbManager.aendereProfilBild(aktuellerBenutzer.getId(), dateiName))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }
        
        // Altes Bild löschen
        if(!aktuellerBenutzer.getProfilBildPfad().equals("default.png"))
        {
            File f = new File(contextPath + aktuellerBenutzer.getProfilBildPfad());
            f.delete();
        }
        System.out.println("File gespeichert: " + absolutePath);
        System.out.println("Web Pfad: " + relativerPfad);

        JSONObject jo = JSONConverter.toJson(relativerPfad);
        outWriter.print(jo);
        return;
    }
    
    private String getFileName(Part part) 
    {
        for (String cd : part.getHeader("content-disposition").split(";")) 
        {
            if (cd.trim().startsWith("filename"))
            {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}

