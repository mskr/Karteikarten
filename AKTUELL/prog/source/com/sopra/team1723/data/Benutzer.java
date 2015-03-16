package com.sopra.team1723.data;

import java.util.*;

/**
 * 
 */
public class Benutzer {

    /**
     * 
     */
    public Benutzer() {
    }
    
    public Benutzer(String eMail, String vorname, String nachname, int matrikelnummer, String studiengang,
            String kennwort, Nutzerstatus nutzerstatus, boolean notifyVeranstAenderung,
            boolean notifyKarteikartenAenderung, NotifyKommentare notifyKommentare)
    {
        this.eMail = eMail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.matrikelnummer = matrikelnummer;
        this.studiengang = studiengang;
        this.kennwort = kennwort;
        this.nutzerstatus = nutzerstatus;
        this.notifyVeranstAenderung = notifyVeranstAenderung;
        this.notifyKarteikartenAenderung = notifyKarteikartenAenderung;
        this.notifyKommentare = notifyKommentare;
    }
    
    public Benutzer(String eMail, String vorname, String nachname, int matrikelnummer, String studiengang,
            String kennwort)
    {
        this.eMail = eMail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.matrikelnummer = matrikelnummer;
        this.studiengang = studiengang;
        this.kennwort = kennwort;
        this.nutzerstatus = Nutzerstatus.STUDENT;
        this.notifyVeranstAenderung = false;
        this.notifyKarteikartenAenderung = false;
        this.notifyKommentare = NotifyKommentare.KEINE;
    }

    /**
     * 
     */
    private String eMail;

    public String geteMail()
    {
        return eMail;
    }


    public String getVorname()
    {
        return vorname;
    }

    public void setVorname(String vorname)
    {
        this.vorname = vorname;
    }

    public String getNachname()
    {
        return nachname;
    }

    public void setNachname(String nachname)
    {
        this.nachname = nachname;
    }

    public int getMatrikelnummer()
    {
        return matrikelnummer;
    }

    public boolean isNotifyVeranstAenderung()
    {
        return notifyVeranstAenderung;
    }

    public void setNotifyVeranstAenderung(boolean notifyVeranstAenderung)
    {
        this.notifyVeranstAenderung = notifyVeranstAenderung;
    }

    public boolean isNotifyKarteikartenAenderung()
    {
        return notifyKarteikartenAenderung;
    }

    public void setNotifyKarteikartenAenderung(boolean notifyKarteikartenAenderung)
    {
        this.notifyKarteikartenAenderung = notifyKarteikartenAenderung;
    }

    public NotifyKommentare getNotifyKommentare()
    {
        return notifyKommentare;
    }

    public void setNotifyKommentare(NotifyKommentare notifyKommentare)
    {
        this.notifyKommentare = notifyKommentare;
    }

    public void setMatrikelnummer(int matrikelnummer)
    {
        this.matrikelnummer = matrikelnummer;
    }

    public String getStudiengang()
    {
        return studiengang;
    }

    public void setStudiengang(String studiengang)
    {
        this.studiengang = studiengang;
    }

    public String getKennwort()
    {
        return kennwort;
    }

    public void setKennwort(String kennwort)
    {
        this.kennwort = kennwort;
    }

    public Nutzerstatus getNutzerstatus()
    {
        return nutzerstatus;
    }

    public void setNutzerstatus(Nutzerstatus nutzerstatus)
    {
        this.nutzerstatus = nutzerstatus;
    }

    /**
     * 
     */
    private String vorname;

    /**
     * 
     */
    private String nachname;

    /**
     * 
     */
    private int matrikelnummer;

    /**
     * 
     */
    private String studiengang;

    /**
     * 
     */
    private String kennwort;

    /**
     * 
     */
    private Nutzerstatus nutzerstatus;
   

    /**
     * 
     */
    private boolean notifyVeranstAenderung;

    /**
     * 
     */
    private boolean notifyKarteikartenAenderung;

    /**
     * 
     */
    private NotifyKommentare notifyKommentare;

}