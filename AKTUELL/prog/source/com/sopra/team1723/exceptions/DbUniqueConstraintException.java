package com.sopra.team1723.exceptions;

/**
  Wenn bei einem in der Datenbank als eindeutig gekennzeichnetes Feld versucht wird einen 
  bereits vorhandenen Wert einzutragen, wird diese Exception geworfen
 */
public class DbUniqueConstraintException extends Exception
{

}
