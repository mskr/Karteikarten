package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import com.sopra.team1723.data.Karteikarte;
import com.sopra.team1723.data.Karteikarte.BeziehungsTyp;
import com.sopra.team1723.data.Notiz;
import com.sopra.team1723.data.Tripel;
import com.sopra.team1723.data.Tupel;
import com.sopra.team1723.data.Veranstaltung;

/**
 * PDF-Exporter für Karteikarten und Veranstaltungen.
 * 
 * @author Andreas R.
 *
 */
public class PDFExporter
{
    private String                                    servletContextPath;
    private String                                    fileName;
    private String                                    texFileName;
    private String                                    workingDir            = "";
    private String                                    subFolder             = "";
    private String                                    dataStr               = "";
    private boolean                                   dataStrOpened         = false;
    private boolean                                   texFileCreated        = false;
    private boolean                                   cleaned               = false;

    private ProcessBuilder                            pb;
    private PDFExportThreadHandler                    peth;
    private Veranstaltung                             vn;
    private boolean                                   exportNotizen         = false;
    private boolean                                   exportAttribute       = false;
    private boolean                                   exportVerweise        = false;

    private static String                             newLineWithSeparation = "\n";
    
    // Lebenszeit der erzeugten Dateien
    private static int                                fileDuration          = 4 * 60 * 1000;

    private static Map<Character, String>             charReplaceList       = new HashMap<>();
    private static Map<String, Tupel<String, String>> tagReplaceList        = new HashMap<>();
    static
    {
        // Zeichen wird ersetzt durch....
        charReplaceList.put('\"', "\\grqq ");
        charReplaceList.put('ä', "\"a");
        charReplaceList.put('ö', "\"o");
        charReplaceList.put('ü', "\"u");
        charReplaceList.put('Ä', "\"A");
        charReplaceList.put('Ö', "\"O");
        charReplaceList.put('Ü', "\"U");
        charReplaceList.put('ß', "\"s");
        charReplaceList.put('#', "\\#");
        charReplaceList.put('%', "\\%");
        charReplaceList.put('~', "\\~");
        // charReplaceList.put("€", "\\texteuro")); // Extra package
        charReplaceList.put('{', "\\{");
        charReplaceList.put('}', "\\}");
        charReplaceList.put('[', "\\[");
        charReplaceList.put(']', "\\]");
        charReplaceList.put('$', "\\$");
        charReplaceList.put('§', "\\§");
        charReplaceList.put('<', "\\textless ");
        charReplaceList.put('>', "\\textgreater ");
        charReplaceList.put('_', "\\_");
        charReplaceList.put('\\', "\\textbackslash "); // Konflikt mit anderen
                                                       // Regeln

        // HTML Tag wird ersetzt durch Start und Ende Tag in Latex
        tagReplaceList.put("pseudo", new Tupel<String, String>("", ""));
        tagReplaceList.put("p", new Tupel<String, String>("", newLineWithSeparation+newLineWithSeparation));
        tagReplaceList.put("br", new Tupel<String, String>("\\newline" + newLineWithSeparation, ""));
        tagReplaceList.put("span", new Tupel<String, String>("", ""));
        tagReplaceList.put("strong", new Tupel<String, String>("\\textbf{", "}"));
        tagReplaceList.put("em", new Tupel<String, String>("\\textit{", "}"));
        tagReplaceList.put("sup", new Tupel<String, String>("\\textsuperscript{", "}"));
        tagReplaceList.put("u", new Tupel<String, String>("\\uline{", "}"));
        tagReplaceList.put("s", new Tupel<String, String>("\\sout{", "}"));
        tagReplaceList.put("sub", new Tupel<String, String>("\\textsubscript{", "}"));
        tagReplaceList.put("ol", new Tupel<String, String>(newLineWithSeparation + "\\begin{enumerate}"
                + newLineWithSeparation, "\\end{enumerate}" + newLineWithSeparation));
        tagReplaceList.put("ul", new Tupel<String, String>(newLineWithSeparation + "\\begin{itemize}"
                + newLineWithSeparation, "\\end{itemize}" + newLineWithSeparation));
        tagReplaceList.put("li", new Tupel<String, String>("\\item ", newLineWithSeparation));
        tagReplaceList.put("h1", new Tupel<String, String>("\\textbf{", "}" + newLineWithSeparation));
        tagReplaceList.put("h2", new Tupel<String, String>("\\textbf{", "}" + newLineWithSeparation));
        tagReplaceList.put("h3", new Tupel<String, String>("\\textbf{", "}" + newLineWithSeparation));
        tagReplaceList.put("h4", new Tupel<String, String>("\\textbf{", "}" + newLineWithSeparation));
        tagReplaceList.put("h5", new Tupel<String, String>("\\textbf{", "}" + newLineWithSeparation));
        tagReplaceList.put("hr", new Tupel<String, String>("\\hrule" + newLineWithSeparation, ""));
        tagReplaceList.put("blockquote", new Tupel<String, String>(newLineWithSeparation + "\\begin{quote}"
                + newLineWithSeparation, newLineWithSeparation + "\\end{quote}" + newLineWithSeparation));

        // Tabellen Mapping muss unten abgefangen werden und 
        // der Latex code muss von hand erzeugt werden. Es muss allerdings eine regel exisiteren, sonst wird
        // Der tag beim konvertierungsvorgang übersprungen.
        tagReplaceList.put("table", new Tupel<String, String>("\\begin{table}[H]" + newLineWithSeparation,
                "\\end{tabular}" + newLineWithSeparation + "\\end{table}"
                        + newLineWithSeparation));
        tagReplaceList.put("td", new Tupel<String, String>("", ""));
        tagReplaceList.put("tr", new Tupel<String, String>("", "\\\\" + newLineWithSeparation + "\\hline"
                + newLineWithSeparation));
    }

    /**
     * Erzeugt einen neuen PDF-Exporter
     * 
     * @param workingDir
     *            Ordner, indem die PDFs generiert werden
     * @param servletContextPath
     *            Pfad des Servlet-Contextes. Wird benötigt um eventuelle Bilder
     *            zu holen.
     * @param vn
     *            Veranstaltungsobjekt
     * @param exportNotizen
     *            True, wenn die Notizen des aktuellen Benutzers mit exportiert
     *            werden sollen.
     * @param exportAttribute
     *            True, wenn die Attribute der Karteikarte mit exportiert werden
     *            sollen.
     * @param exportVerweise
     *            True, wenn Verweise exportiert werden sollen.
     */
    public PDFExporter(String workingDir, String servletContextPath, Veranstaltung vn, boolean exportNotizen,
            boolean exportAttribute, boolean exportVerweise)
    {
        this.exportNotizen = exportNotizen;
        this.exportAttribute = exportAttribute;
        this.exportVerweise = exportVerweise;

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

        dataStr = createHeader();
        dataStr += createVnInhalt();
    }

    /**
     * Erzeugt den Header des TEX-Dokuments
     * 
     * @return
     */
    public String createHeader()
    {
        if (dataStrOpened || cleaned)
            return "";

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
                + "\\usepackage{ulem}          % Erlaubt unter- und durchstreichen"
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
                + "\\usepackage{float}"
                + newLineWithSeparation

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

                + newLineWithSeparation
                + "% Packet für Mathe-Formeln"
                + newLineWithSeparation
                + "\\usepackage{amsmath, amssymb}"
                + newLineWithSeparation

                + newLineWithSeparation
                + "% Für besondere Absätze"
                + newLineWithSeparation
                + "\\parindent 0pt      % Einrückung bei Absatz"
                + newLineWithSeparation
                + "\\parskip 6pt        % Abstände zwischen Absätzen"
                + newLineWithSeparation

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
                + vn.getErsteller().getVorname()
                + " "
                + vn.getErsteller().getNachname()
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

        return header;
    }

    /**
     * Erzeugt das erste Kapitel mit der Veranstaltungsbeschreibung
     * 
     * @return
     */
    private String createVnInhalt()
    {

        if (!dataStrOpened || cleaned)
            return "";

        String data = "\\chapter{Veranstaltungsbeschreibung}" + newLineWithSeparation
                + transformHTMLToLaTeX(vn.getBeschreibung()) + newLineWithSeparation + "\\newpage";

        return data;
    }

    /**
     * Fügt eine neue Karteikarte zum aktuellen Tex dokument hinzu
     * 
     * @param kk
     *            Karteikartenobjekt
     * @param depth
     *            Tiefe in der Hirarchie. Jede Stufe zwischen 0 und 4 wird auf
     *            das LaTeX-Equivalent gemappt. Ein Wert < 0 entfernt die
     *            Karteikarte aus dem Skript bzw. fügt sie garnicht hinzu. Ein
     *            Wert > 4 wird behandelt wie == 4.
     * @param n
     *            Das zugehörige Notiz objekt. Wird null übergeben, wird keine
     *            Notiz angehängt.
     * @return Gibt True zurück, wenn die Karteikarte angehängt werden konnte.
     */
    public boolean appendKarteikarte(Karteikarte kk, int depth, Notiz n)
    {
        if (texFileCreated || cleaned || !dataStrOpened || depth < 0)
            return false;

        System.out.println("Append KK: " + kk.getTitel());
        String Strkk;
        switch (kk.getTyp())
        {
            case VIDEO:
                Strkk = createVideoKKLatexStr(kk, depth);
                dataStr += Strkk + newLineWithSeparation;
                break;
            case BILD:
                Strkk = createBildKKLatexStr(kk, depth);
                dataStr += Strkk + newLineWithSeparation;
                break;
            case TEXT:
                Strkk = createTextKKLatexStr(kk, depth);
                dataStr += Strkk + newLineWithSeparation;
                break;

            default:
                return false;
        }

        dataStr += exportVerweise(kk) + newLineWithSeparation;

        if (exportNotizen && n != null && !n.getInhalt().equals(""))
        {
            dataStr += "\\paragraph{Notizen}" + newLineWithSeparation + transformHTMLToLaTeX(n.getInhalt())
                    + newLineWithSeparation;
        }

        return true;
    }

    /**
     * Erzeugt die Latex-repräsentation der übergebenen Karteikarte
     * 
     * @param kk
     * @param depth
     * @return
     */
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

    /**
     * Erzeugt den Latex code für eine Bild-karteikarte
     * 
     * @param kk
     * @param depth
     * @return
     */
    private String createBildKKLatexStr(Karteikarte kk, int depth)
    {
        String bildPfad = servletContextPath + "/files/images/" + kk.getId() + ".png";
        System.out.println("BildPfad: " + bildPfad);
        System.out.println("Copy to: " + workingDir + subFolder + "/" + kk.getId() + ".png");
        try
        {
            FileUtils.copyFile(new File(bildPfad), new File(workingDir + subFolder + "/" + kk.getId() + ".png"));
            String latexStr;
            if(kk.getInhalt().equals(""))
            {
                latexStr = "\\begin{figure}[H] " + newLineWithSeparation + 
                        "  \\centering" + newLineWithSeparation
                        + "  \\includegraphics[width=0.5\\linewidth]{" + kk.getId() + ".png}" + newLineWithSeparation
                        + "  \\caption[" + replaceInvalidChars(kk.getTitel()) + "]"
                        + "{" + replaceInvalidChars(kk.getTitel()) + createAttribute(kk) + "}" + newLineWithSeparation 
                        + "  \\label{kk_" + kk.getId() + "}" + newLineWithSeparation 
                        + "\\end{figure}" + newLineWithSeparation;

                // TODO Bilder mit überschrift ?
                return latexStr;// putStrIntoChapter(depth,kk.getTitel(), latexStr);
            }
            else
            {
                latexStr = "\\begin{figure}[H] " + newLineWithSeparation + 
                        "  \\centering" + newLineWithSeparation
                        + "  \\includegraphics[width=0.5\\linewidth]{" + kk.getId() + ".png}" + newLineWithSeparation
                        + "  \\caption[" + replaceInvalidChars(kk.getTitel()) + "]"
                        + "{" + replaceInvalidChars(kk.getTitel()) + createAttribute(kk) + "}" + newLineWithSeparation
                        + "\\end{figure}" + newLineWithSeparation
                        + transformHTMLToLaTeX(kk.getInhalt());
                

                // TODO Bilder mit überschrift ?
                return putStrIntoChapter(depth,kk, latexStr);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Erzeugt den Latex code für eine Bild-karteikarte
     * 
     * @param kk
     * @param depth
     * @return
     */
    private String createVideoKKLatexStr(Karteikarte kk, int depth)
    {
        String bildPfad = servletContextPath + "/files/general/noMovie.png";

        try{
            FileUtils.copyFile(new File(bildPfad), new File(workingDir + subFolder + "/noMovie.png"));
        }
        catch(Exception e){}

        String latexStr = "\\begin{figure}[H] " + newLineWithSeparation + 
                "  \\centering" + newLineWithSeparation
                + "  \\includegraphics[width=0.5\\linewidth]{noMovie.png}" + newLineWithSeparation
                + "  \\label{kk_" + kk.getId() + "}" + newLineWithSeparation 
                + "\\end{figure}" + newLineWithSeparation;

        return putStrIntoChapter(depth,kk, latexStr);
    }

    /**
     * Erzeugt einen LaTex-String der die exportierten Verweise als klickbare
     * Links enthält.
     * 
     * @param kk
     *            Karteikartenobjekt
     * @return Gibt den LaTeX-String zurück
     */
    private String exportVerweise(Karteikarte kk)
    {
        if (!exportVerweise || kk.getVerweise().size() == 0)
            return "";

        String dataStr = "\\paragraph{Querverweise}" + newLineWithSeparation + "\\begin{description}"
                + newLineWithSeparation;

        ArrayList<Tupel<Integer, String>> vorraussetzung = new ArrayList<>();
        ArrayList<Tupel<Integer, String>> zusatz = new ArrayList<>();
        ArrayList<Tupel<Integer, String>> sonstiges = new ArrayList<>();
        ArrayList<Tupel<Integer, String>> uebung = new ArrayList<>();

        for (Tripel<BeziehungsTyp, Integer, String> v : kk.getVerweise())
        {
            if (v.x == BeziehungsTyp.V_VORAUSSETZUNG)
                vorraussetzung.add(new Tupel<Integer, String>(v.y, v.z));
            else if (v.x == BeziehungsTyp.V_UEBUNG)
                uebung.add(new Tupel<Integer, String>(v.y, v.z));
            else if (v.x == BeziehungsTyp.V_SONSTIGES)
                sonstiges.add(new Tupel<Integer, String>(v.y, v.z));
            else if (v.x == BeziehungsTyp.V_ZUSATZINFO)
                zusatz.add(new Tupel<Integer, String>(v.y, v.z));
        }

        if (vorraussetzung.size() > 0)
        {
            dataStr += "\\item[Vorraussetzung:]" + newLineWithSeparation;

            for (int i = 0; i < vorraussetzung.size(); i++)
            {
                Tupel<Integer, String> t = vorraussetzung.get(i);
                dataStr += "\\nameref{kk_" + t.x + "}";
                if (i < vorraussetzung.size() - 1)
                    dataStr += ", ";
            }
            dataStr += newLineWithSeparation;
        }
        if (uebung.size() > 0)
        {
            dataStr += "\\item[\"Ubung:]" + newLineWithSeparation;

            for (int i = 0; i < uebung.size(); i++)
            {
                Tupel<Integer, String> t = uebung.get(i);
                dataStr += "\\nameref{kk_" + t.x + "}";
                if (i < uebung.size() - 1)
                    dataStr += ", ";
            }
            dataStr += newLineWithSeparation;
        }
        if (zusatz.size() > 0)
        {
            dataStr += "\\item[Zusatzinfo:]" + newLineWithSeparation;

            for (int i = 0; i < zusatz.size(); i++)
            {
                Tupel<Integer, String> t = zusatz.get(i);
                dataStr += "\\nameref{kk_" + t.x + "}";
                if (i < zusatz.size() - 1)
                    dataStr += ", ";
            }
            dataStr += newLineWithSeparation;
        }
        if (sonstiges.size() > 0)
        {
            dataStr += "\\item[Sonstiges:]" + newLineWithSeparation;

            for (int i = 0; i < sonstiges.size(); i++)
            {
                Tupel<Integer, String> t = sonstiges.get(i);
                dataStr += "\\nameref{kk_" + t.x + "}";
                if (i < sonstiges.size() - 1)
                    dataStr += ", ";
            }
            dataStr += newLineWithSeparation;
        }

        dataStr += "\\end{description}" + newLineWithSeparation;
        return dataStr;
    }

    /**
     * Parst den übergebenen HTML-String und wandelt ihn in sein
     * Latex-Equivalent um.
     * 
     * @param html
     * @return
     */
    private String transformHTMLToLaTeX(String html)
    {
        // Reguläre ausdrücke sind notwendig um den Text der in keinem seperaten html tag steht in ein pseudo tag zu packen.
        // Die regulären ausdrücke haben folgende Bedeutung:
        /**
         * (>) = Ende eines html-tags
         * ([^<]{1,}) = Aller text, der kein tag-anfang-symbol (<) enthält. Also der Text der gesucht wird.
         * (<[^/]) = html tag der KEIN ende ist 
         * 
         * -> Reagiert auf folgende Struktur:
         * <tag>gesuchterText<andererTag>
         */
        html = html.replaceAll("(>)([^<]{1,})(<[^/])", "$1<pseudo>$2</pseudo>$3");
        /**
         * (<[/][\\w]{0,1}>) = Html-Tag, der ein ende-tag ist
         * ([^<]{1,}) = Aller text, der kein tag-anfang-symbol (<) enthält. Also der Text der gesucht wird.
         * (<[/]) = html tag der ende ist.
         * 
         * -> Reagiert auf folgende Struktur:
         * </tag>gesuchterText</andererTag>
         */
        html = html.replaceAll("(<[/][\\w\\s]{1,}>)([^<]{1,})(<[/])", "$1<pseudo>$2</pseudo>$3");
        /**
         * (\\/>) = Html-Tag, der ein self-closing-tag ist (br)
         * ([^<]{1,}) = Aller text, der kein tag-anfang-symbol (<) enthält. Also der Text der gesucht wird.
         * (<[/]) = html tag der ende ist.
         * 
         * -> Reagiert auf folgende Struktur:
         * <tag />gesuchterText</andererTag>
         */
        html = html.replaceAll("(\\/>)([^<]{1,})(<\\/)", "$1<pseudo>$2</pseudo>$3");
        
        /**
         * Non-breakable-Spaces ersetzen
         */
        html = html.replaceAll("(&nbsp;)", " ");
        
        Document doc = Jsoup.parse("<html><body>" + html + "</body></html>");
        
        
        String latexStr = recursiveTransformHTLMtoLatex(doc.body(), 0);
        return latexStr;
    }

    /**
     * Läuft rekursiv durch den HTML-Baum durch und erzeugt die
     * Latex-Repräsentation.
     * 
     * @param n
     * @param depth
     * @return
     */
    private String recursiveTransformHTLMtoLatex(Element n, int depth)
    {
        List<Element> nodes = n.children();
        String result = "";

        if (!n.nodeName().equals("body"))
        {
            for(int i = 0; i < depth; i++)
                System.out.print(" ");
            System.out.println("<" + n.nodeName() + ">");
            String tag = mapHTMLTagToLaTeXTag(n, true);
            result += tag;
        }

        String content = n.ownText();
        // Workaround um an text ohne trimming zu kommen
        if(!content.equals(""))
        {
            content = n.textNodes().get(0).getWholeText();
        }
        
        if (nodes.size() == 0)
        {

            for(int i = 0; i < depth; i++)
                System.out.print(" ");
            System.out.println("Content: " + content);
            
            if (n.attr("class").equals("mathjax_formel"))
            {
                System.out.println("Skip replacing. Formel gefunden");
                result += content;
            }
            else
                result += replaceInvalidChars(content);
        }
        else
        {
//            Node firstChild = n.childNode(0);
//            if(firstChild instanceof TextNode)
//            {
//                result += replaceInvalidChars(((TextNode) firstChild).getWholeText());
//            }

            for(int i = 0; i < depth; i++)
                System.out.print(" ");
            System.out.println("Content aber noch kinder: " + content);

            for(int i = 0; i < depth; i++)
                System.out.print(n.text());
            
            System.out.println(" Text: " + content);
            
            result += replaceInvalidChars(content);
            
            for (Element n2 : nodes)
            {
                result += recursiveTransformHTLMtoLatex(n2, depth + 1);
            }
        }

        if (!n.nodeName().equals("body"))
        {
            for(int i = 0; i < depth; i++)
                System.out.print(" ");
            System.out.println("</" + n.nodeName() + ">");
            String tag = mapHTMLTagToLaTeXTag(n, false);
            result += tag;
        }
        return result;
    }

    /**
     * Ersetzt ungültige zeichen durch die Latex repräsentation
     * 
     * @param html
     * @return
     */
    private String replaceInvalidChars(String html)
    {
        String result = "";
        for (int i = 0; i < html.length(); i++)
        {
            String replacer = charReplaceList.get(html.charAt(i));
            if (replacer == null)
                result += html.charAt(i);
            else
                result += replacer;
        }
        return result;
    }

    /**
     * Liefert den Latex code zurück, der den angegebenen HTML-Knoten
     * repräsentiert.
     * 
     * @param elem
     * @param begin
     * @return
     */
    private String mapHTMLTagToLaTeXTag(Element elem, boolean begin)
    {
        String nodeName = elem.nodeName();
        Tupel<String, String> replacer = tagReplaceList.get(nodeName);
        if (replacer == null)
        {
            if (begin)
                System.out.println("[PDF-EXPORTER]: Info -> HTML-Tag \"" + elem.nodeName()
                        + "\" nicht bekannt und daher übersprungen.");
            return "";
        }


        if (nodeName.equals("table"))
        {
            if (begin)
            {

                int colCnt = 0;
                String caption = null;

                colCnt = elem.getElementsByTag("tbody").get(0).child(0).getElementsByTag("td").size();

                if (elem.getElementsByTag("caption").size() != 0)
                {
                    caption = elem.getElementsByTag("caption").first().text();
                }

                String tableConfig = "{|";
                for (int i = 0; i < colCnt; i++)
                {
                    tableConfig += "c|";
                }
                tableConfig += "}";

                if (caption == null)
                    return replacer.x + "\\centering" + newLineWithSeparation + "\\begin{tabular}" + newLineWithSeparation + tableConfig
                            + newLineWithSeparation + "\\hline" + newLineWithSeparation;
                else
                    return replacer.x + "\\caption{" + replaceInvalidChars(caption) + "}" + newLineWithSeparation
                            + "\\centering" + newLineWithSeparation + "\\begin{tabular}" + newLineWithSeparation
                            + tableConfig + newLineWithSeparation + "\\hline" + newLineWithSeparation;
            }
            else
                return replacer.y;
        }
        else if (nodeName.equals("td"))
        {
            if (begin)
            {
                int idx = elem.siblingIndex();
                if (idx == 0)
                    return replacer.x;
                else
                    return " & " + replacer.x;
            }
            else
                return replacer.y;
        }
        else
        {
            if (begin)
                return replacer.x;
            else
                return replacer.y;
        }
    }

    /**
     * Packt den übergebenen Latex String in ein Kapitel mit dem Titel der
     * Karteikarte.
     * 
     * @param chapterDepth
     * @param kk
     * @param data
     * @return
     */
    private String putStrIntoChapter(int chapterDepth, Karteikarte kk, String data)
    {
        String newData = null;
        String title = replaceInvalidChars(kk.getTitel());

        switch (chapterDepth)
        {
            case 0:
                newData = "\\chapter[" + title + "]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 1:
                newData = "\\section[" + title + "]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 2:
                newData = "\\subsection[" + title + "]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 3:
                newData = "\\subsubsection[" + title + "]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 4:
            default:
                newData = "\\paragraph[" + title + "]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
        }
        newData += "\\label{kk_" + kk.getId() + "}";
        return newData + newLineWithSeparation + data;
    }

    /**
     * Erzeugt den String für die Attribute einer Karteikarte
     * 
     * @param kk
     * @return
     */
    public String createAttribute(Karteikarte kk)
    {
        if (!exportAttribute)
            return "";

        String tmp = "\\protect\\footnote{Attribute: ";
        int cnt = 0;
        if (kk.isIstSatz())
            tmp += (cnt++ == 0 ? "" : ", ") + "Satz";
        if (kk.isIstBeweis())
            tmp += (cnt++ == 0 ? "" : ", ") + "Beweis";
        if (kk.isIstDefinition())
            tmp += (cnt++ == 0 ? "" : ", ") + "Definition";
        if (kk.isIstWichtig())
            tmp += (cnt++ == 0 ? "" : ", ") + "Wichtig";
        if (kk.isIstGrundlage())
            tmp += (cnt++ == 0 ? "" : ", ") + "Grundlage";
        if (kk.isIstZusatzinfo())
            tmp += (cnt++ == 0 ? "" : ", ") + "Zusatzinfo";
        if (kk.isIstExkurs())
            tmp += (cnt++ == 0 ? "" : ", ") + "Exkurs";
        if (kk.isIstBeispiel())
            tmp += (cnt++ == 0 ? "" : ", ") + "Beispiel";
        if (kk.isIstUebung())
            tmp += (cnt++ == 0 ? "" : ", ") + "Uebung";

        tmp += "}";
        if (cnt > 0)
            return tmp;
        else
            return "";
    }

    /**
     * Schließt das aktelle Tex-Dokument und startet den Kompilierungsvorgang.
     * 
     * @return
     */
    public boolean startConvertToPDFFile()
    {
        if (!createAndCloseFile())
            return false;

        startConvertFile();

        return true;
    }

    /**
     * Beendet das Hinzufügen von Karteikarten zum aktuellen Tex-Dokument und
     * schreibt den generierten Text in die Datei.
     * 
     * @return True, wenn die Datei erzeugt und gefüllt werden konnte.
     */
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

    /**
     * Erzeugt einen neuen Thread, der dann pdflatex startet und das PDF
     * kompiliert.
     * 
     * @return True, wenn der Thread gestartet werden konnte
     */
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

    /**
     * Leifert den handler zurück, der die kompilierung des tex Dokuments
     * übernimmt.
     * 
     * @return
     */
    public PDFExportThreadHandler getExecutor()
    {
        return peth;
    }

    public String getPDFFileName()
    {
        return fileName;
    }

    public String getTexFileName()
    {
        return texFileName;
    }

    /**
     * Löscht alle erzeugten Dateien
     */
    public void deleteFiles()
    {
        if (!peth.creationFinished())
            return;
        System.out.println("[PDF-EXPORTER]: Lösche erstellte Dateien.");

        if (peth.creationSucessfull())
            FileUtils.deleteQuietly(new File(workingDir + fileName));

        FileUtils.deleteQuietly(new File(workingDir + texFileName));
    }

    /**
     * Entfernt den temporären Ordner, dass nur noch die PDF und das
     * tex-Dokument übrig ist.
     */
    public void cleanUp(boolean copyPossible)
    {
        if (!texFileCreated || cleaned)
            return;

        try
        {
            // Copy nach oben
            if (copyPossible)
                FileUtils.copyFile(new File(workingDir + subFolder + "/" + fileName), new File(workingDir + fileName));
            
            // Tex file immer sichern
            FileUtils.copyFile(new File(workingDir + subFolder + "/" + texFileName), new File(workingDir + texFileName));
            
            FileUtils.deleteDirectory(new File(workingDir + subFolder));
            
            // Timer für das löschen der Dateien
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    deleteFiles();
                }
              }, fileDuration);
            
            cleaned = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
