package com.lise.data;

public interface IBenutzerverwaltung{
	
	public Benutzer getBenutzer(String sessionId);
	
	public void anmelden(String eMail, String passwort);
	
	public boolean registrieren(Benutzer user);
	
	public boolean passwortAendern(Benutzer user, String altesPasswort, String neuesPasswort);
	
	
}