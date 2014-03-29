package de.hanneseilers.jftdiserial.core;

import jd2xx.JD2XX;

/**
 * Serial connection stop bits
 * @author Hannes Eilers
 *
 */
public enum StopBits {

	STOPBIT_1(1, JD2XX.STOP_BITS_1),
	STOPBIT_1_5(1.5, JD2XX.STOP_BITS_1_5),
	STOPBIT_2(2, JD2XX.STOP_BITS_2);
	
	public double bits;
	public int bits_jd2xx;
	private StopBits(double stopbits, int stopbits_jd2xx){
		bits = stopbits;
		bits_jd2xx = stopbits_jd2xx;
	}
	
}
