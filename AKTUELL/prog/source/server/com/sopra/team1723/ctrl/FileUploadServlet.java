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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;

import com.sopra.team1723.data.Benutzer;
import com.sopra.team1723.data.Nutzerstatus;

/**
 * Dieses Servlet kümmert sich um den Upload von Files. Dazu gehören
 * Profilbilder und Karteikarteninhalte.
 *
 */
@MultipartConfig
public class FileUploadServlet extends ServletController
{

    protected final int profilBildHeigth = 150;             // Pixel
    protected final int profilBildWidth  = profilBildHeigth;

    @Override
    protected void processRequest(String aktuelleAction, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        HttpSession s = req.getSession();
        PrintWriter outWriter = resp.getWriter();
        Benutzer aktuellerBenutzer = (Benutzer) s.getAttribute(sessionAttributeaktuellerBenutzer);
        IDatenbankmanager dbManager = (IDatenbankmanager) s.getAttribute(sessionAttributeDbManager);
        Part uploadedFile;
        String fileName;
        InputStream contentStream;

        ServletContext servletContext;
        String contextPath;
        String fileExt;

        // Upload eines Bildes für eine Karteikarte
        if (aktuelleAction.equals(ParamDefines.ActionUploadKKBild))
        {
            uploadedFile = req.getPart(ParamDefines.UploadFile);
            fileName = getFileName(uploadedFile);
            contentStream = uploadedFile.getInputStream();
            	
            //absoluten pfad ausgehend vom laufwerk, auf dem das Servlet läuft
            servletContext = getServletContext();
            contextPath = servletContext.getRealPath(File.separator);
            fileExt = FilenameUtils.getExtension(fileName);
            
            //prüfe auf valide file-extensions
            if (fileExt == null
                    || (!fileExt.equalsIgnoreCase("jpg") && !fileExt.equalsIgnoreCase("jpeg") && !fileExt
                            .equalsIgnoreCase("png")))
            {
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
            }
            //UNIQUE FILENAME: unix timestamp erschaffen, wird später gehasht
            String unixTimestamp = Instant.now().getEpochSecond() + "";

            String UploadID = null;
            //timestamp in bytearray umwandeln
            byte[] bytesOfMessage = unixTimestamp.getBytes("UTF-8");

            //message digest klasse (java.security package, unterstützt mehrere md-versionen, hier md5)
            MessageDigest md;
            try
            {	
            	
                md = MessageDigest.getInstance("MD5");
                //hash to md5
                md.update(bytesOfMessage);
                byte[] digest = md.digest();
                
                //convert md5 byte-array zu string
                StringBuffer sb = new StringBuffer();
                for (byte b : digest)
                {
                    sb.append(String.format("%02x", b & 0xff));
                }
                UploadID = sb.toString();
                //System.out.println("uploadid:" + UploadID);

            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
            }
            if (UploadID != null)
            {
            	//mache aus dem InputStream ein BufferedImage
                BufferedImage originalImage = ImageIO.read(contentStream);
                //file soll nun unter der uploadID gefolgt von der Extension abgespeichert werden
                String dateiName = UploadID + ".png";
                String relativerPfad = dirKKBild + dateiName;
                //gesamter pfad ergibt sich aus dem Pfad des Javaprojects und dem pfad innerhalb,
                //unter dem es abgespeichert werden soll
                String absolutePath = contextPath + relativerPfad;
                ImageIO.write(originalImage, "png", new File(absolutePath));
                
                //wenn nach einer stunde datei noch vorhanden, also nicht verwendet wurde, lösche die datei
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run()
                    {
                        FileUtils.deleteQuietly(new File(absolutePath));
                    }
                }, 60 * 60 * 1000);

                JSONObject jo = JSONConverter.toJson(UploadID);
                outWriter.print(jo);
            }
            else
            {
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
            }
        }
        // Upload einer videodatei für eine Karteikarte
        else if (aktuelleAction.equals(ParamDefines.ActionUploadKKVideo))
        {
            uploadedFile = req.getPart(ParamDefines.UploadFile);
            fileName = getFileName(uploadedFile);
            contentStream = uploadedFile.getInputStream();

            servletContext = getServletContext();
            contextPath = servletContext.getRealPath(File.separator);
            fileExt = FilenameUtils.getExtension(fileName);

            if (fileExt == null || (!fileExt.equalsIgnoreCase("mp4")))
            {
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
            }
            String unixTimestamp = Instant.now().getEpochSecond() + "";

            String UploadID = null;
            byte[] bytesOfMessage = unixTimestamp.getBytes("UTF-8");

            MessageDigest md;
            try
            {
                md = MessageDigest.getInstance("MD5");
                md.update(bytesOfMessage);
                byte[] digest = md.digest();
                StringBuffer sb = new StringBuffer();
                for (byte b : digest)
                {
                    sb.append(String.format("%02x", b & 0xff));
                }
                UploadID = sb.toString();
                System.out.println("uploadid:" + UploadID);

            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
            }
            if (UploadID != null)
            {
                String dateiName = UploadID + ".mp4";
                String relativerPfad = dirKKVideo + dateiName;
                String absolutePath = contextPath + relativerPfad;

                FileOutputStream fos = new FileOutputStream(absolutePath);
                int b = 0;
                while (b != -1)
                {

                    b = contentStream.read();
                    fos.write(b);
                }
                int read = 0;
                final byte[] bytes = new byte[1024];
                while ((read = contentStream.read(bytes)) != -1)
                {
                    fos.write(bytes, 0, read);
                }
                if (fos != null)
                {
                    fos.close();
                }
                JSONObject jo = JSONConverter.toJson(UploadID);
                outWriter.print(jo);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run()
                    {
                        FileUtils.deleteQuietly(new File(absolutePath));
                    }
                }, 60 * 60 * 1000);
            }
        }
        // Upload des Profilbilds für einen Benutzer
        else if (aktuelleAction.equals(ParamDefines.ActionUploadProfilBild))
        {
            String idStr = req.getParameter(ParamDefines.Id);

            int id = 0;
            try
            {
                id = Integer.parseInt(idStr);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
                return;
            }

            if (id != aktuellerBenutzer.getId() && aktuellerBenutzer.getNutzerstatus() != Nutzerstatus.ADMIN)
            {
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorNotAllowed);
                outWriter.print(jo);
                return;
            }

            uploadedFile = req.getPart(ParamDefines.UploadFile);
            fileName = getFileName(uploadedFile);
            contentStream = uploadedFile.getInputStream();

            servletContext = getServletContext();
            contextPath = servletContext.getRealPath(File.separator);
            fileExt = FilenameUtils.getExtension(fileName);

            if (fileExt == null
                    || (!fileExt.equalsIgnoreCase("jpg") && !fileExt.equalsIgnoreCase("jpeg")
                            && !fileExt.equalsIgnoreCase("png") && !fileExt.equalsIgnoreCase("bmp")))
            {
                // Sende Error zurück
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
                outWriter.print(jo);
                return;
            }

            // Bild neu Skalieren und speichern
            BufferedImage originalImage = ImageIO.read(contentStream);

            // Bild zuschneiden
            if (originalImage.getWidth() > originalImage.getHeight())
            {
                int diff = originalImage.getWidth() - originalImage.getHeight();
                // Links und rechts abschneiden
                originalImage = originalImage.getSubimage(diff / 2, 0, originalImage.getWidth() - diff,
                        originalImage.getHeight());
            }
            else if (originalImage.getWidth() < originalImage.getHeight())
            {
                int diff = originalImage.getHeight() - originalImage.getWidth();
                // oben und unten abschneiden
                originalImage = originalImage.getSubimage(0, diff / 2, originalImage.getWidth(),
                        originalImage.getHeight() - diff);
            }

            // Bild skalieren
            BufferedImage scaledImage = new BufferedImage(profilBildWidth, profilBildHeigth,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = scaledImage.createGraphics();
            g.drawImage(originalImage, 0, 0, profilBildWidth, profilBildHeigth, null);
            g.dispose();

            String dateiName = System.currentTimeMillis() + ".png";
            String relativerPfad = dirProfilBilder + dateiName;
            String absolutePath = contextPath + relativerPfad;
            ImageIO.write(scaledImage, "png", new File(absolutePath));

            // Benutzer holen um zu prüfen, wie sein Altes profilbild heißt
            Benutzer bcurr = dbManager.leseBenutzer(id);

            if (!dbManager.aendereProfilBild(id, dateiName))
            {
                // Sende Error zurück
                JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorSystemError);
                outWriter.print(jo);
                return;
            }

            // Altes Bild löschen
            if (!bcurr.getProfilBildPfad().contains("default.png"))
            {
                File f = new File(contextPath + bcurr.getProfilBildPfad());
                f.delete();
            }
            System.out.println("File gespeichert: " + absolutePath);
            System.out.println("Web Pfad: " + relativerPfad);

            JSONObject jo = JSONConverter.toJson(relativerPfad);
            outWriter.print(jo);
            return;
        }
        else
        {
            // Sende Error zurück
            JSONObject jo = JSONConverter.toJsonError(ParamDefines.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
    }
    /**
     * Liest den Dateinamen aus einer Multipart message
     * @param part
     * @return
     */
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
