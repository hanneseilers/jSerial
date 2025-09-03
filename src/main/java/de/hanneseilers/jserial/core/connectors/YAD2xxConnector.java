package de.hanneseilers.jserial.core.connectors;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import de.hanneseilers.jserial.core.Baudrates;
import de.hanneseilers.jserial.core.DataBits;
import de.hanneseilers.jserial.core.Parity;
import de.hanneseilers.jserial.core.SerialDevice;
import de.hanneseilers.jserial.core.StopBits;
import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;

/**
 * FTD2xx 64 bit connector
 * @author Hannes Eilers
 *
 */
public class YAD2xxConnector extends AbstractConnector {

	private FTDIInterface ftdiInterface = null;
	private Device device = null;
	
	private Baudrates baudrate = Baudrates.BAUD_9600;
	// following data not used yet
//	private DataBits dataBits;
//	private StopBits stopBits;
//	private Parity parity;
	private int timeout = 500;
	
	/**
	 * Constructor
	 */
	public YAD2xxConnector() {
		log = LogManager.getLogger();
		connectorName = "yad2xx (64bit)";
		connectorLibDir = "yad2xx";
		
		try{			
			// load library
			libLoaded = loadRequiredLibs("FTDIInterface");
			if( libLoaded ){
				ftdiInterface = new FTDIInterface();
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
			
			if( libLoaded ){
				for(Device dev : ftdiInterface.getDevices() ){
					devices.add( new SerialDevice(dev) );
				}
			}
			
		} catch (FTDIException e) {
			log.error("Can not list devices.");
		}	
		
		return devices;
	}

	@Override
	public boolean connect(SerialDevice sDevice) {
		// check if any device is already open
		if( device != null && device.isOpen() ){
			disconnect();
		}
		
		// connect to new device		
		try {
			
			if( libLoaded ){
				device = sDevice.getYad2xxDevice();
				device.open();
				device.setTimeouts(timeout, timeout);
				device.setBaudRate(baudrate.baud);
				
				// start reading
				new Thread( new SerialDataReader(device) ).start();
				
				return true;
			}
			
		} catch (FTDIException e) {
			log.error("Could not connect to device {}", sDevice);
		} catch (IllegalStateException e){
			log.error("Could not open device {}", sDevice);
		}
		
		
		return false;
	}

	@Override
	public boolean disconnect() {
		try{
			
			if( device != null ){
				device.close();
				device = null;
				return true;
			}
			
		}catch (FTDIException e){
			log.error("Could not disconnect device {}", device);
		}
		return false;
	}

	@Override
	public boolean setConnectionSettings(Baudrates baudrate, DataBits dataBits,
			StopBits stopBits, Parity parity, int timeout) {
		this.baudrate = baudrate;
		// following data not used yet
//		this.dataBits = dataBits;
//		this.stopBits = stopBits;
//		this.parity = parity;
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
		try{
			
			if( device != null && device.isOpen() ){
				device.write(buffer);
				return true;
			}
			
		}catch (FTDIException e){
			log.warn("Exception while writing to device {}", (device != null ? device.getSerialNumber() : device));
		}
		return false;
	}
	
	/**
	 * Class for readings data from serial port
	 * @author Hannes Eilers
	 *
	 */
	private class SerialDataReader implements Runnable{

		private Device device;
		
		public SerialDataReader(Device aDevice) {
			device = aDevice;
		}
		
		@Override
		public void run() {
			boolean active = true;
			
			while( active && device.isOpen() ){
				try {
					byte[] buffer = new byte[1];
					if( device.read( buffer ) > 0){
						notifySerialDataRecievedListener( buffer[0] );
					}
					
				} catch (FTDIException e) {
					log.warn("Exception while reading from device {}", (device != null ? device.getSerialNumber() : device));
					active = false;
				}
			}
		}

	}

}
