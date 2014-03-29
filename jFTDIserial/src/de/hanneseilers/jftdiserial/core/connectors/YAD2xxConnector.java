package de.hanneseilers.jftdiserial.core.connectors;

import java.util.ArrayList;
import java.util.List;

import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hanneseilers.jftdiserial.core.Baudrates;
import de.hanneseilers.jftdiserial.core.DataBits;
import de.hanneseilers.jftdiserial.core.Parity;
import de.hanneseilers.jftdiserial.core.SerialDevice;
import de.hanneseilers.jftdiserial.core.StopBits;
import de.hanneseilers.jftdiserial.core.interfaces.jFTDIserialConnector;

/**
 * FTD2xx 64 bit connector
 * @author Hannes Eilers
 *
 */
public class YAD2xxConnector implements jFTDIserialConnector {

	private static final Logger log = LogManager.getLogger();
	private FTDIInterface ftdiInterface = null;
	private Device device = null;
	private boolean libLoaded = false;
	
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
		try{
			ftdiInterface = new FTDIInterface();
			libLoaded = true;
			log.info("Loaded " + getConnectorName());
		}catch (UnsatisfiedLinkError e){
			libLoaded = false;
		}
	}
	
	@Override
	public String getConnectorName() {
		return "yad2xx (64bit)";
	}
	
	@Override
	public List<SerialDevice> getAvailableDevices() {
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
				return true;
			}
			
		} catch (FTDIException e) {
			log.error("Could not connect to device " + sDevice);
		} catch (IllegalStateException e){
			log.error("Could not open device " + sDevice);
		}
		
		
		return false;
	}

	@Override
	public boolean connect() {
		List<SerialDevice> devices = getAvailableDevices();
		if( devices.size() > 0 ){
			return connect( devices.get(0) );
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
			log.error("Could not disconnect device " + device);
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
	public byte read() {
		return read(1)[0];
	}

	@Override
	public byte[] read(int num) {
		byte[] buffer = new byte[num];
		
		try{
			
			if( device != null && device.isOpen()){
				device.read(buffer);
			}
			
		}catch (FTDIException e){
			log.error("Exception while reading from device " + (device != null ? device.getSerialNumber() : device));
		}
		
		return buffer;
	}

	@Override
	public boolean write(byte b) {
		return write( new byte[]{b} );
	}

	@Override
	public boolean write(byte[] buffer) {
		try{
			
			if( device != null && !device.isOpen() ){
				device.write(buffer);
				return true;
			}
			
		}catch (FTDIException e){
			log.error("Exception while writing to device " + (device != null ? device.getSerialNumber() : device));
		}
		return false;
	}

	

}
