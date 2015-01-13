package com.lise.data;

public class BenutzerEinstellungen{
	
	public enum eNotifyDiskussionen {
		KEINE_BENACHRICHTIGUNG , VERANSTALTUNG_DIE_ICH_BESUCHER, TEILGENOMMEN_HABE,  
	}
	
	private boolean gruppenEinladungenErlauben = false;
	private eNotifyDiskussionen NotifyDiskussionen = eNotifyDiskussionen.KEINE_BENACHRICHTIGUNG;
	
	public BenutzerEinstellungen(){
		//
	}
	
	
}