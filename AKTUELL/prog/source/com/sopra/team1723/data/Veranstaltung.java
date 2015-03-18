package com.sopra.team1723.data;

import java.util.*;

/**
 * 
 */
public class Veranstaltung {

    
    
    public Veranstaltung(String titel, String beschreibung, String studiengang, String semester,
            String zugangspasswort, boolean bewertungenErlaubt, boolean moderatorKarteikartenBearbeiten,
            String ersteller, ArrayList<String> moderatoren, boolean kommentareErlaubt)
    {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.studiengang = studiengang;
        this.semester = semester;
        this.zugangspasswort = zugangspasswort;
        this.bewertungenErlaubt = bewertungenErlaubt;
        this.moderatorKarteikartenBearbeiten = moderatorKarteikartenBearbeiten;
        this.kommentareErlaubt = kommentareErlaubt;
        this.ersteller = ersteller;
        this.moderatoren = new ArrayList<String>(moderatoren);
    }

    /**
     * 
     */
    public Veranstaltung() {
    }

    /**
     * 
     */
    private String titel;

    /**
     * 
     */
    private String beschreibung;

    /**
     * 
     */
    private String studiengang;

    /**
     * 
     */
    private String semester;

    /**
     * 
     */
    private String zugangspasswort;

    /**
     * 
     */

    private boolean kommentareErlaubt;
    /**
     * 
     */
    
    private boolean bewertungenErlaubt;

    /**
     * 
     */
    private boolean moderatorKarteikartenBearbeiten;

    /**
     * 
     */
    private String ersteller;

    /**
     * 
     */
    private ArrayList<String> moderatoren;
    
    /**
     * 
     */
    private boolean diskussionenErlaubt;

    public String getBeschreibung()
    {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung)
    {
        this.beschreibung = beschreibung;
    }

    public String getStudiengang()
    {
        return studiengang;
    }

    public void setStudiengang(String studiengang)
    {
        this.studiengang = studiengang;
    }

    public String getSemester()
    {
        return semester;
    }

    public void setSemester(String semester)
    {
        this.semester = semester;
    }

    public String getZugangspasswort()
    {
        return zugangspasswort;
    }

    public void setZugangspasswort(String zugangspasswort)
    {
        this.zugangspasswort = zugangspasswort;
    }

    public boolean isDiskussionenErlaubt()
    {
        return diskussionenErlaubt;
    }

    public void setBewertungenErlaubt(boolean bewertungenErlaubt)
    {
        this.bewertungenErlaubt = bewertungenErlaubt;
    }

    public boolean isModeratorKarteikartenBearbeiten()
    {
        return moderatorKarteikartenBearbeiten;
    }

    public void setModeratorKarteikartenBearbeiten(boolean moderatorKarteikartenBearbeiten)
    {
        this.moderatorKarteikartenBearbeiten = moderatorKarteikartenBearbeiten;
    }

    public String getErsteller()
    {
        return ersteller;
    }

    public void setErsteller(String ersteller)
    {
        this.ersteller = ersteller;
    }

    public ArrayList<String> getModeratoren()
    {
        return moderatoren;
    }

    public void setModeratoren(int[] moderatorenIDs)
    {
        this.moderatoren = new ArrayList<String>(moderatoren);
    }

    public String getTitel()
    {
        return titel;
    }

    public boolean isKommentareErlaubt()
    {
        return kommentareErlaubt;
    }

    public void setKommentareErlaubt(boolean kommentareErlaubt)
    {
        this.kommentareErlaubt = kommentareErlaubt;
    }

}