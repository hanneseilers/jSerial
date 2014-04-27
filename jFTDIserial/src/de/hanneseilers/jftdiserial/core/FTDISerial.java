package de.hanneseilers.jftdiserial.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hanneseilers.jftdiserial.core.connectors.JD2XXConnector;
import de.hanneseilers.jftdiserial.core.connectors.RXTXConnector;
import de.hanneseilers.jftdiserial.core.connectors.YAD2xxConnector;
import de.hanneseilers.jftdiserial.core.interfaces.SerialDataRecievedListener;
import de.hanneseilers.jftdiserial.core.interfaces.jFTDIserialConnector;

/**
 * Main class
 * @author Hannes Eilers
 *
 */
public class FTDISerial implements jFTDIserialConnector, SerialDataRecievedListener {

	private static final Logger log = LogManager.getLogger();
	private List<jFTDIserialConnector> connectors = new ArrayList<jFTDIserialConnector>();
	private jFTDIserialConnector connector = null;	
	private boolean connected = false;
	
	private List<SerialDataRecievedListener> serialDataRecievedListeners = new ArrayList<SerialDataRecievedListener>();
	
	/**
	 * Constructor
	 */
	public FTDISerial() {		
		registerConnectors();
		selectFirstAvailableLib();
	}
	
	/**
	 * Constructor to set connection settings
	 * @param baudrate	{@link Baudrates}
	 * @param dataBits	{@link DataBits}
	 * @param stopBits	{@link StopBits}
	 * @param parity	{@link Parity}
	 * @param timeout	{@link Integer} timeout for sending and recieving data.
	 */
	public FTDISerial(Baudrates baudrate, DataBits dataBits, StopBits stopBits, Parity parity, int timeout){
		this();
		setConnectionSettings(baudrate, dataBits, stopBits, parity, timeout);
	}
	
	/**
	 * Sets {@link List} of available {@link jFTDIserialConnector}.
	 * @return	{@link List} of available {@link jFTDIserialConnector}.
	 */
	private List<jFTDIserialConnector> registerConnectors(){
		connectors.add( new JD2XXConnector() );
		connectors.add( new YAD2xxConnector() );
		connectors.add( new RXTXConnector() );
		
		return connectors;
	}
	
	/**
	 * Selects first available library
	 */
	public void selectFirstAvailableLib(){
		for( jFTDIserialConnector con : connectors ){
			if( con.isLibLoaded() ){
				selectLibByName( con.getConnectorName() );
			}
		}
	}
	
	/**
	 * @return {@link List} of available library {@link String} names.
	 */
	public List<String> getAvailableLibNames(){
		List<String> list = new ArrayList<String>();
		
		for( jFTDIserialConnector con : connectors ){
			if( con.isLibLoaded() ){
				list.add(con.getConnectorName());
			}
		}
		
		return list;
	}
	
	/**
	 * Selects an available library by using it's {@link String} connectorName.
	 * @param connectorName	{@link String} connectorName.
	 */
	public void selectLibByName(String name){
		for( jFTDIserialConnector con : connectors ){
			if( con.isLibLoaded() && con.getConnectorName().equals(name) ){
				// disconnect old connector and set new connector
				disconnect();
				connector = con;
				connector.addSerialDataRecievedListener(this);
				log.info("Selected library {}", connector.getConnectorName());
			}
		}
	}
	
	/**
	 * @return {@link String} connectorName of the selected library or {@code null} if no library selected.
	 */
	public String getSelectedLibName(){
		if( connector != null ){
			return connector.getConnectorName();
		}
		
		return null;
	}
	
	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}	

	@Override
	public List<SerialDevice> getAvailableDevices() {
		if( connector != null ){
			return connector.getAvailableDevices();
		}
		return new ArrayList<SerialDevice>();
	}

	@Override
	public boolean connect(SerialDevice device) {
		if( connector != null ){
			connected = connector.connect(device);
		}
		return connected;
	}

	public boolean connect() {
		if( connector != null ){
			List<SerialDevice> devices = getAvailableDevices();
			if( devices.size() > 0 ){
				return connect( devices.get(0) );
			}
			return false;
		}
		return connected;
	}

	@Override
	public boolean disconnect() {
		if( connector != null ){
			connector.removeAllSerialDataRecievedListener();
			if( connector.disconnect() ){
				connected = false;
				return true;
			};
		}
		return false;
	}

	@Override
	public boolean setConnectionSettings(Baudrates baudrate, DataBits dataBits,
			StopBits stopBits, Parity parity, int timeout) {
		if( connector != null ){
			log.debug("Setting connection settings: {} {} {} {} {}",
					baudrate, dataBits, stopBits, parity, timeout);
			return connector.setConnectionSettings(baudrate, dataBits, stopBits, parity, timeout);
		}
		return false;
	}

	@Override
	public boolean isLibLoaded() {
		if( connector != null ){
			return connector.isLibLoaded();
		}
		return false;
	}
	
	@Override
	public boolean write(byte b) {
		if( connector != null ){
			return connector.write(b);
		}
		return false;
	}

	public boolean write(byte[] buffer) {
		if( connector != null ){
			return connector.write(buffer);
		}
		return false;
	}

	@Override
	public String getConnectorName() {
		return "jFTDIserial";
	}

	@Override
	public void addSerialDataRecievedListener(SerialDataRecievedListener listener){
		synchronized (serialDataRecievedListeners) {
			serialDataRecievedListeners.add(listener);
			log.debug("Added listener {}", listener);
		}
	}
	
	@Override
	public void removeSerialDataRecievedListener(SerialDataRecievedListener listener){
		synchronized (serialDataRecievedListeners) {
			serialDataRecievedListeners.remove(listener);
			log.debug("Removed listener {}", listener);
		}
	}
	
	@Override
	public void removeAllSerialDataRecievedListener(){
		synchronized (serialDataRecievedListeners) {
			serialDataRecievedListeners.clear();
			log.debug("Removed all listener");
		}
	}
	
	/**
	 * Notifies all registered {@link SerialDataRecievedListener} about new data.
	 * @param data	Recieved {@link Byte} data 
	 */
	protected void notifySerialDataRecievedListener(byte data){
		synchronized (serialDataRecievedListeners) {
			for( SerialDataRecievedListener listener : serialDataRecievedListeners ){
				new Thread( new SerialDataRecievedRunnable(listener, data) ).start();
				
			}
		}
	}
	
	@Override
	public void serialDataRecieved(byte data) {
		log.debug("data recieved {}", data);
		notifySerialDataRecievedListener( data );
	}

}
