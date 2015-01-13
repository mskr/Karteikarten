package com.lise.util;

import java.sql.Connection;

public class DatenbankManager implements IDatenbankManager
{	
	protected Connection con = null;
	
	private void DatenbankManager() {
		// TODO connection initialisieren
	}

	public Connection getConnection() {
		return null;
	}

	@Override
	public boolean pruefeLogin(String eMail, String passwort) {
		return false;
	}

	@Override
	public Benutzer holeBenutzer(String eMail) {
		return null;
	}

	@Override
	public boolean speichereBenutzer(Benutzer user, boolean overwriteExisting) {
		return false;
	}	
}