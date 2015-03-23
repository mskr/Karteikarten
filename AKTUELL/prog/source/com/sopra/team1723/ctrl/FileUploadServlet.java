package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;

@MultipartConfig
public class FileUploadServlet extends ServletController
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        super.doPost(req, resp);

        if(!doProcessing())
            return;
        
        if(!aktuelleAction.equals(requestActionUploadProfilBild))
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        Part uploadedFile = req.getPart(requestUploadFile);
        String fileName = getFileName(uploadedFile);
        InputStream contentStream = uploadedFile.getInputStream();

        ServletContext servletContext = getServletContext();
        String contextPath = servletContext.getRealPath(File.separator);
        String relativerPfad = dirProfilBilder + aktuellerBenutzer.geteMail() + "." + FilenameUtils.getExtension(fileName);
        String absolutePath = contextPath + relativerPfad;
        
        File f = new File(absolutePath);
        if(f.exists())
            System.out.println("WARNING: Datei existiert bereits! Sie wird nun überschrieben!");

        f.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(f);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = contentStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        outputStream.close();
        
        System.out.println("File gespeichert: " + absolutePath);
        System.out.println("  relativer Pfad: " + relativerPfad);

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

