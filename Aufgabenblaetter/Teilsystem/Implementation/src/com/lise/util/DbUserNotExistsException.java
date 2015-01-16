package com.lise.util;

public class DbUserNotExistsException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String eMail = null;
	
	public DbUserNotExistsException(String msg, String eMail) {
		super(msg);
		this.eMail = eMail;
	}
	
	
}