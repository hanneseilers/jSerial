package de.hanneseilers.jserial.core.interfaces;

/**
 * Interface for data recieved listener
 * @author Hannes Eilers
 *
 */
public interface SerialDataRecievedListener {

	/**
	 * {@link SerialDataRecievedListener} callback if {@link Byte} data recieved from serial port.
	 * @param data	Recieved {@link Byte} data.
	 */
	public void serialDataRecieved(byte data);
	
}
