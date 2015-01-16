package com.lise.util;

public class LoginFailedException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Boolean eMailWrong = false;
	public Boolean passwortWrong = false;
	
	public LoginFailedException(String msg, boolean passwortWrong, boolean eMailWrong) {
		super(msg);
		this.eMailWrong = eMailWrong;
		this.passwortWrong = passwortWrong;
	}
}
