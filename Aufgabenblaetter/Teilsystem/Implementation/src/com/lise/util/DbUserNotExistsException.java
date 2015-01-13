package com.lise.util;

public class DbUserNotExistsException extends Exception
{
	public String eMail = null;
	
	public DbUserNotExistsException(String msg, String eMail) {
		super(msg);
		this.eMail = eMail;
	}
	
	
}