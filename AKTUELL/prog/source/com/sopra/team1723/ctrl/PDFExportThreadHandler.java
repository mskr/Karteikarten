package com.sopra.team1723.ctrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PDFExportThreadHandler implements Runnable
{

    private boolean        creationStarted       = false;
    private boolean        creationFinised       = false;
    private boolean        creationSucessfull    = false;
    private ProcessBuilder pb;
    private PDFExporter pe;
    public PDFExportThreadHandler( PDFExporter pe, ProcessBuilder pb)
    {
        this.pb = pb;
        this.pe = pe;
    }
    public boolean creationFinished()
    {
        return creationFinised;
    }
    public boolean creationSucessfull()
    {
        return creationSucessfull;
    }
    @Override
    public void run()
    {
        try
        {
            int copileRunNr = 0;
            int res = 0;
            for(; copileRunNr < 2; copileRunNr++)
            {
                
                System.out.println(String.valueOf(copileRunNr+1) + ".Run of pdflatex-Process and wait for finish...");
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
               System.out.println(String.valueOf(copileRunNr+1) + ".Run Finished..."); 
               if(res != 0)
                   break;
            }
            if (res != 0)
            {
                System.out.println("Errorcode von pdflatex: " + res);
                creationSucessfull = false;
            }
            else
            {
                creationSucessfull = true;
            }
        }
        catch (IOException | InterruptedException ex)
        {
            creationSucessfull = false;
            ex.printStackTrace();
        }
        finally{
            creationFinised = true;
            pe.cleanUp(creationSucessfull);
        }
    }

}
