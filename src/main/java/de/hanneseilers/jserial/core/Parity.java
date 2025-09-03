package de.hanneseilers.jserial.core;

import gnu.io.SerialPort;
import jd2xx.JD2XX;

public enum Parity {

	NONE(JD2XX.PARITY_NONE, SerialPort.PARITY_NONE),
	EVEN(JD2XX.PARITY_EVEN, SerialPort.PARITY_EVEN),
	ODD(JD2XX.PARITY_ODD, SerialPort.PARITY_ODD),
	MARK(JD2XX.PARITY_MARK, SerialPort.PARITY_MARK),
	SPACE(JD2XX.PARITY_SPACE, SerialPort.PARITY_SPACE);
	
	
	public int parity;
	public int parity_jd2xx;
	public int parity_rxtx;
	private Parity(int parity_jd2xx, int parity_rxtx){
		parity = this.ordinal();
		this.parity_jd2xx = parity_jd2xx;
		this.parity_rxtx = parity_rxtx;
	}
	
}
