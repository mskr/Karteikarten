package com.lise.data;

import javax.servlet.http.HttpSession;

import com.lise.util.DbUserNotExistsException;
import com.lise.util.DbUserStoringException;
import com.lise.util.IDatenbankManager;
import com.lise.util.LoginFailedException;

public class  Benutzerverwaltung implements IBenutzerverwaltung{
	
	//private Map<String,String> aktiveBenutzer; 
	private IDatenbankManager dbManager;

	public Benutzerverwaltung(IDatenbankManager dbManager){		
		// Check parameter
		if(dbManager == null)
			throw new NullPointerException("DB-Manager should not be null!");
			
		this.dbManager = dbManager;
		//this.aktiveBenutzer = new HashMap<String, String>();
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
			try
			{
				return dbManager.holeBenutzer(eMail);
			}
			catch(DbUserNotExistsException e)
			{
				System.err.println("Error while reading User. EMail-Adress invalid: " + e.eMail);
				return null;
			}
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
		dbManager.pruefeLogin(eMail, passwort);
		
		// Wenn alles funktioniert hat, dann weiter
		// Speichere SessionID in der Map
		// TODO Map könnte man weglassen so wies aussieht !
		//aktiveBenutzer.put(session.getId(), eMail);
		
		// Setze Benutzer-eMail in Session
		session.setAttribute("UsereMail", eMail);
		// Sete Zeit, in der die Session gültig bleibt
		// TODO test mit 10 Sekunden!
		session.setMaxInactiveInterval(10);
	}

	/**
	 * Speichert den Benuzter mit Hilfe des DB-Managers in der Datenbank. Es wird eine Exception geworfen, falls dies fehl schlägt.
	 * @see com.lise.data.IBenutzerverwaltung#registrieren(com.lise.data.Benutzer)
	 */
	@Override
	public boolean registrieren(Benutzer user) throws DbUserStoringException  {
		if(user == null)
			throw new NullPointerException("User is null");
		
		dbManager.speichereBenutzer(user, false);
		
		return false;
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
			session.invalidate();
			System.out.println("Benutzer wurde ausgeloggt: id= " + session.getId());
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