package com.lise.data;

import java.util.HashMap;
import java.util.Map;

public class  Benutzerverwaltung implements IBenutzerverwaltung{
	
	private Map<String,String> aktiveBenutzer; 
	

	public Benutzerverwaltung() {
		this.aktiveBenutzer = new HashMap<String, String>();
	}	

	@Override
	public Benutzer getBenutzer(String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void anmelden(String eMail, String passwort) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean registrieren(Benutzer user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean passwortAendern(Benutzer user, String altesPasswort,
			String neuesPasswort) {
		// TODO Auto-generated method stub
		return false;
	}



}