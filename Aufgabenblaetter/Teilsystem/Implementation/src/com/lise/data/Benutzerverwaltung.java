package com.lise.data;

import javax.servlet.http.HttpSession;

import com.lise.util.DbUserNotExistsException;
import com.lise.util.DbUserStoringException;
import com.lise.util.IDatenbankManager;
import com.lise.util.LoginFailedException;

public class  Benutzerverwaltung implements IBenutzerverwaltung{
	private IDatenbankManager dbManager;

	public Benutzerverwaltung(IDatenbankManager dbManager){		
		// Check parameter
		if(dbManager == null)
			throw new NullPointerException("DB-Manager should not be null!");
			
		this.dbManager = dbManager;
	}	

	/**
	 * Liefert den Benutzer zurück, der zu dieser session gehört, ist kein Benutzer angemeldet, dann gibt die Funktion null zurück.
	 * @see com.lise.data.IBenutzerverwaltung#getBenutzer(javax.servlet.http.HttpSession)
	 */
	@Override
	public Benutzer getBenutzer(HttpSession session) 
	{
		// eMail-Addr. holen
		String eMail = (String) session.getAttribute("UsereMail"); //aktiveBenutzer.get(session.getId());
		
		if(eMail == null || eMail == "")
			return null;
		else
		{
//			try
//			{
				//return dbManager.holeBenutzer(eMail);
				Benutzer b = new Benutzer();
				b.email = eMail;
				b.id = 1;
				b.matrikelnummer = 1234568;
				b.studiengang = "Informatik";
				b.vorname = "Hans";
				b.nachname = "Mayer";
				b.passwort = "1234";
				return b;
				
				
//			}
//			catch(DbUserNotExistsException e)
//			{
//				System.err.println("Error while reading User. EMail-Adress invalid: " + e.eMail);
//				return null;
//			}
		}
	}

	/**
	 * Meldet den Benuzter mit Passwort und eMail-Addresse im System an. Die Anmeldung wird in der HttpSession gespeichert.
	 * @see com.lise.data.IBenutzerverwaltung#anmelden(java.lang.String, java.lang.String, javax.servlet.http.HttpSession)
	 */
	@Override
	public void anmelden(String eMail, String passwort, HttpSession session) throws LoginFailedException
	{
		if(eMail == null || passwort == null)
			throw new NullPointerException("eMail or passwort is null!");
		
		// Exception bei Fehler
		// TODO erst möglich wenn DB implementiert
		// dbManager.pruefeLogin(eMail, passwort);
		if(!passwort.equals("1234") || !eMail.equals("max.mustermann@test.com") )
		{
			LoginFailedException e = new LoginFailedException("Login fehlgeschlagen", !passwort.equals("1234"), !eMail.equals("max.mustermann@test.com"));
			throw e;
		}
		// Wenn alles funktioniert hat, dann weiter		
		// Setze Benutzer-eMail in Session
		session.setAttribute("UsereMail", eMail);
		// Sete Zeit, in der die Session gültig bleibt
		// TODO test mit ein paar Sekunden!
		session.setMaxInactiveInterval(30);
		
		System.out.println("Benutzer '" + eMail + "' ist angemeldet.");
		System.out.println("SessionID = " + session.getId());
		System.out.println("Gültigkeitsdauer = " + session.getMaxInactiveInterval() + " Sekunden");
	}

	/**
	 * Speichert den Benuzter mit Hilfe des DB-Managers in der Datenbank. Es wird eine Exception geworfen, falls dies fehl schlägt.
	 * @see com.lise.data.IBenutzerverwaltung#registrieren(com.lise.data.Benutzer)
	 */
	@Override
	public boolean registrieren(Benutzer user) throws DbUserStoringException  {
		if(user == null)
			throw new NullPointerException("User is null");
		
		return dbManager.speichereBenutzer(user, false);
	}

	/**
	 * Ändert das Passwort des Benutzers in der Datenbank. Gibt false zurück, wenn der Benutzer nicht angemeldet ist 
	 * oder wenn das alte Passwort falsch war.
	 * @see com.lise.data.IBenutzerverwaltung#passwortAendern(com.lise.data.Benutzer, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean passwortAendern(HttpSession session, String altesPasswort,
			String neuesPasswort) {
		
		if(!istEingeloggt(session))
			return false;
		
		// Hole mail
		String eMail = (String) session.getAttribute("UsereMail");
		
		// Login prüfen
		try {
			dbManager.pruefeLogin(eMail, altesPasswort);
			
		} catch (LoginFailedException e) {
			return false;
		}
		// Hole Benutzer
		Benutzer user = null;
		try {
			user = dbManager.holeBenutzer(eMail);
		} catch (DbUserNotExistsException e1) {
			return false;
		}
		// Ändere Passwort
		user.passwort = neuesPasswort;
		
		// speichere neuen Benutzer
		try {
			dbManager.speichereBenutzer(user, true);
		} catch (DbUserStoringException e) {
			return false;
		}
		System.out.println("Passwort wurde erfolgreich geändert.");
		return true;
	}
	
	/**
	 * Gibt true zurück, wenn der Benutzer abgemeldet wurde. Bei False war kein Benutzer angemeldet.
	 * @see com.lise.data.IBenutzerverwaltung#abmelden(javax.servlet.http.HttpSession)
	 */
	@Override
	public boolean abmelden(HttpSession session) {
		if(istEingeloggt(session))
		{
			// Beendet die Session. Die Session wird beim nächsten mal neu angelgegt.
			session.invalidate();
			System.out.println("Benutzer wurde ausgeloggt: id war " + session.getId());
			return true;
		}
		
		return false;
	}

	/**
	 * Liefert true zurück, wenn der Benutzer, der zur Session gehört, eingeloggt ist.
	 * @see com.lise.data.IBenutzerverwaltung#istEingeloggt(javax.servlet.http.HttpSession)
	 */
	@Override
	public boolean istEingeloggt(HttpSession session) {
		return session.getAttribute("UsereMail") != null;
	}
}