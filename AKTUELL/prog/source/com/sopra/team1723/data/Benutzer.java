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
    
    // Konstruktor der alle Parameter setzt
    public Benutzer(int id, String eMail, String vorname, String nachname, int matrikelnummer, String studiengang,
            String kennwort, Nutzerstatus nutzerstatus, boolean notifyVeranstAenderung,
            boolean notifyKarteikartenAenderung, NotifyKommentare notifyKommentare)
    {
        this.id = id;
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
    
    // Konstruktor der alle Attribute setzt bis auf id
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

    // Konstruktur ohne Kennwort, Matrikelnummer, Studiengang, Nutzerstatus (Die letzten 3 können vom Benutzer
    // selbst nicht geändert werden
    public Benutzer(String eMail, String vorname, String nachname, boolean notifyVeranstAenderung,
            boolean notifyKarteikartenAenderung, NotifyKommentare notifyKommentare)
    {
        super();
        this.eMail = eMail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.notifyVeranstAenderung = notifyVeranstAenderung;
        this.notifyKarteikartenAenderung = notifyKarteikartenAenderung;
        this.notifyKommentare = notifyKommentare;
    }

    // Konstruktor ohne Kennwort
    public Benutzer(String eMail, String vorname, String nachname, int matrikelnummer, String studiengang,
            Nutzerstatus nutzerstatus, boolean notifyVeranstAenderung, boolean notifyKarteikartenAenderung,
            NotifyKommentare notifyKommentare)
    {
        super();
        this.eMail = eMail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.matrikelnummer = matrikelnummer;
        this.studiengang = studiengang;
        this.nutzerstatus = nutzerstatus;
        this.notifyVeranstAenderung = notifyVeranstAenderung;
        this.notifyKarteikartenAenderung = notifyKarteikartenAenderung;
        this.notifyKommentare = notifyKommentare;
    }

    // Konstruktor ohne die persönlichen Einstellungen des Benutzers
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

    public String geteMail()
    {
        return eMail;
    }
    
    public void seteMail(String eMail)
    {
        this.eMail = eMail;
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
    
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    
    
    public String getProfilBildPfad()
    {
        return profilBildPfad;
    }

    public void setProfilBildPfad(String profilBildPfad)
    {
        this.profilBildPfad = profilBildPfad;
    }

    /**
     * 
     */
    private int id;
    
    /**
     * 
     */
    private String eMail;
     
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
    
    /**
     * 
     */
    private String profilBildPfad;

}