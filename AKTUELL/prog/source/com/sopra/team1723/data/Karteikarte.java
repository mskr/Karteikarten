package com.sopra.team1723.data;

import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sopra.team1723.ctrl.IjsonObject;
import com.sopra.team1723.ctrl.ParamDefines;

/**
 * 
 */
public class Karteikarte implements IjsonObject {

	
    // Kompletter Konstruktor. Z.B. beim Lesen aus der db verwendet
    public Karteikarte(int id, String titel, Calendar aenderungsdatum, String inhalt,
            KarteikartenTyp typ, int veranstaltung, int bewertung, boolean istSatz, boolean istLemma, boolean istBeweis,
            boolean istDefinition, boolean istWichtig, boolean istGrundlage, boolean istZusatzinfo, boolean istExkurs, boolean istBeispiel,
            boolean istUebung, ArrayList<Tripel<BeziehungsTyp,Integer,String>> verweise)
    {
        this();
        this.id = id;
        this.titel = titel;
        this.aenderungsdatum = aenderungsdatum;
        this.inhalt = inhalt;
        this.typ = typ;
        this.veranstaltung = veranstaltung;
        this.bewertung = bewertung;
        
        this.istSatz = istSatz;
        this.istLemma = istLemma;
        this.istBeweis = istBeweis;
        this.istDefinition = istDefinition;
        this.istWichtig = istWichtig;
        this.istGrundlage = istGrundlage;
        this.istZusatzinfo = istZusatzinfo;
        this.istExkurs = istExkurs;
        this.istBeispiel = istBeispiel;
        this.istUebung = istUebung;
        
        this.hatBewertet = false;
        
        this.verweise = verweise;
    }
    
	// Konstruktor der verwendet wird wenn eine Karteikarte erstellt wird.
    public Karteikarte(String titel, String inhalt, KarteikartenTyp typ, int veranstaltung,
            boolean istSatz, boolean istLemma, boolean istBeweis, boolean istDefinition, boolean istWichtig, boolean istGrundlage,
            boolean istZusatzinfo, boolean istExkurs, boolean istBeispiel, boolean istUebung, ArrayList<Tripel<BeziehungsTyp,Integer,String>> verweise)
    {
        this();
        this.titel = titel;
        this.inhalt = inhalt;
        this.typ = typ;
        this.veranstaltung = veranstaltung;
        
        this.istSatz = istSatz;
        this.istLemma = istLemma;
        this.istBeweis = istBeweis;
        this.istDefinition = istDefinition;
        this.istWichtig = istWichtig;
        this.istGrundlage = istGrundlage;
        this.istZusatzinfo = istZusatzinfo;
        this.istExkurs = istExkurs;
        this.istBeispiel = istBeispiel;
        this.istUebung = istUebung;
        
        this.id = -1;
        this.aenderungsdatum = Calendar.getInstance();
        this.bewertung = 0;
        this.hatBewertet = false;
        
        this.verweise = verweise;
    }
 // Konstruktor der verwendet wird wenn eine Karteikarte bearbeitet wird.
    public Karteikarte(int id, String titel, String inhalt, KarteikartenTyp typ, int veranstaltung,
            boolean istSatz, boolean istLemma, boolean istBeweis, boolean istDefinition, boolean istWichtig, boolean istGrundlage,
            boolean istZusatzinfo, boolean istExkurs, boolean istBeispiel, boolean istUebung, ArrayList<Tripel<BeziehungsTyp,Integer,String>> verweise)
    {
        this();
        this.titel = titel;
        this.inhalt = inhalt;
        this.typ = typ;
        this.veranstaltung = veranstaltung;
        
        this.istSatz = istSatz;
        this.istLemma = istLemma;
        this.istBeweis = istBeweis;
        this.istDefinition = istDefinition;
        this.istWichtig = istWichtig;
        this.istGrundlage = istGrundlage;
        this.istZusatzinfo = istZusatzinfo;
        this.istExkurs = istExkurs;
        this.istBeispiel = istBeispiel;
        this.istUebung = istUebung;
        
        this.id = id;
        this.aenderungsdatum = Calendar.getInstance();
        this.bewertung = 0;
        this.hatBewertet = false;
    }

    /**
     * 
     */
    /**
     * 
     */
    public Karteikarte() {
        super();


    }

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String titel;
    private boolean hatBewertet;

    /**
     * 
     */
    private Calendar aenderungsdatum;

    /**
     * 
     */
    private String inhalt;

    /**
     * 
     */
    private KarteikartenTyp typ;
    
    
    
    private int veranstaltung;
    
    private int bewertung;
    
    private boolean istSatz;    
    private boolean istLemma; 
    private boolean istBeweis;
    private boolean istDefinition;
    private boolean istWichtig;
    private boolean istGrundlage;
    private boolean istZusatzinfo;
    private boolean istExkurs;
    private boolean istBeispiel;
    private boolean istUebung;
    
    private ArrayList<Tripel<BeziehungsTyp,Integer,String>> verweise;
       
    
    public enum BeziehungsTyp {
        H_CHILD,
        H_BROTHER,
        V_VORAUSSETZUNG, 
        V_UEBUNG, 
        V_ZUSATZINFO,
        V_SONSTIGES
    };
    
    public enum AttributTyp {
        SATZ,
        LEMMA,
        BEWEIS,
        DEFINITION,
        WICHTIG,
        GRUNDLAGEN,
        ZUSATZINFO,
        EXKURS,
        BEISPIEL,
        UEBUNG
    }
    

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

    public Calendar getAenderungsdatum()
    {
        return aenderungsdatum;
    }

    public void setAenderungsdatum(Calendar aenderungsdatum)
    {
        this.aenderungsdatum = aenderungsdatum;
    }

    public String getInhalt()
    {
        return inhalt;
    }

    public void setInhalt(String inhalt)
    {
        this.inhalt = inhalt;
    }


    public KarteikartenTyp getTyp()
    {
        return typ;
    }

    public void setTyp(KarteikartenTyp typ)
    {
        this.typ = typ;
    }

    public int getVeranstaltung()
    {
        return veranstaltung;
    }

    public void setVeranstaltung(int veranstaltung)
    {
        this.veranstaltung = veranstaltung;
    }

    public int getBewertung()
    {
        return bewertung;
    }

    public void setBewertung(int bewertung)
    {
        this.bewertung = bewertung;
    }
    public void setHatBewertet(boolean hatBewertet)
    {
        this.hatBewertet = hatBewertet;
    }

    

    public boolean isIstSatz()
    {
        return istSatz;
    }



    public void setIstSatz(boolean istSatz)
    {
        this.istSatz = istSatz;
    }



    public boolean isIstBeweis()
    {
        return istBeweis;
    }



    public void setIstBeweis(boolean istBeweis)
    {
        this.istBeweis = istBeweis;
    }



    public boolean isIstDefinition()
    {
        return istDefinition;
    }



    public void setIstDefinition(boolean istDefinition)
    {
        this.istDefinition = istDefinition;
    }



    public boolean isIstWichtig()
    {
        return istWichtig;
    }



    public void setIstWichtig(boolean istWichtig)
    {
        this.istWichtig = istWichtig;
    }



    public boolean isIstGrundlage()
    {
        return istGrundlage;
    }



    public void setIstGrundlage(boolean istGrundlage)
    {
        this.istGrundlage = istGrundlage;
    }



    public boolean isIstExkurs()
    {
        return istExkurs;
    }



    public void setIstExkurs(boolean istExkurs)
    {
        this.istExkurs = istExkurs;
    }



    public boolean isIstBeispiel()
    {
        return istBeispiel;
    }



    public void setIstBeispiel(boolean istBeispiel)
    {
        this.istBeispiel = istBeispiel;
    }



    public boolean isIstUebung()
    {
        return istUebung;
    }



    public void setIstUebung(boolean istUebung)
    {
        this.istUebung = istUebung;
    }



    public boolean isHatBewertet()
    {
        return hatBewertet;
    }

    /**
     * Erlaubte typen: vorraussetzung, zusatz, sonstiges, uebung
     * @param verweise
     */
    public void setVerweise(ArrayList<Tripel<BeziehungsTyp, Integer, String>> verweise){
        this.verweise = verweise;
    }
    
    public ArrayList<Tripel<BeziehungsTyp, Integer, String>> getVerweise()
    {
        return verweise;
    }

    public boolean isIstLemma()
    {
        return istLemma;
    }

    public void setIstLemma(boolean istLemma)
    {
        this.istLemma = istLemma;
    }

    public boolean isIstZusatzinfo()
    {
        return istZusatzinfo;
    }

    public void setIstZusatzinfo(boolean istZusatzinfo)
    {
        this.istZusatzinfo = istZusatzinfo;
    }

    @Override
    public JSONObject toJSON(boolean full)
    {
        JSONObject jo = new JSONObject();
        jo.put(ParamDefines.Klasse, ParamDefines.KlasseKarteikarte);
        jo.put(ParamDefines.jsonErrorCode, ParamDefines.jsonErrorNoError);
        jo.put(ParamDefines.Id, this.getId());
        jo.put(ParamDefines.Titel, this.getTitel());
        jo.put(ParamDefines.Type, this.getTyp().name());
        jo.put(ParamDefines.Veranstaltung, this.getVeranstaltung());
        jo.put(ParamDefines.HatGevoted, hatBewertet);
        
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        jo.put(ParamDefines.Aenderungsdatum, f.format(this.getAenderungsdatum().getTime()));
        jo.put(ParamDefines.Bewertung, this.getBewertung());
        jo.put(ParamDefines.Inhalt, this.getInhalt());
        
        JSONArray arr = new JSONArray();

        arr.add(this.istSatz);
        arr.add(this.istLemma);
        arr.add(this.istBeweis);
        arr.add(this.istDefinition);
        arr.add(this.istWichtig);
        arr.add(this.istGrundlage);
        arr.add(this.istZusatzinfo);
        arr.add(this.istExkurs);
        arr.add(this.istBeispiel);
        arr.add(this.istUebung);
 
        jo.put(ParamDefines.Attribute, arr);
        
        JSONArray arr2 = new JSONArray();
        for(Tripel<BeziehungsTyp,Integer,String> verw : verweise){
            JSONObject o = new JSONObject();
            o.put(ParamDefines.Type, verw.x.toString());
            o.put(ParamDefines.Id, verw.y);
            o.put(ParamDefines.Titel, verw.z);
            arr2.add(o);
        }
        jo.put(ParamDefines.Verweise, arr2);
        
        return jo;
    }

    @Override
	public String toString() {
		return "Karteikarte [id=" + id + ", titel=" + titel + ", aenderungsdatum=" + aenderungsdatum + ", inhalt="
				+ inhalt + ", typ=" + typ + ", veranstaltung=" + veranstaltung + ", bewertung=" + bewertung + "]";
	}

    

    
}