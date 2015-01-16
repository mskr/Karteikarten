package com.lise.util;

import com.lise.data.Benutzer;

import java.sql.Connection;


public interface IDatenbankManager
{
	public boolean pruefeLogin(String eMail, String passwort) throws LoginFailedException;
	public Benutzer holeBenutzer(String eMail) throws DbUserNotExistsException;
	public boolean speichereBenutzer(Benutzer user, boolean overwriteExisting) throws DbUserStoringException;
	
}

