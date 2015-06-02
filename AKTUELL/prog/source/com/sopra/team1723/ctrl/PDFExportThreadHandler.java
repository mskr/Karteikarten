package com.sopra.team1723.ctrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Startet den Kompilierungsvorgang der PDF und speichert das Ergebnis.
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
            int copileRunNr = 0;
            int res = 0;
            for (; copileRunNr < 2; copileRunNr++)
            {

                System.out.println("[PDFEXPORT-THREAD-HANDLER]: " + String.valueOf(copileRunNr + 1)
                        + ".Lauf von pdflatex warte auf Fertigstellung...");
                Process p = pb.start();

                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        InputStream is = p.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                        while (p.isAlive())
                        {
                            try
                            {
                                reader.readLine();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

                res = p.waitFor();
                System.out
                        .println("[PDFEXPORT-THREAD-HANDLER]: " + String.valueOf(copileRunNr + 1) + ".Lauf fertig...");
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
