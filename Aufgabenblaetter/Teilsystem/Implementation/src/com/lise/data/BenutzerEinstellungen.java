package com.lise.data;

public class BenutzerEinstellungen{
	
	public enum eNotifyDiskussionen {
		KEINE_BENACHRICHTIGUNG , VERANSTALTUNG_DIE_ICH_BESUCHER, TEILGENOMMEN_HABE,  
	}
	
	private boolean gruppenEinladungenErlauben = false;
	private eNotifyDiskussionen NotifyDiskussionen = eNotifyDiskussionen.KEINE_BENACHRICHTIGUNG;
	
	public BenutzerEinstellungen(boolean gruppenEinladungenErlauben, String NotifyDiskussionen){
		this.gruppenEinladungenErlauben = gruppenEinladungenErlauben;
		this.NotifyDiskussionen = eNotifyDiskussionen.valueOf(NotifyDiskussionen);
	}
	
	public void setGruppenEinladungenErlauben(boolean gruppenEinladungenErlauben){
		this.gruppenEinladungenErlauben = gruppenEinladungenErlauben;
	}
	
	public boolean getGruppenEinladungenErlauben(){
		return gruppenEinladungenErlauben;
	}
	
	public void setNotifyDiskussionen(String NotifyDiskussionen){
		this.NotifyDiskussionen = eNotifyDiskussionen.valueOf(NotifyDiskussionen);
	}
	
	public eNotifyDiskussionen getNotifyDiskussionen(){
		return NotifyDiskussionen;
	}
}