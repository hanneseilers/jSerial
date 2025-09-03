package de.hanneseilers.jserial.core;

import de.hanneseilers.jserial.core.interfaces.SerialDataRecievedListener;

/**
 * Class for notifying {@link SerialDataRecievedListener}.
 * @author Hannes Eilers
 *
 */
public class SerialDataRecievedRunnable implements Runnable{

	private SerialDataRecievedListener listener;
	private byte data;
	
	/**
	 * Constructor
	 * @param aListener		{@link SerialDataRecievedListener} to notify.
	 * @param aData			Recieved {@link Byte} data.
	 */
	public SerialDataRecievedRunnable(SerialDataRecievedListener aListener, byte aData) {
		listener = aListener;
		data = aData;
	}
	
	@Override
	public void run() {
		listener.serialDataRecieved(data);
	}
	
}
