package de.hanneseilers.jftdiserial.core.interfaces;

import java.util.List;

import de.hanneseilers.jftdiserial.core.Baudrates;
import de.hanneseilers.jftdiserial.core.DataBits;
import de.hanneseilers.jftdiserial.core.Parity;
import de.hanneseilers.jftdiserial.core.SerialDevice;
import de.hanneseilers.jftdiserial.core.StopBits;

public interface jFTDIserialConnector {

	/**
	 * @return {@link String} of connectorName of connector
	 */
	public String getConnectorName();
	
	/**
	 * @return {@link List} of {@link SerialDevice} that are available. 
	 */
	public List<SerialDevice> getAvailableDevices();
	
	/**
	 * Connects to a {@link SerialDevice}.
	 * @param sDevice {@link SerialDevice}
	 * @return {@code true } if successfull, {@code flase} otherwise.
	 */
	public boolean connect(SerialDevice sDevice);
	
	/**
	 * Disconnects from {@link SerialDevice}.
	 * @return {@code true} if successfull, {@code false} otherwise.
	 */
	public boolean disconnect();
	
	/**
	 * Sets connection settings.
	 * @param baudrate	{@link Baudrates}
	 * @param dataBits	{@link DataBits}
	 * @param stopBits	{@link StopBits}
	 * @param parity	{@link Parity}
	 * @param timeout	{@link Integer} timeout for sending and recieving data.
	 * @return 			{@code true} if successfull, {@code false} otherwise
	 */
	public boolean setConnectionSettings(Baudrates baudrate, DataBits dataBits,StopBits stopBits, Parity parity, int timeout);
	
	/**
	 * @return {@code true} if connector could load library correctly, {@code false} otherwise.
	 */
	public boolean isLibLoaded();
	
	/**
	 * Registers a {@link SerialDataRecievedListener}.
	 * @param listener	{@link SerialDataRecievedListener} to register
	 */
	public void addSerialDataRecievedListener(SerialDataRecievedListener listener);
	
	/**
	 * Unregisters a {@link SerialDataRecievedListener}.
	 * @param listener	{@link SerialDataRecievedListener} to unregister
	 */
	public void removeSerialDataRecievedListener(SerialDataRecievedListener listener);
	
	/**
	 * Unregisters all {@link SerialDataRecievedListener}.
	 */
	public void removeAllSerialDataRecievedListener();
	
	/**
	 * Writes a single byte to serial device
	 * @param b	{@link Byte}
	 * @return	{@code true} if successfull, {@code false} otherwise
	 */
	public boolean write(byte b);
	
	/**
	 * Writes an array of {@link Byte} to serial device
	 * @param buffer	{@link Byte} array
	 * @return			{@code true} if successfull, {@code false} otherwise
	 */
	public boolean write(byte[] buffer);
	
}
