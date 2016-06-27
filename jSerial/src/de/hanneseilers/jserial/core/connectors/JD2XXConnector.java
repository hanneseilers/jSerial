package de.hanneseilers.jserial.core.connectors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import org.apache.logging.log4j.LogManager;

import de.hanneseilers.jserial.core.Baudrates;
import de.hanneseilers.jserial.core.DataBits;
import de.hanneseilers.jserial.core.Parity;
import de.hanneseilers.jserial.core.SerialDevice;
import de.hanneseilers.jserial.core.StopBits;
import jd2xx.JD2XX;
import jd2xx.JD2XXEvent;
import jd2xx.JD2XXEventListener;

/**
 * FTD2XX 32 bit connector
 * @author Hannes Eilers
 *
 */
public class JD2XXConnector extends AbstractConnector implements JD2XXEventListener {
	
	private JD2XX device = null;
	private boolean libLoaded = false;
	
	private Baudrates baudrate = Baudrates.BAUD_9600;
	private DataBits dataBits = DataBits.DATABITS_8;
	private StopBits stopBits = StopBits.STOPBIT_1;
	private Parity parity = Parity.NONE;
	private int timeout = 500;

	public JD2XXConnector() {
		log = LogManager.getLogger();
		connectorName = "jd2xx (32bit)";
		connectorLibDir = "jd2xx";
		
		try{
			libLoaded = loadRequiredLibs("JD2XX", true);
			if( libLoaded ){
				device = new JD2XX();
				log.info("Loaded {}", getConnectorName());
			}
		}catch (UnsatisfiedLinkError e){
			libLoaded = false;
		}
	}

	@Override
	synchronized public List<SerialDevice> getAvailableDevices() {
		List<SerialDevice> devices = new ArrayList<SerialDevice>();
		try {
			
			Object[] devs = device.listDevicesBySerialNumber();
			for( Object dev : devs )
				log.debug("found device: {}", dev);
			
		} catch (IOException e) {
			log.error("Can not list serial devices.");
		}
		
		return devices;
	}

	@Override
	public boolean connect(SerialDevice sDevice) {
		try{
			
			if( libLoaded ){
				device.openByDescription(sDevice.getJd2xxDevice());
				device.setTimeouts(timeout, timeout);
				device.setBaudRate(baudrate.baud);
				device.setDataCharacteristics(dataBits.bits, stopBits.bits_jd2xx, parity.parity_jd2xx);
				
				// Add listener
				device.addEventListener(this);
			}
			
		}catch (IOException e){
			log.error("Could not connect to device {}", sDevice);
		} catch (TooManyListenersException e) {
			log.error("Could not add data recieved listener to device {}", sDevice);
		}
		
		return false;
	}

	@Override
	public boolean disconnect() {
		try{
			
			if( device != null ){
				device.close();
				return true;
			}
			
		}catch (IOException e){
			log.error("Could not disconnect device.");
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
	synchronized public boolean write(byte b) {
		return write( new byte[]{b} );
	}

	@Override
	synchronized public boolean write(byte[] buffer) {
		try {
			device.write(buffer);
			return true;
		} catch (IOException e) {
			log.error("Could not write data to serial port {}", device);
		}
		return false;
	}

	@Override
	public void jd2xxEvent(JD2XXEvent event) {
		if( event.getEventType() == JD2XXEvent.EVENT_RXCHAR ){
			try {
				for( byte b : device.read(1) ){
					notifySerialDataRecievedListener( b );
				}				
			} catch (IOException e) {
				log.warn("Error recieving data byte from {}", device);
			}
		}
	}

}
