package com.lise.data;

public class Benutzer {
	
	public enum eNutzerstatus {
		Administrator, Dozent, Student,  
	}
	
	protected String vorname = null;
	protected String nachname = null;
	protected int matrikelnummer = 0;
	protected String email = null;
	protected String studiengang = null;
	protected String passwort = null;
	protected eNutzerstatus nutzerstatus = null;
	protected int id = 0;
	protected BenutzerEinstellungen benutzereinstellungen = null ;
	
	
	public Benutzer(){
		setNutzerstatus(eNutzerstatus.Student);
	}
	
	//Methoden Getter/Setter
	
	
	public String getVorname() {
		return vorname;
	}


	public void setVorname(String vorname) {
		this.vorname = vorname;
	}


	public String getNachname() {
		return nachname;
	}


	public void setNachname(String nachname) {
		this.nachname = nachname;
	}


	public int getMatrikelnummer() {
		return matrikelnummer;
	}


	public void setMatrikelnummer(int matrikelnummer) {
		this.matrikelnummer = matrikelnummer;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String eMail) {
		this.email = eMail;
	}


	public String getStudiengang() {
		return studiengang;
	}


	public void setStudiengang(String studiengang) {
		this.studiengang = studiengang;
	}


	public String getPasswort() {
		return passwort;
	}


	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}


	public eNutzerstatus getNutzerstatus() {
		return nutzerstatus;
	}


	public void setNutzerstatus(eNutzerstatus nutzerstatus) {
		this.nutzerstatus = nutzerstatus;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public BenutzerEinstellungen getBenutzereinstellungen() {
		return benutzereinstellungen;
	}


	public void setBenutzereinstellungen(BenutzerEinstellungen benutzereinstellungen) {
		this.benutzereinstellungen = benutzereinstellungen;
	}

	
	
}