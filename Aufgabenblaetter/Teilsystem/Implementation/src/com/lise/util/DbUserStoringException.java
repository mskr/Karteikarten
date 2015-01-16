package com.lise.util;

import com.lise.data.Benutzer;

public class DbUserStoringException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean vornameInvalid = false;
	public boolean nachnameInvalid = false;
	public boolean martrikelnummerInvalid = false;
	public boolean eMailInvalid = false;
	public boolean studiengangNotSupported = false;
	public boolean passwortInvalid = false;
	public boolean nutzerstatusInvalid = false;
	public Benutzer user = null;
	
	public DbUserStoringException(String msg, Benutzer user) {
		super(msg);
		this.user = user;
	}
	
	
}