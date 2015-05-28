package com.sopra.team1723.ctrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.Iterator;

import javafx.scene.shape.Line;
import jdk.internal.org.xml.sax.InputSource;

import org.apache.commons.io.FileUtils;

import com.sopra.team1723.data.Karteikarte;

public class PDFExporter implements Runnable
{
    private String servletContextPath;
    private String fileName;
    private String texFileName;
    private String workingDir = "";
    private String subFolder = "";
    private String dataStr = "";
    private boolean dataStrOpened = false;
    private boolean texFileCreated = false;
    private boolean pdfCreated = false;
    private boolean cleaned = false;
    private String newLineWithSeparation = System.getProperty("line.separator");
    
    private String Titel = "";
    private String Author = "";

    private ProcessBuilder pb;
    private Process p;
    
    // TODO 
//    servletContext = getServletContext();
//    contextPath = servletContext.getRealPath(File.separator);
    public PDFExporter(String workingDir, String servletContextPath)
    {
        this.workingDir = workingDir + "/";
        this.servletContextPath = servletContextPath;
        
        if(!new File(workingDir).exists())
        {
            System.out.println("Erstelle working dir: " + workingDir);
            if(!(new File(workingDir)).mkdirs())
                System.out.println("Kann working dir " + workingDir + " nicht erstellen!");
        }
        
        subFolder = String.valueOf(System.currentTimeMillis());
        texFileName = subFolder+".tex";
        fileName = subFolder + ".pdf";
        if(!(new File(this.workingDir + subFolder)).mkdirs())
            System.out.println("Kann subfolder " + subFolder + " nicht erstellen!");
        
        
    }
    public void setInfo(String Author, String Titel)
    {
        this.Titel = Titel;
        this.Author = Author;
    }
    
    public boolean createHeader()
    {
        if(dataStrOpened || cleaned)
            return false;
        
        dataStrOpened = true;
        String header = "\\documentclass[12pt]{scrreprt}" + newLineWithSeparation 
                + newLineWithSeparation 
                + "\\usepackage[ngerman]{babel}"  + newLineWithSeparation 
                + "\\usepackage{marvosym}        % Packet für komplexe Tabellen" + newLineWithSeparation 
                + "\\usepackage{array}" + newLineWithSeparation 
                + "% Zwei Zeile für den ganzen Text in Arial/Helvetica" + newLineWithSeparation 
                + "%\\usepackage[scaled]{helvet}" + newLineWithSeparation 
                + "%\\renewcommand{\\familydefault}{\\sfdefault}   " + newLineWithSeparation 
                + "% Paket für Grafiken und Abbildungen" + newLineWithSeparation 
                + "\\usepackage{graphicx}" + newLineWithSeparation 
                + "" + newLineWithSeparation 
                + "% Numerierung Tabellen und Bilder kapitelunabhängig" + newLineWithSeparation 
                + "\\usepackage{chngcntr}" + newLineWithSeparation 
                + "\\counterwithout{figure}{chapter}" + newLineWithSeparation 
                + "\\counterwithout{table}{chapter}" + newLineWithSeparation 
                + "\\counterwithout{footnote}{chapter}" + newLineWithSeparation 
                + "" + newLineWithSeparation 
                + "% Packet für Mathe-Formeln" + newLineWithSeparation 
                + "\\usepackage{amsmath, amssymb}" + newLineWithSeparation 
                + "\\usepackage{float}" + newLineWithSeparation 
                + "" + newLineWithSeparation 
                + "% Für besondere Absätze" + newLineWithSeparation 
                + "\\parindent 0pt      % Einrückung bei Absatz" + newLineWithSeparation 
                + "\\parskip 6pt        % Abstände zwischen Absätzen" + newLineWithSeparation 
                + "" + newLineWithSeparation 
                + "% Inhalte für den Titel des Dokuments" + newLineWithSeparation 
                + "\\subject{eLearning-PDF-Export}" + newLineWithSeparation 
                + "\\title{" + Titel + "}" + newLineWithSeparation 
                + "\\author{" + Author + "}" + newLineWithSeparation 
                + "% Hart rein coden oder \\today" + newLineWithSeparation 
                + "\\date{\\today}" + newLineWithSeparation 
                + "" + newLineWithSeparation 
                + "% Inhaltsverzeichnis klickbar machen, allgemein Verweise als Links." + newLineWithSeparation 
                + "% Außerdem wird ein Inhaltsverzeichnis im AdobeReader erzeugt. Also klickbares Inhaltsverzeichnis an der Seite." + newLineWithSeparation 
                + "\\usepackage{hyperref}" + newLineWithSeparation 
                + "" + newLineWithSeparation 
                + "\\begin{document}" + newLineWithSeparation 
                + "% Titelseite erstellen" + newLineWithSeparation 
                + "\\maketitle" + newLineWithSeparation 
                + "" + newLineWithSeparation 
                + "% Hier erscheint das Inhaltsverzeichnis (Nach dem hinzufügen 2 mal kompilieren)" + newLineWithSeparation 
                + "\\tableofcontents" + newLineWithSeparation ;
        
        dataStr = header;
        System.out.println("Header hinzugefügt");
        return true;
    }
    
    private boolean createAndCloseFile()
    {
        if(!dataStrOpened || cleaned)
            return false;
        
        dataStr += "\\listoffigures"+ newLineWithSeparation
                + "\\end{document}" + newLineWithSeparation ;
        dataStrOpened = false;
        
        FileWriter writer = null;
        try {
           
            writer = new FileWriter(workingDir + subFolder + "/" + texFileName, false);
            writer.write(dataStr, 0, dataStr.length());
            writer.close();
            texFileCreated = true;
            System.out.println("String in File " +subFolder + "/" + texFileName+" geschrieben.");
        } catch (IOException ex) {
            System.out.println("Fehler beim schreiben der TexDatei.");
            ex.printStackTrace();
        }

        return true;
    }
    
    private boolean convertFile()
    {
        if(!texFileCreated || cleaned)
            return false;
        
        pb = new ProcessBuilder("pdflatex", "-synctex=1", "-interaction=nonstopmode", workingDir + subFolder + "/" + texFileName);
        pb.directory(new File(workingDir + subFolder));
        try {
            System.out.println("Start pdflatex-Process and wait for finish...");
            System.out.println();
            pb.redirectErrorStream(true);
            p = pb.start();
            new Thread(this).start();
            int res = p.waitFor();
            if(res != 0)
            {
                System.out.println("Errorcode von pdflatex: " + res);
                return false;
            }
            System.out.println("Finished...");
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean appendKarteikarte(Karteikarte kk, int depth)
    {
        if(texFileCreated || cleaned || !dataStrOpened)
            return false;
     
        System.out.println("Append KK: " + kk.getTitel());
        switch (kk.getTyp())
        {
            case VIDEO:
                System.out.println("Video KK - nicht unterstützt");
                return false;   // Nicht unterstützt
            case BILD:
                System.out.println("Bild KK");
                String Strkk = createBildKKLatexStr(kk, depth);
                dataStr += Strkk + newLineWithSeparation;
                break;
            case TEXT:
                System.out.println("Text kk");
                String Strkk1 = createTextKKLatexStr(kk, depth);
                dataStr += Strkk1 + newLineWithSeparation;
                break;

            default:
                return false;
        }
        return true;
    }
    
    private String createTextKKLatexStr(Karteikarte kk, int depth)
    {
        
        if(kk.getTitel().equals(""))
        {
            return putStrIntoChapter(depth,kk.getTitel(), "");
        }
        else
        {
            String data = kk.getInhalt();

            return putStrIntoChapter(depth, kk.getTitel(), transformHTMLToLaTeX(data));
        }
    }
    private String transformHTMLToLaTeX(String html)
    {
        // TODO
        return html;
    }
    
    private String putStrIntoChapter(int chapterDepth, String title, String data)
    {
        String newData = null;
        switch (chapterDepth)
        {
            case 0:
                newData = "\\chapter{" + title + "}" + newLineWithSeparation;
                break;
            case 1:
                newData = "\\section{" + title + "}" + newLineWithSeparation;
                break;
            case 2:
                newData = "\\subsection{" + title + "}" + newLineWithSeparation;
                break;
            case 3:
                newData = "\\subsubsection{" + title + "}" + newLineWithSeparation;
                break;
            case 4:
                newData = "\\paragraph{" + title + "}" + newLineWithSeparation;
                break;

            default:
                newData = "";
                break;
        }
        return newData + newLineWithSeparation + data;
    }
    
    private String createBildKKLatexStr(Karteikarte kk, int depth)
    {
        String bildPfad = servletContextPath + "/files/images/" + kk.getId() + ".png";
        System.out.println("BildPfad: " + bildPfad);
        System.out.println("Copy to: " + workingDir + subFolder + "/" + kk.getId() + ".png");
        try
        {
            FileUtils.copyFile(new File(bildPfad), new File(workingDir + subFolder + "/" + kk.getId() + ".png"));
            
            String latexStr =  "\\begin{figure}[H] " + newLineWithSeparation
                    + "  \\centering" + newLineWithSeparation
                    + "  \\includegraphics[width=0.5\\linewidth]{"+kk.getId()+".png}" + newLineWithSeparation
                    + "  \\caption{"+ kk.getTitel() +"}" + newLineWithSeparation
                    + "  \\label{fig:kk_bild"+kk.getId()+"}" + newLineWithSeparation
                    + "\\end{figure}" + newLineWithSeparation;
            
            // TODO Bilder mit überschrift ?
            return latexStr;//putStrIntoChapter(depth,kk.getTitel(), latexStr);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
       
    }
    
    public String convertToPDFFile()
    {
        if(!createAndCloseFile())
            return null;
        
        // 2 Mal kompilieren !
        if(!convertFile())
            return null;
        if(!convertFile())
            return null;
        
        cleanUp();
        
        return fileName;
    }
    /**
     * Entfernt den temporären Ordner und, dass nur noch die PDF übrig ist.
     */
    public void cleanUp()
    {
        if((!pdfCreated && !texFileCreated) || cleaned)
            return;
        
        try
        {
            // Copy nach oben
            FileUtils.copyFile(new File(workingDir + subFolder + "/" + fileName), new File(workingDir + fileName));
            FileUtils.deleteDirectory(new File(workingDir + subFolder));
            cleaned = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run()
    {
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        while(p.isAlive())
        {
            try
            {
                String line = reader.readLine();
                if(line != null)
                    System.err.println(line);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
