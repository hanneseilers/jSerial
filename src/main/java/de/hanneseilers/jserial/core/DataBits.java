package de.hanneseilers.jserial.core;

import gnu.io.SerialPort;

/**
 * Serial connection data bits
 * @author Hannes Eilers
 *
 */
public enum DataBits {

	DATABITS_5(5, SerialPort.DATABITS_5),
	DATABITS_6(6, SerialPort.DATABITS_6),
	DATABITS_7(7, SerialPort.DATABITS_7),
	DATABITS_8(8, SerialPort.DATABITS_8);
	
	public int bits;
	public int bits_rxtx;
	private DataBits(int databits, int databist_rxtx){
		bits = databits;
		bits_rxtx = databist_rxtx;
	}
	
}
