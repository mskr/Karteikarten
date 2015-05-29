package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import com.sopra.team1723.data.Karteikarte;

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

    private String         Titel                 = "";
    private String         Author                = "";

    private ProcessBuilder pb;
    private PDFExportThreadHandler peth;

    public PDFExporter(String workingDir, String servletContextPath)
    {
        this.workingDir = workingDir + "/";
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

    public void setInfo(String Author, String Titel)
    {
        this.Titel = Titel;
        this.Author = Author;
    }

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
                + Titel
                + "}"
                + newLineWithSeparation
                + "\\author{"
                + Author
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
                return false; // Nicht unterstützt
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

        if (kk.getTitel().equals(""))
        {
            return putStrIntoChapter(depth, kk.getTitel(), "");
        }
        else
        {
            String data = kk.getInhalt();
            return putStrIntoChapter(depth, kk.getTitel(), transformHTMLToLaTeX(data));
        }
    }

    private String transformHTMLToLaTeX(String html)
    {
        Document doc = Jsoup.parseBodyFragment(html);
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
            result += mapHTMLTagToLaTeXTag(n.nodeName(), true) + " ";
            
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
            result += mapHTMLTagToLaTeXTag(n.nodeName(), false);
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

    private String mapHTMLTagToLaTeXTag(String nodeNameHTML, boolean begin)
    {
        String res = "";
        switch (nodeNameHTML.toLowerCase())
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

            default:
                res = "";
                break;
        }
        return res;
    }

    private String putStrIntoChapter(int chapterDepth, String title, String data)
    {
        String newData = null;
        title = replaceInvalidChars(title);
        data = replaceInvalidChars(data);
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

            String latexStr = "\\begin{figure}[H] " + newLineWithSeparation + "  \\centering" + newLineWithSeparation
                    + "  \\includegraphics[width=0.5\\linewidth]{" + kk.getId() + ".png}" + newLineWithSeparation
                    + "  \\caption{" + kk.getTitel() + "}" + newLineWithSeparation + "  \\label{fig:kk_bild"
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

    public boolean startConvertToPDFFile()
    {
        if (!createAndCloseFile())
            return false;

        // 2 Mal kompilieren !
        startConvertFile();

        return true;

    }
    public String getFileName()
    {
        return fileName;
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
            FileUtils.deleteDirectory(new File(workingDir + subFolder));
            cleaned = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
