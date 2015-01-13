package com.lise.util;

import com.lise.data.Benutzer;

import java.sql.Connection;


public interface IDatenbankManager
{
	public boolean pruefeLogin(String eMail, String passwort);
	public Benutzer holeBenutzer(String eMail);
	public boolean speichereBenutzer(Benutzer user, boolean overwriteExisting);
	
}

