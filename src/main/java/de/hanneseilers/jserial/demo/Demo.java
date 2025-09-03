package de.hanneseilers.jserial.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hanneseilers.jserial.core.Baudrates;
import de.hanneseilers.jserial.core.DataBits;
import de.hanneseilers.jserial.core.JSerial;
import de.hanneseilers.jserial.core.Parity;
import de.hanneseilers.jserial.core.SerialDevice;
import de.hanneseilers.jserial.core.StopBits;
import de.hanneseilers.jserial.core.interfaces.SerialDataRecievedListener;

/**
 * Test class
 * @author Hannes Eilers
 *
 */
public class Demo implements SerialDataRecievedListener {

	private static final Logger log = LogManager.getLogger();
	
	public void test(){
		log.debug("Createing FTDIserial");
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		
		JSerial ftdi = new JSerial();
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		
		// showing available libraries
		List<String> libs = ftdi.getAvailableLibNames();
		log.info( "Available libs = {}", libs.size() );
		for( int i=0; i<libs.size(); i++ ){
			String lib = libs.get(i);
			log.debug("LIB {}: {}", i, lib);
		}
		
		// select lib
		int nLib;
		System.out.print("Enter lib number to select:");
        try{
            nLib = Integer.parseInt(console.readLine());
        }catch(NumberFormatException | IOException e){
            nLib = 0;
        }
        
		if( nLib < libs.size() ){
			log.debug("Selected library {}", libs.get(nLib));
			ftdi.selectLibByName( libs.get(nLib) );
		}

		// setting connection settings
		ftdi.setConnectionSettings(Baudrates.BAUD_9600,
				DataBits.DATABITS_8, StopBits.STOPBIT_1, Parity.NONE, 500);
		
		// showing available devices
		List<SerialDevice> devices = ftdi.getAvailableDevices();
		for( int i=0; i<devices.size(); i++ ){
			SerialDevice dev = devices.get(i);
			log.debug("DEVICE {}: {}", i, dev);
		}
		
		// select device to connect to
		int nDev;
		System.out.print("Enter device number to select:");
        try{
        	nDev = Integer.parseInt(console.readLine());
        }catch(NumberFormatException | IOException e){
        	nDev = 0;
        }
        
		if( nDev < devices.size() ){
			log.debug("Selected device {}", devices.get(nDev));
			ftdi.connect( devices.get(nDev) );
		}
		log.debug("Connected to device = {}", ftdi.isConnected());
		
		// connect listener
		ftdi.addSerialDataRecievedListener(this);
		
		// write and read data
		if( ftdi.isConnected() ){
			
			ftdi.write( (byte) 0x3f );			
			log.debug("write {} to device", (char) 0x3f);
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		// close connection
		log.debug("Disconnecting");
		ftdi.disconnect();
		ftdi.removeAllSerialDataRecievedListener();
	}	
	

	@Override
	public void serialDataRecieved(byte data) {
		System.out.print( (char) data );
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Demo t = new Demo();
		t.test();
	}

}
