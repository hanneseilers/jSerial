package de.hanneseilers.jftdiserial.test;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hanneseilers.jftdiserial.core.Baudrates;
import de.hanneseilers.jftdiserial.core.DataBits;
import de.hanneseilers.jftdiserial.core.FTDISerial;
import de.hanneseilers.jftdiserial.core.Parity;
import de.hanneseilers.jftdiserial.core.SerialDevice;
import de.hanneseilers.jftdiserial.core.StopBits;

/**
 * Test class
 * @author Hannes Eilers
 *
 */
public class Test {

	private static final Logger log = LogManager.getLogger();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.debug("Createing FTDIserial");
		FTDISerial ftdi = new FTDISerial();
		
		// showing available libraries
		List<String> libs = ftdi.getAvailableLibNames();
		for( String lib : libs ){
			log.debug("LIB: " + lib);
		}
		
		// select first available lib manually
		if( libs.size() > 0 ){
			ftdi.selectLibByName( libs.get(0) );
		}
		
		// showing available devices
		List<SerialDevice> devices = ftdi.getAvailableDevices();
		for( SerialDevice dev : devices ){
			log.debug("DEVICE: " + dev);
		}
		
		// setting connection settings
		ftdi.setConnectionSettings(Baudrates.BAUD_300,
				DataBits.DATABITS_8, StopBits.STOPBIT_1, Parity.NONE, 3000);
		
		// connect to first device
		if( devices.size() > 0 ){
			ftdi.connect(devices.get(0));
		}
		log.debug("Connected to device = " + ftdi.isConnected());
		
		// write and read data
		if( ftdi.isConnected() ){
			ftdi.write( (byte) 0xef );
			for( byte b : ftdi.read(5) ){
				log.debug(b);
			}
		}
	}

}
