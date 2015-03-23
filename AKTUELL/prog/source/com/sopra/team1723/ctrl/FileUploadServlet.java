package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;

public class FileUploadServlet extends ServletController
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        //super.doPost(req, resp);

        resp.setContentType("text/json");
        outWriter = resp.getWriter();
        
        boolean isMultipartContent = ServletFileUpload.isMultipartContent(req);
        if (!isMultipartContent) 
        {
            System.out.println("Kein Dateiupload!");
            return;
        }
        System.out.println("File upload gestartet!");
        
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try 
        {
            List<FileItem> fields = upload.parseRequest(req);
            System.out.println("Anzahl Formular Felder: " + fields.size());
            
            Iterator<FileItem> it = fields.iterator();
            if (!it.hasNext()) 
            {
                System.out.println("Keine Felder wurden übertragen!");
                return;
            }
            System.out.println("Lese Felder...");
            List<String> filePathes = new ArrayList<>();
            while (it.hasNext()) 
            {
                FileItem fileItem = it.next();
                boolean isFormField = fileItem.isFormField();
                if (isFormField) 
                {
                    System.out.println("Formular Feld: " + fileItem.getFieldName() );
                    System.out.println("Text-Inhalt: " + fileItem.getString());
                } 
                else 
                {
                    System.out.println("Datei Feld: " + fileItem.getFieldName());

                    if(fileItem.getName().equals(""))
                    {
                        System.out.println("Dateiname leer. Überspringen!");
                        System.out.println();
                        continue;
                    }
                    
//                    System.out.println("Text Inhalt: " + fileItem.getString());
                    System.out.println("Datei Name: " + fileItem.getName());
                    System.out.println("Content Type: " + fileItem.getContentType());
                    System.out.println("Dateigröße (Byte): " + fileItem.getSize());
                    
                    
                    // Datei erzeugen
                    ServletContext servletContext = getServletContext();
                    String contextPath = servletContext.getRealPath(File.separator);
                    String absolutePath = contextPath + "/files/" + fileItem.getName();
                    
                    File f = new File(absolutePath);
                    f.createNewFile();
                    FileOutputStream fstr = new FileOutputStream(f);
                    
                    fstr.write(fileItem.get());
                    fstr.close();
                    System.out.println("File gespeichert: " + f.getAbsolutePath());
                    System.out.println("  relativer Pfad: " + "/files/" + fileItem.getName());
                    
                    filePathes.add("/files/" + fileItem.getName());
                }
                System.out.println();
            }
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJson(filePathes);
            outWriter.print(jo);
            return;
        }
        catch (FileUploadException e) 
        {
            e.printStackTrace();
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorSystemError);
            outWriter.print(jo);
            return;
        }
    }
}
