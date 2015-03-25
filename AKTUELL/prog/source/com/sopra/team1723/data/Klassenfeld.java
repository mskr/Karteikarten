package com.sopra.team1723.data;

// Ein Attribut ist eindeutig durch den Klassennamen und den Attributnamen bestimmt
// Objekte dieser Klasse können an die Suchfunktion übergeben werden, die dann weiß
// dass sie das Suchmuster mit diesem Attribut in der db vergleichen soll.
public class Klassenfeld{
    public String klasse;
    public String feld;
    
    public Klassenfeld(String klasse, String feld)
    {
        this.klasse = klasse;
        this.feld = feld;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((feld == null) ? 0 : feld.hashCode());
        result = prime * result + ((klasse == null) ? 0 : klasse.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Klassenfeld other = (Klassenfeld) obj;
        if (feld == null)
        {
            if (other.feld != null)
                return false;
        }
        else if (!feld.equals(other.feld))
            return false;
        if (klasse == null)
        {
            if (other.klasse != null)
                return false;
        }
        else if (!klasse.equals(other.klasse))
            return false;
        return true;
    }
    
    
}