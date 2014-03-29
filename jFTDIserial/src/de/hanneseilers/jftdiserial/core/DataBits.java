package de.hanneseilers.jftdiserial.core;

/**
 * Serial connection data bits
 * @author Hannes Eilers
 *
 */
public enum DataBits {

	DATABITS_5(5),
	DATABITS_6(6),
	DATABITS_7(7),
	DATABITS_8(8);
	
	public int bits;
	private DataBits(int databits){
		bits = databits;
	}
	
}
