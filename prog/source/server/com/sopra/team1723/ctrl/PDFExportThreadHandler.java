package com.sopra.team1723.ctrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Startet den Kompilierungsvorgang der PDF in einem eigenen Thread und speichert das Ergebnis.
 * 
 * @author Andreas R.
 *
 */
public class PDFExportThreadHandler implements Runnable
{
    private boolean        creationFinised    = false;
    private boolean        creationSucessfull = false;
    private ProcessBuilder pb;
    private PDFExporter    pe;

    // Bekommt den Exporter übergeben sowie den ProcessBuilder, der den latex-kompiler startet
    public PDFExportThreadHandler(PDFExporter pe, ProcessBuilder pb)
    {
        this.pb = pb;
        this.pe = pe;
    }

    /**
     * Erzeugt die PDF
     */
    @Override
    public void run()
    {
        try
        {
            // Latex dokumente müssen 2 mal kompiliert werden, da sonst das Inhaltsverzeichniss
            // nicht angezeigt wird. Das wird erst beim 2. Latex lauf erzeugt.
            int copileRunNr = 0;
            int res = 0;
            for (; copileRunNr < 2; copileRunNr++)
            {

                System.out.println("[PDFEXPORT-THREAD-HANDLER]: " + String.valueOf(copileRunNr + 1)
                        + ".Lauf von pdflatex warte auf Fertigstellung...");
                Process p = pb.start();

                // Es muss ein extra Thread erzeugt werden, indem alles gelesen wird, 
                // was der Latex-kompiler ausgibt. Andernfalls würde der Thread blockieren.
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        // Reader des inputstreams erzegen
                        InputStream is = p.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        // Daten lesen, solange der Prozess aktiv ist.
                        while (p.isAlive())
                        {
                            try
                            {
                                // Daten einfach lesen und dann verwerfen. wir benötigen sie nicht.
                                reader.readLine();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                // Warten, bis der latex process fertig ist. Dies legt den aktuellen Prozess schlafen.
                res = p.waitFor();
                
                System.out.println("[PDFEXPORT-THREAD-HANDLER]: " + String.valueOf(copileRunNr + 1) + ".Lauf fertig...");
                // Falls res != 0 ist ein Fehler beim kompilierungsvorgang aufgetreten. Dann abbrechen
                if (res != 0)
                    break;
            }
            if (res != 0)
            {
                System.out.println("[PDFEXPORT-THREAD-HANDLER]: " + "Errorcode von pdflatex: " + res);
                creationSucessfull = false;
            }
            else
            {
                System.out.println("[PDFEXPORT-THREAD-HANDLER]: " + "PDF erfolgreich erzeugt.");
                creationSucessfull = true;
            }
        }
        catch (IOException | InterruptedException ex)
        {
            creationSucessfull = false;
            ex.printStackTrace();
        }
        finally
        {
            creationFinised = true;
            pe.cleanUp(creationSucessfull);
        }
    }

    public boolean creationFinished()
    {
        return creationFinised;
    }

    public boolean creationSucessfull()
    {
        return creationSucessfull;
    }

}
