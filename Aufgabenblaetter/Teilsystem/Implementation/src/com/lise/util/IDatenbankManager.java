package com.lise.util;


import java.util.ArrayList;

import com.lise.data.Benutzer;

public interface IDatenbankManager
{
	public boolean pruefeLogin(String eMail, String passwort) throws LoginFailedException;
	public Benutzer holeBenutzer(String eMail) throws DbUserNotExistsException;
	public void speichereBenutzer(Benutzer user) throws DbUserStoringException;
	public void bearbeiteBenutzer(Benutzer user) throws DbUserStoringException;
	public ArrayList<String> holeStudiengaenge();
}

