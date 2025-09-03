package de.hanneseilers.jserial.core.connectors;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.logging.log4j.LogManager;

import de.hanneseilers.jserial.core.Baudrates;
import de.hanneseilers.jserial.core.DataBits;
import de.hanneseilers.jserial.core.Parity;
import de.hanneseilers.jserial.core.SerialDevice;
import de.hanneseilers.jserial.core.StopBits;

public class RXTXConnector extends AbstractConnector implements SerialPortEventListener {
	
	private SerialPort device;
	private InputStream input;
	private OutputStream output;
	private boolean libLoaded = false;
	
	private Baudrates baudrate = Baudrates.BAUD_9600;
	private DataBits dataBits;
	private StopBits stopBits;
	private Parity parity;
	private int timeout = 500;
	
	public RXTXConnector() {
		log = LogManager.getLogger();
		connectorName = "rxtx serial";
		connectorLibDir = "rxtx";
		
		System.getProperty("java.library.path");
		libLoaded = loadRequiredLibs("rxtxSerial", true);
		if( libLoaded ){
			log.info("Loaded " + getConnectorName());
		}
	}

	@Override
	synchronized  public List<SerialDevice> getAvailableDevices() {
		List<SerialDevice> devices = new ArrayList<SerialDevice>();
		
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		while( ports.hasMoreElements() ){
			CommPortIdentifier port = ports.nextElement();
			if( port.getPortType() == CommPortIdentifier.PORT_SERIAL ){
				devices.add( new SerialDevice(port) );
			}
		}
		
		return devices;
	}

	@Override
	public boolean connect(SerialDevice sDevice) {
		try{
			
			CommPort com = sDevice.getRxTxDevice().open(this.getConnectorName(), timeout);
			device = (SerialPort) com;				
			device.setSerialPortParams(baudrate.baud, dataBits.bits_rxtx, stopBits.bits_rxtx, parity.parity_rxtx);
			device.addEventListener(this);
			device.notifyOnDataAvailable(true);
			
			input = device.getInputStream();
			output = device.getOutputStream();
			return true;
			
		}catch (PortInUseException e){
			log.error("Serial port {} is in use.", sDevice.getRxTxDevice().getName());
		}catch (Exception e){
			log.error("Can not open port {}", sDevice.getRxTxDevice().getName());
		}
		return false;
	}

	@Override
	public boolean disconnect() {
		if( device != null ){
			try{
				device.close();
				input.close();
				output.close();
				
				device = null;
				input = null;
				output = null;
				return true;
			}catch (IOException e){
				log.error("Exception while disconnecting serial port");
			}
		}
		return false;
	}

	@Override
	public boolean setConnectionSettings(Baudrates baudrate, DataBits dataBits,
			StopBits stopBits, Parity parity, int timeout) {
		this.baudrate = baudrate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.timeout = timeout;
		return true;
	}

	@Override
	public boolean isLibLoaded() {
		return libLoaded;
	}

	@Override
	public boolean write(byte b) {
		return write( new byte[]{b} );
	}
	
	@Override
	synchronized public boolean write(byte[] buffer) {
		if( device != null && output != null ){
			try{
				output.write(buffer);
			}catch (IOException e){
				log.error("Could not write data to serial port {}", device.getName());
			}
		}
		
		return false;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if( event.getEventType() == SerialPortEvent.DATA_AVAILABLE ){
			
			try {
				byte[] buffer = new byte[1];
				input.read( buffer );
				notifySerialDataRecievedListener( buffer[0] );
			} catch (IOException e) {
				log.error("Error recieving data byte from {}", device.getName());
			}
			
		}
	}

}
