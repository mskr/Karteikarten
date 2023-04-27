import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.jfree.graphics2d.svg.*;

public class Identicon {
	
	// Groesse des SVGs in Pixeln
	public static final int IMGSIZE= 200;
	// Anzahl der Raster-Zellen (entlang einer Kante)
	public static final int GRIDSIZE = 8;
	// Seitenlaenge einer Raster-Zelle in Pixeln
	public static int cellSize = IMGSIZE/GRIDSIZE;

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Ich brauche einen Seed!");
			return;
		}
		// Seed vom Benutzer
		int seed;
		try { 
			seed = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			System.out.println("Ich brauche einen Integer als Seed.");
			return;
		}
		// SVG Objekt
		SVGGraphics2D svg = new SVGGraphics2D(IMGSIZE, IMGSIZE);
		// Zufaellige Farbe
		svg.setPaint(Color.decode("0x"+Integer.toHexString(seed*100)));
		// Erzeugen des Identicons
//		randomTriangles(svg, seed);
		randomTrianglesWithReflection(svg, seed);
		// Erzeugen der SVG Datei
		generateSVG(svg);
	}
	
	/**
	 * Setzt in jede Raster-Zelle ein zufaellig ausgewaehltes Dreieck
	 * aus der triangle Methode.
	 * @param svg
	 * @param seed
	 */
	public static void randomTriangles(SVGGraphics2D svg, int seed) {
		for(int i = 0; i < GRIDSIZE; i++) {
			for(int j = 0; j < GRIDSIZE; j++) {
				svg.fill(triangle(i,j,randInt(1,7000,seed*i+j)/1000+1));
			}
		}
	}
	
	/**
	 * 
	 */
	public static void randomTrianglesWithReflection(SVGGraphics2D svg, int seed) {
		for(int i = 0; i < GRIDSIZE/2; i++) {
			for(int j = 0; j < GRIDSIZE; j++) {
				int rand = randInt(1,7000,seed*i+j)/1000+1;
				// Die ersten GRIDSIZE/2 Spalten fuellen
				svg.fill(triangle(i,j,rand));
				// Die restlichen Spalten mit der Spiegelung fuellen
				if(rand == 1) rand = 3;
				else if(rand == 3) rand = 1;
				else if(rand == 4) rand = 8;
				else if(rand == 5) rand = 7;
				else if(rand == 7) rand = 5;
				else if(rand == 8) rand = 4;
				svg.fill(triangle(GRIDSIZE-1-i,j,rand));
			}
		}
	}
	
	/**
	 * Erzeugt ein Dreieck im der angegebenen Raster-Zelle.
	 * @param offsetX ist ein Wert zwischen 0 und GRIDSIZE-1
	 * @param offsetY ist ein Wert zwischen 0 und GRIDSIZE-1
	 * @param type definiert, wohin die Spitze des Dreiecks zeigt
	 * @return
	 */
	public static Polygon triangle(int offsetX, int offsetY, int type) {
		int baselineX = offsetX*cellSize;
		int baselineY = offsetY*cellSize;
		int peakX = offsetX*cellSize+cellSize;
		int peakY = offsetY*cellSize+cellSize;
		int middleX = offsetX*cellSize+cellSize/2;
		int middleY = offsetY*cellSize+cellSize/2;
		if(offsetX > GRIDSIZE-1 || offsetY > GRIDSIZE-1 || offsetX < 0 || offsetY < 0) {
			System.out.println("Ich werde kein Dreieck ausserhalb der Viewbox liefern.");
			return null;
		}
		switch(type) {
			case 1: return new Polygon(new int[]{baselineX, baselineX, peakX}, new int[]{peakY, baselineY, baselineY}, 3); // Spitze links oben
			case 2: return new Polygon(new int[]{baselineX, peakX, middleX}, new int[]{peakY, peakY, baselineY}, 3); // Spitze mitte oben
			case 3: return new Polygon(new int[]{baselineX, peakX, peakX}, new int[]{baselineY, baselineY, peakY}, 3); // Spitze rechts oben
			case 4: return new Polygon(new int[]{baselineX, peakX, baselineX}, new int[]{baselineY, middleY, peakY}, 3); // Spitze mitte rechts
			case 5: return new Polygon(new int[]{baselineX, peakX, peakX}, new int[]{peakY, peakY, baselineY}, 3); // Spitze unten rechts
			case 6: return new Polygon(new int[]{baselineX, middleX, peakX}, new int[]{baselineY, peakY, baselineY}, 3); // Spitze mitte unten
			case 7: return new Polygon(new int[]{baselineX, peakX, baselineX}, new int[]{peakY, peakY, baselineY}, 3); // Spitze links unten
			case 8: return new Polygon(new int[]{baselineX, peakX, peakX}, new int[]{middleY, baselineY, peakY}, 3); // Spitze mitte links
			default:
				System.out.println("Ich kenne kein Dreieck mit der Nummer "+type+".");
				return null;
		}
	}

	/**
	 * Schreibt das SVG Objekt in eine SVG Datei.
	 * @param svg
	 */
	public static void generateSVG(SVGGraphics2D svg) {
		try {
			SVGUtils.writeToSVG(new File("C:/Users/Marius/Desktop/out.svg"), svg.getSVGElement());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max, long seed) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random(seed);

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}

}