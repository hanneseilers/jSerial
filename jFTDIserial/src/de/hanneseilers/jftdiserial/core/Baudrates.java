package de.hanneseilers.jftdiserial.core;

public enum Baudrates {

	BAUD_300(300),
	BAUD_600(600),
	BAUD_1200(1200),
	BAUD_2400(2400),
	BAUD_4800(4800),
	BAUD_9600(9600),
	BAUD_14400(14400),
	BAUD_19200(19200),
	BAUD_28800(28800),
	BAUD_38400(38400),
	BAUD_56000(56000),
	BAUD_57600(57600),
	BAUD_115200(115200),
	BAUD_128000(128000),
	BAUD_256000(256000);
	
	public int baud;
	
	/**
	 * Constructor
	 * @param baudrate	{@link Integer} of baudrate
	 */
	private Baudrates(int baudrate){
		baud = baudrate;
	}
	
}
