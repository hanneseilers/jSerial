package de.hanneseilers.jftdiserial.core;

import jd2xx.JD2XX;

public enum Parity {

	NONE(JD2XX.PARITY_NONE),
	EVEN(JD2XX.PARITY_EVEN),
	ODD(JD2XX.PARITY_ODD),
	MARK(JD2XX.PARITY_MARK),
	SPACE(JD2XX.PARITY_SPACE);
	
	
	public int parity;
	public int parity_jd2xx;
	private Parity(int parity_jd2xx){
		parity = this.ordinal();
		this.parity_jd2xx = parity_jd2xx;
	}
	
}
