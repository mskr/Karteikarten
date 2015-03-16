package com.sopra.team1723.ctrl;

import java.sql.Connection;

import javax.servlet.http.*;

import com.sopra.team1723.data.*;

/**
 * Implementiert die Methoden des @ref IDatenbankmanager. Bietet eine Schnittstelle zur Datenbank.
 */
public class Datenbankmanager implements IDatenbankmanager {

    /**
     * Implementiert die Methoden des @ref IDatenbankmanager. Bietet eine Schnittstelle zur Datenbank.
     */
    public Datenbankmanager() {
    }

    /**
     * 
     */
    private Connection con;

	@Override
	public Benutzer leseBenutzer(String eMail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean schreibeBenutzer(Benutzer benutzer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bearbeiteBenutzer(Benutzer benutzer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loescheBenutzer(String eMail) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pruefeLogin(String eMail, String passwort) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean passwortAendern(String eMail, String neuesPasswort) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Veranstaltung leseVeranstaltung(String veranstTitel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean schreibeVeranstaltung(Veranstaltung veranst) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bearbeiteVeranstaltung(Veranstaltung veranst) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loescheVeranstaltung(String veranstTitel) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Karteikarte leseKarteikarte(int karteikID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Karteikarte[] leseKindKarteikarten(int vaterKarteikID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean schreibeKarteikarte(Karteikarte karteik, int vaterKarteikID, int Position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void schreibeErsteKarteikarte(Karteikarte karteik, int sohnKarteikID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean bearbeiteKarteikarte(Karteikarte karteik) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loescheKarteikarte(int karteikID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bewerteKarteikarte(int karteikID, int bewert, String benutzer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hatKarteikarteBewertet(int karteikID, String benutzer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Kommentar[] leseKommentare(int karteikID, int vaterKID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean schreibeKommentar(Kommentar kommentar) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bearbeiteKommentar(Kommentar kommentar) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loescheKommentar(int kommentarID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bewerteKommentar(int kommentarID, int bewert, String benutzer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hatKommentarBewertet(int kommentarID, String benutzer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean istModerator(Veranstaltung veranst, String benutzerMail) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zuVeranstaltungEinschreiben(String veranstTitel, String eMail) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean vonVeranstaltungAbmelden(String veranstTitel, String eMail) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Notiz[] leseNotizen(String erstellerEMail, int karteikID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean schreibeNotiz(Notiz notiz) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bearbeiteNotiz(Notiz notiz) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loescheNotiz(int notizID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rolleZuweisen(String eMail, Nutzerstatus status) {
		// TODO Auto-generated method stub
		return false;
	}
}