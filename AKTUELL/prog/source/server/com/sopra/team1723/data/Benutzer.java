package com.sopra.team1723.data;

import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.IjsonObject;
import com.sopra.team1723.ctrl.ParamDefines;


/**
 * 
 */
public class Benutzer implements IjsonObject {

    private String theme;

    /**
     * 
     */
    public Benutzer() {
        this.nutzerstatus = Nutzerstatus.STUDENT;
        this.notifyVeranstAenderung = false;
        this.notifyKarteikartenAenderung = false;
        this.notifyKommentare = NotifyKommentare.KEINE;
        this.profilBild = "default.png";
    }
    
    // Konstruktor der alle Parameter setzt
    public Benutzer(int id, String eMail, String vorname, String nachname, int matrikelnummer, String studiengang,
            String kennwort, Nutzerstatus nutzerstatus, boolean notifyVeranstAenderung,
            boolean notifyKarteikartenAenderung, NotifyKommentare notifyKommentare, String profilBild, String theme)
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
        this.profilBild = profilBild;
        this.theme = theme;
    }
    
    // Konstruktur ohne Kennwort, Matrikelnummer, Studiengang, Nutzerstatus (Die letzten 3 können vom Benutzer
    // selbst nicht geändert werden
    public Benutzer(int id, String eMail, String vorname, String nachname, boolean notifyVeranstAenderung,
            boolean notifyKarteikartenAenderung, NotifyKommentare notifyKommentare)
    {
        this.id = id;
        this.eMail = eMail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.notifyVeranstAenderung = notifyVeranstAenderung;
        this.notifyKarteikartenAenderung = notifyKarteikartenAenderung;
        this.notifyKommentare = notifyKommentare;
    }

    // Konstruktor ohne Kennwort
    public Benutzer(int id, String eMail, String vorname, String nachname, int matrikelnummer, String studiengang,
            Nutzerstatus nutzerstatus, boolean notifyVeranstAenderung, boolean notifyKarteikartenAenderung,
            NotifyKommentare notifyKommentare)
    {
        this.id = id;
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

    // Konstruktor ohne die persönlichen Einstellungen des Benutzers und ohne Nutzerstatus.
    // nutzerstatus wird auf die Defaulteinstellung STUDENT gesetzt
    // notifyKommentare wird auf DISKUSSION_TEILGENOMMEN gesetzt
    // notifyVeranstAenderung wird auf true gesetzt
    // notifyKarteikartenAenderung wird auf true gesetzt
    // Dieser Konstruktor wird beim Registrieren verwendet.
    public Benutzer(String eMail, String vorname, String nachname, int matrikelnummer, String studiengang,
            String kennwort)
    {
        super();
        this.eMail = eMail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.matrikelnummer = matrikelnummer;
        this.studiengang = studiengang;
        this.kennwort = kennwort;
        this.nutzerstatus = Nutzerstatus.STUDENT;
        this.notifyKommentare = NotifyKommentare.DISKUSSION_TEILGENOMMEN;
        this.notifyVeranstAenderung = true;
        this.notifyKarteikartenAenderung = true;
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
        return profilBild;
    }

    public void setProfilBildPfad(String profilBildPfad)
    {
        this.profilBild = profilBildPfad;
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
    private String profilBild;

    @Override
    public JSONObject toJSON(boolean full)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.Klasse, ParamDefines.KlasseBenutzer);
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        jo.put(ParamDefines.Id, this.getId());
        jo.put(ParamDefines.Vorname, this.getVorname());
        jo.put(ParamDefines.Nachname, this.getNachname());
        jo.put(ParamDefines.Nutzerstatus, this.getNutzerstatus().name());
        jo.put(ParamDefines.Studiengang, this.getStudiengang());
        jo.put(ParamDefines.ProfilBildPfad, this.getProfilBildPfad());
        
        if(full)
        {
            jo.put(ParamDefines.Email, this.geteMail());
            jo.put(ParamDefines.MatrikelNr, new Integer(this.getMatrikelnummer()).toString());
            jo.put(ParamDefines.NotifyVeranstAenderung, this.isNotifyVeranstAenderung());
            jo.put(ParamDefines.NotifyKarteikartenAenderung, this.isNotifyKarteikartenAenderung());
            jo.put(ParamDefines.NotifyKommentare, this.getNotifyKommentare().name());
            jo.put(ParamDefines.Theme, this.theme);
        }
        return jo;
    }

    public void setTheme(String theme)
    {
        this.theme = theme;
    }

    public String getTheme()
    {
        return theme;
    }

}
