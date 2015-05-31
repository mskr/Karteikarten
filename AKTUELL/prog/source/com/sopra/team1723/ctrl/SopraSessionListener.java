package com.sopra.team1723.ctrl;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SopraSessionListener implements HttpSessionListener
{

    @Override
    public void sessionCreated(HttpSessionEvent arg0)
    {
        System.out.println("[SessionListener] Created Session");
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0)
    {
        System.out.println("[SessionListener] Destroyed Session");
        // Prüfen, ob noch alte dateien erzeugt wurden
        PDFExporter pexp = (PDFExporter) arg0.getSession().getAttribute(ServletController.sessionAttributePDFExporter);
        if(pexp != null){
            pexp.deleteFiles();
        }
        arg0.getSession().setAttribute(ServletController.sessionAttributePDFExporter,null);
    }

}
