package com.sopra.team1723.data;


/**
 * 
 */
public class Veranstaltung {

    
    
    public Veranstaltung(int id, String titel, String beschreibung, String semester,
            String zugangspasswort, boolean bewertungenErlaubt, boolean moderatorKarteikartenBearbeiten,
            int ersteller, boolean kommentareErlaubt, int anzTeilnehmer)
    {
        this.id = id;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.semester = semester;
        this.zugangspasswort = zugangspasswort;
        this.bewertungenErlaubt = bewertungenErlaubt;
        this.moderatorKarteikartenBearbeiten = moderatorKarteikartenBearbeiten;
        this.kommentareErlaubt = kommentareErlaubt;
        this.ersteller = ersteller;
        this.anzTeilnehmer = anzTeilnehmer;
    }
    
 


    /**
     * 
     */
    public Veranstaltung() {
    }
    /**
     * 
     */
    private int id;
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
    private int anzTeilnehmer;
    
    private int ersteller;

    public int getId()
    {
        return id;
    }




    public void setId(int id)
    {
        this.id = id;
    }




    public String getTitel()
    {
        return titel;
    }




    public void setTitel(String titel)
    {
        this.titel = titel;
    }




    public String getBeschreibung()
    {
        return beschreibung;
    }




    public void setBeschreibung(String beschreibung)
    {
        this.beschreibung = beschreibung;
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




    public boolean isKommentareErlaubt()
    {
        return kommentareErlaubt;
    }




    public void setKommentareErlaubt(boolean kommentareErlaubt)
    {
        this.kommentareErlaubt = kommentareErlaubt;
    }




    public boolean isBewertungenErlaubt()
    {
        return bewertungenErlaubt;
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




    public int getErsteller()
    {
        return ersteller;
    }




    public void setErsteller(int ersteller)
    {
        this.ersteller = ersteller;
    }




    public int getAnzTeilnehmer()
    {
        return anzTeilnehmer;
    }




    public void setAnzTeilnehmer(int anzTeilnehmer)
    {
        this.anzTeilnehmer = anzTeilnehmer;
    }


    

}