package com.lise.data;

import javax.servlet.http.HttpSession;

import com.lise.util.DbUserStoringException;
import com.lise.util.LoginFailedException;

public interface IBenutzerverwaltung{
	
	public Benutzer getBenutzer(HttpSession session);

	public void anmelden(String eMail, String passwort, HttpSession session) throws LoginFailedException;

	public boolean istEingeloggt(HttpSession session);
	
	public boolean abmelden(HttpSession session);
	
	public boolean registrieren(Benutzer user) throws DbUserStoringException;

	public boolean passwortAendern(HttpSession session, String altesPasswort, String neuesPasswort);
	
	
}