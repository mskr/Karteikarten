package com.sopra.team1723.data;

import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.IjsonObject;
import com.sopra.team1723.ctrl.JSONConverter;
import com.sopra.team1723.ctrl.ParamDefines;


/**
 * 
 */
public class Veranstaltung implements IjsonObject {

    
    
    public Veranstaltung(int id, String titel, String beschreibung, String semester,
            String zugangspasswort, boolean bewertungenErlaubt, boolean moderatorKarteikartenBearbeiten,
            Benutzer ersteller, boolean kommentareErlaubt, int anzTeilnehmer)
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
    
    private Benutzer ersteller;

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




    public Benutzer getErsteller()
    {
        return ersteller;
    }




    public void setErsteller(Benutzer ersteller)
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




    @Override
    public JSONObject toJSON(boolean full)
    {
        // Ignore parameter full
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.jsonErrorTxt, ParamDefines.jsonErrorNoError);    
        jo.put(ParamDefines.Id, this.getId());   
        jo.put(ParamDefines.Titel, this.getTitel()); 
        jo.put(ParamDefines.Beschr, this.getBeschreibung()); 
        jo.put(ParamDefines.Semester, this.getSemester()); 
        jo.put(ParamDefines.BewertungenErlauben, this.isBewertungenErlaubt()); 
        jo.put(ParamDefines.ModeratorKkBearbeiten, this.isModeratorKarteikartenBearbeiten()); 
        jo.put(ParamDefines.KommentareErlauben,this.isKommentareErlaubt());
        jo.put(ParamDefines.Ersteller,this.getErsteller().toJSON(true));
        return jo;
    }


    

}