package com.sopra.team1723.ctrl;

import org.json.simple.JSONObject;

/**
 * Wird von Benutzern, Veranstaltungen etc. implementiert
 * und enthaelt eine JSON-Converter-Methode.
 *
 */
public interface IjsonObject
{
    /**
     * Liefert das betreffende Objekt als JSONObject zurueck
     * @param full gibt bei Benutzern an, ob auch die pers. Einstellungen eingepackt werden sollen.
     * Ist das Objekt kein Benutzer ist dieser Parameter irrelevant.
     * @return
     */
    public JSONObject toJSON(boolean full);

}
