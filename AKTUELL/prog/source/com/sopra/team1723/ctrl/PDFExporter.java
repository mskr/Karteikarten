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
import java.util.List;

import javafx.scene.shape.Line;
import jdk.internal.org.xml.sax.InputSource;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import com.sopra.team1723.data.Karteikarte;
import com.sopra.team1723.data.Veranstaltung;

public class PDFExporter
{
    private String         servletContextPath;
    private String         fileName;
    private String         texFileName;
    private String         workingDir            = "";
    private String         subFolder             = "";
    private String         dataStr               = "";
    private boolean        dataStrOpened         = false;
    private boolean        texFileCreated        = false;
    private boolean        cleaned               = false;
    private String         newLineWithSeparation = "\n";

    private ProcessBuilder pb;
    private PDFExportThreadHandler peth;
    private Veranstaltung vn;
    private boolean exportNotizen = false;
    private boolean exportKommentare = false;
    private boolean exportAttribute = false;
    
    public PDFExporter(String workingDir, String servletContextPath, Veranstaltung vn, boolean exportNotizen, boolean exportKommentare, boolean exportAttribute)
    {
        this.exportNotizen = exportNotizen;
        this.exportKommentare = exportKommentare;
        this.exportAttribute = exportAttribute;
        this.workingDir = workingDir + "/";
        this.vn = vn;
        this.servletContextPath = servletContextPath;

        if (!new File(workingDir).exists())
        {
            System.out.println("Erstelle working dir: " + workingDir);
            if (!(new File(workingDir)).mkdirs())
                System.out.println("Kann working dir " + workingDir + " nicht erstellen!");
        }

        subFolder = String.valueOf(System.currentTimeMillis());
        texFileName = subFolder + ".tex";
        fileName = subFolder + ".pdf";
        if (!(new File(this.workingDir + subFolder)).mkdirs())
            System.out.println("Kann subfolder " + subFolder + " nicht erstellen!");
        
        createHeader();
    }

//    public void setInfo(String Author, String Titel)
//    {
//        this.Titel = Titel;
//        this.Author = Author;
//    }

    public boolean createHeader()
    {
        if (dataStrOpened || cleaned)
            return false;

        dataStrOpened = true;
        String header = "\\documentclass[12pt]{scrreprt}"
                + newLineWithSeparation
                + newLineWithSeparation
                + "\\usepackage[ngerman]{babel}"
                + newLineWithSeparation
                + "\\usepackage{marvosym}        % Packet für komplexe Tabellen"
                + newLineWithSeparation
                + "\\usepackage{array}"
                + newLineWithSeparation
                + "% Zwei Zeile für den ganzen Text in Arial/Helvetica"
                + newLineWithSeparation
                + "%\\usepackage[scaled]{helvet}"
                + newLineWithSeparation
                + "%\\renewcommand{\\familydefault}{\\sfdefault}   "
                + newLineWithSeparation
                + "% Paket für Grafiken und Abbildungen"
                + newLineWithSeparation
                + "\\usepackage{graphicx}"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Numerierung Tabellen und Bilder kapitelunabhängig"
                + newLineWithSeparation
                + "\\usepackage{chngcntr}"
                + newLineWithSeparation
                + "\\counterwithout{figure}{chapter}"
                + newLineWithSeparation
                + "\\counterwithout{table}{chapter}"
                + newLineWithSeparation
                + "\\counterwithout{footnote}{chapter}"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Packet für Mathe-Formeln"
                + newLineWithSeparation
                + "\\usepackage{amsmath, amssymb}"
                + newLineWithSeparation
                + "\\usepackage{float}"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Für besondere Absätze"
                + newLineWithSeparation
                + "\\parindent 0pt      % Einrückung bei Absatz"
                + newLineWithSeparation
                + "\\parskip 6pt        % Abstände zwischen Absätzen"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Inhalte für den Titel des Dokuments"
                + newLineWithSeparation
                + "\\subject{eLearning-PDF-Export}"
                + newLineWithSeparation
                + "\\title{"
                + vn.getTitel()
                + "}"
                + newLineWithSeparation
                + "\\author{"
                + vn.getErsteller().getVorname() + " " + vn.getErsteller().getNachname() 
                + "}"
                + newLineWithSeparation
                + "% Hart rein coden oder \\today"
                + newLineWithSeparation
                + "\\date{\\today}"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Inhaltsverzeichnis klickbar machen, allgemein Verweise als Links."
                + newLineWithSeparation
                + "% Außerdem wird ein Inhaltsverzeichnis im AdobeReader erzeugt. Also klickbares Inhaltsverzeichnis an der Seite."
                + newLineWithSeparation + "\\usepackage{hyperref}" + newLineWithSeparation + "" + newLineWithSeparation
                + "\\begin{document}" + newLineWithSeparation + "% Titelseite erstellen" + newLineWithSeparation
                + "\\maketitle" + newLineWithSeparation + "" + newLineWithSeparation
                + "% Hier erscheint das Inhaltsverzeichnis (Nach dem hinzufügen 2 mal kompilieren)"
                + newLineWithSeparation + "\\tableofcontents" + newLineWithSeparation;

        dataStr = header;
        System.out.println("Header hinzugefügt");
        return true;
    }

    private boolean createAndCloseFile()
    {
        if (!dataStrOpened || cleaned)
            return false;

        dataStr += "\\listoffigures" + newLineWithSeparation + "\\end{document}" + newLineWithSeparation;
        dataStrOpened = false;

        FileWriter writer = null;
        try
        {

            writer = new FileWriter(workingDir + subFolder + "/" + texFileName, false);
            writer.write(dataStr, 0, dataStr.length());
            writer.close();
            texFileCreated = true;
            System.out.println("String in File " + subFolder + "/" + texFileName + " geschrieben.");
        }
        catch (IOException ex)
        {
            System.out.println("Fehler beim schreiben der TexDatei.");
            ex.printStackTrace();
        }
        return true;
    }

    private boolean startConvertFile()
    {
        if (!texFileCreated || cleaned || peth != null)
            return false;

        pb = new ProcessBuilder("pdflatex", "-synctex=1", "-interaction=nonstopmode", workingDir + subFolder + "/"
                + texFileName);
        pb.directory(new File(workingDir + subFolder));
        pb.redirectErrorStream(true);
        
        peth = new PDFExportThreadHandler(this, pb);
        new Thread(peth).start();
        
        return true;
    }

    public boolean appendKarteikarte(Karteikarte kk, int depth)
    {
        if (texFileCreated || cleaned || !dataStrOpened)
            return false;

        System.out.println("Append KK: " + kk.getTitel());
        switch (kk.getTyp())
        {
            case VIDEO:
                System.out.println("Video KK - nicht unterstützt");
                dataStr += "Video " + replaceInvalidChars(kk.getTitel() + " (Übersprungen)") + newLineWithSeparation;
                break;
            case BILD:
                String Strkk = createBildKKLatexStr(kk, depth);
                dataStr += Strkk + newLineWithSeparation;
                break;
            case TEXT:
                String Strkk1 = createTextKKLatexStr(kk, depth);
                dataStr += Strkk1 + newLineWithSeparation;
                break;

            default:
                return false;
        }        
        return true;
    }
    public PDFExportThreadHandler getExecutor()
    {
        return peth;
    }

    private String createTextKKLatexStr(Karteikarte kk, int depth)
    {

        if (kk.getTitel().equals(""))
        {
            return putStrIntoChapter(depth, kk, "");
        }
        else
        {
            String data = kk.getInhalt();
            return putStrIntoChapter(depth, kk, transformHTMLToLaTeX(data));
        }
    }

    private String transformHTMLToLaTeX(String html)
    {
        Document doc = Jsoup.parse("<html><body>" + html + "</body></html>");
        String latexStr = recursiveTransformHTLMtoLatex(doc.body(), 0);
        return latexStr;
    }

    private String recursiveTransformHTLMtoLatex(Element n, int depth)
    {
        List<Element> nodes = n.children();
        String result = "";


        if (!n.nodeName().equals("body"))
        {
//            for (int i = 0; i < depth; i++)
//                System.out.print("  ");
//            System.out.println("<" + n.nodeName() + ">");
            result += mapHTMLTagToLaTeXTag(n, true) + " ";
        }
        
        
        if (nodes.size() == 0)
        {
            String content = n.text();
//            for (int i = 0; i < depth; i++)
//                System.out.print("  ");
//            System.out.println(content);

            result += content;
        }
        else
        {
            for (Element n2 : nodes)
            {
                result += recursiveTransformHTLMtoLatex(n2, depth + 1);
            }
        }
        
        if (!n.nodeName().equals("body"))
        {
//            for (int i = 0; i < depth; i++)
//                System.out.print("  ");
//            System.out.println("</" + n.nodeName() + ">");
            result += mapHTMLTagToLaTeXTag(n, false);
        }
        return result;
    }
    
    private String replaceInvalidChars(String html)
    {
        // TODO 
        html = html.replace("\"", "\\grqq ");
        html = html.replace("ä", "\"a");
        html = html.replace("ö", "\"o");
        html = html.replace("ü", "\"u");
        html = html.replace("Ä", "\"A");
        html = html.replace("Ö", "\"O");
        html = html.replace("Ü", "\"U");
        html = html.replace("ß", "\"s");
        System.out.println(html);
        return html;
    }

    private String mapHTMLTagToLaTeXTag(Node node, boolean begin)
    {
        String res = "";
        switch (node.nodeName().toLowerCase())
        {
            case "br":
                if (begin)
                    res = "\\newline"+ newLineWithSeparation;
                else
                    res = "";
                break;
            // case "p":
            // if(begin)
            // res = "\\paragraph{";
            // else
            // res = "}";
            // break;
            case "span":
                if (begin)
                    res = "";
                else
                    res = "";
                break;
            case "strong":
                if (begin)
                    res = "\\textbf{";
                else
                    res = " }";
                break;
            case "em":
                if (begin)
                    res = "\\textit{";
                else
                    res = "}";
                break;
            case "u":
                if (begin)
                    res = "\\textsuperscript{";
                else
                    res = "}";
                break;
            case "sub":
                if (begin)
                    res = "\\textsubscript{";
                else
                    res = "}";
                break;
            case "ol":
                if (begin)
                    res = "\\begin{enumerate}"+ newLineWithSeparation;
                else
                    res = "\\end{enumerate}"+ newLineWithSeparation;
                break;
            case "ul":
                if (begin)
                    res = "\\begin{itemize}"+ newLineWithSeparation;
                else
                    res = "\\end{itemize}"+ newLineWithSeparation;
                break;
            case "li":
                if (begin)
                    res = "\\item";
                else
                    res = ""+ newLineWithSeparation;
                break;
            case "h1":
            case "h2":
            case "h3":
            case "h4":
            case "h5":
                if (begin)
                    res = "\\textbf{";
                else
                    res = "}"+ newLineWithSeparation;
                break;

            default:
                res = "";
                break;
        }
        return res;
    }

    private String putStrIntoChapter(int chapterDepth, Karteikarte kk, String data)
    {
        String newData = null;
        String title = replaceInvalidChars(kk.getTitel()) ;
        data = replaceInvalidChars(data);
        switch (chapterDepth)
        {
            case 0:
                newData = "\\chapter["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 1:
                newData = "\\section["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 2:
                newData = "\\subsection["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 3:
                newData = "\\subsubsection["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 4:
                newData = "\\paragraph["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
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

            String latexStr = "\\begin{figure}[H] " + newLineWithSeparation + "  \\centering" + newLineWithSeparation
                    + "  \\includegraphics[width=0.5\\linewidth]{" + kk.getId() + ".png}" + newLineWithSeparation
                    + "  \\caption{" + kk.getTitel() + createAttribute(kk) + "}" + newLineWithSeparation + "  \\label{fig:kk_bild"
                    + kk.getId() + "}" + newLineWithSeparation + "\\end{figure}" + newLineWithSeparation;
            
            
            // TODO Bilder mit überschrift ?
            return latexStr;// putStrIntoChapter(depth,kk.getTitel(), latexStr);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public String createAttribute(Karteikarte kk)
    {
        if(!exportAttribute)
            return "";
        
        String tmp = "\\protect\\footnote{Attribute: ";
        int cnt = 0;
        if(kk.isIstSatz())
            tmp += (cnt++==0?"":", ") + "Satz";
        if(kk.isIstBeweis())
            tmp += (cnt++==0?"":", ") + "Beweis";
        if(kk.isIstDefinition())
            tmp += (cnt++==0?"":", ") + "Definition";
        if(kk.isIstWichtig())
            tmp += (cnt++==0?"":", ") + "Wichtig";
        if(kk.isIstGrundlage())
            tmp += (cnt++==0?"":", ") + "Grundlage";
        if(kk.isIstZusatzinfo())
            tmp += (cnt++==0?"":", ") + "Zusatzinfo";
        if(kk.isIstExkurs())
            tmp += (cnt++==0?"":", ") + "Exkurs";
        if(kk.isIstBeispiel())
            tmp += (cnt++==0?"":", ") + "Beispiel";
        if(kk.isIstUebung())
            tmp += (cnt++==0?"":", ") + "Uebung";
        
        tmp += "}";
        if(cnt>0)
            return tmp;
        else
            return "";
    }
    public boolean startConvertToPDFFile()
    {
        if (!createAndCloseFile())
            return false;

        // 2 Mal kompilieren !
        startConvertFile();

        return true;

    }
    public String getPDFFileName()
    {
        return fileName;
    }
    public String getTexFileName()
    {
        return texFileName;
    }
    
    public void deleteFiles()
    {
        if(!peth.creationFinished())
            return;
        
        if(peth.creationSucessfull())
            FileUtils.deleteQuietly(new File(workingDir + subFolder + "/" + fileName));
        
        FileUtils.deleteQuietly(new File(workingDir + subFolder + "/" + texFileName));
    }

    /**
     * Entfernt den temporären Ordner und, dass nur noch die PDF übrig ist.
     */
    public void cleanUp(boolean copyPossible)
    {
        if (!texFileCreated || cleaned)
            return;

        try
        {
            // Copy nach oben
            if(copyPossible)
                FileUtils.copyFile(new File(workingDir + subFolder + "/" + fileName), new File(workingDir + fileName));
            
            // Tex file immer sichern
            FileUtils.copyFile(new File(workingDir + subFolder + "/" + texFileName), new File(workingDir + texFileName));
            
            FileUtils.deleteDirectory(new File(workingDir + subFolder));
            cleaned = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
