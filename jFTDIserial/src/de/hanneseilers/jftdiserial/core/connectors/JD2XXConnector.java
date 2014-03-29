package de.hanneseilers.jftdiserial.core.connectors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hanneseilers.jftdiserial.core.Baudrates;
import de.hanneseilers.jftdiserial.core.DataBits;
import de.hanneseilers.jftdiserial.core.Parity;
import de.hanneseilers.jftdiserial.core.SerialDevice;
import de.hanneseilers.jftdiserial.core.StopBits;
import de.hanneseilers.jftdiserial.core.interfaces.jFTDIserialConnector;
import jd2xx.JD2XX;

/**
 * FTD2XX 32 bit connector
 * @author Hannes Eilers
 *
 */
public class JD2XXConnector implements jFTDIserialConnector {
	
	private static final Logger log = LogManager.getLogger();
	private JD2XX device = null;
	private boolean libLoaded = false;
	
	private Baudrates baudrate = Baudrates.BAUD_9600;
	private DataBits dataBits = DataBits.DATABITS_8;
	private StopBits stopBits = StopBits.STOPBIT_1;
	private Parity parity = Parity.NONE;
	private int timeout = 500;

	public JD2XXConnector() {
		try{
			device = new JD2XX();
			libLoaded = true;
			log.info("Loaded " + getConnectorName());
		}catch (UnsatisfiedLinkError e){
			libLoaded = false;
		}
	}
	
	@Override
	public String getConnectorName() {
		return "jd2xx (32bit)";
	}

	@Override
	public List<SerialDevice> getAvailableDevices() {
		List<SerialDevice> devices = new ArrayList<SerialDevice>();
		try {
			
			Object[] devs = device.listDevicesBySerialNumber();
			for( Object dev : devs )
				log.debug("found device: " + dev);
			
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
			}
			
		}catch (IOException e){
			log.error("Could not connect to device " + sDevice);
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
	public byte read() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] read(int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean write(byte b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean write(byte[] buffer) {
		// TODO Auto-generated method stub
		return false;
	}

}
