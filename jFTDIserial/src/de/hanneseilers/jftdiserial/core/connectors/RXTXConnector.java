package de.hanneseilers.jftdiserial.core.connectors;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hanneseilers.jftdiserial.core.Baudrates;
import de.hanneseilers.jftdiserial.core.DataBits;
import de.hanneseilers.jftdiserial.core.Parity;
import de.hanneseilers.jftdiserial.core.SerialDevice;
import de.hanneseilers.jftdiserial.core.StopBits;
import de.hanneseilers.jftdiserial.core.interfaces.jFTDIserialConnector;

public class RXTXConnector implements jFTDIserialConnector {

	private static final Logger log = LogManager.getLogger();
	
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
		String libPath = System.getProperty("java.library.path");
		StringTokenizer libPathParser = new StringTokenizer(libPath, ";");
		File libSource = getRxTxLibSource();
		
		if( libSource != null ){
			while( libPathParser.hasMoreElements() ){
				
				// get path for library
				libPath = libPathParser.nextToken();
				File libDestination = new File(libPath + "/" + libSource.getName());
				
				// check if can write in path
				if( libSource.canRead() ){
					
					try{
						// copy lib
						Files.copy( Paths.get(libSource.getPath()), Paths.get(libDestination.getPath()) );
					
						// try to load library					
						System.loadLibrary("rxtxSerial");
						libLoaded = true;
						log.info("Copied rxtx lib to " + libDestination.getPath());
						break;
					}catch (IOException e){
						log.debug("Can not copy rxtx library to " + libDestination.getPath());
					}catch (UnsatisfiedLinkError e){
						log.warn("Could not load rxtx library!");
					}
					
				}
				
			}	
		} else{
			log.warn("rxtx library doesn't support your operating system!");
		}
	}
	
	private File getRxTxLibSource(){
		String os = System.getProperty("os.name").toLowerCase();
		String bit = System.getProperty("sun.arch.data.model");
		String ending = null;
		String prefix = "";
		
		// Get os type
		// WINDOWS
		if( os.indexOf("win") >= 0 ){
			os = "windows";
			ending = ".dll";
		}
		// LINUX
		else if( os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 ){
			os = "linux";
			ending = ".so";
		}
		// MAC
		else if( os.indexOf("mac") >= 0 ){
			os = "mac";
			prefix = "lib";
			ending = ".jnilib";
		}
		// SOLARIS (not supported)
		else if( os.indexOf("sunos") >= 0 ){
			os = "solaris";
			prefix = "lib";
		}
		// OS NOT SUPPORTED
		else{
			os = null;
		}
		
		// get os 32 or 64 bit
		if( bit.contains("64") ){
			bit = "64bit";
		}
		else if( bit.contains("32") ){
			bit = "32bit";
		}
		else{
			bit = null;
		}
		
		if( os != null && bit != null ){
		
			// get file
			File libFile = new File("lib/" + os + "/" + bit + "/" + prefix + "rxtxSerial" + ending);
			log.debug("OS: " + os);
			log.debug("BIT: " + bit);
			log.debug("RXTX-LIB: " + libFile.getPath());
			
			if( libFile.isFile() ){
				return libFile;
			}
			
		}
		
		return null;
	}
	
	@Override
	public String getConnectorName() {
		return "rxtx serial";
	}

	@Override
	public List<SerialDevice> getAvailableDevices() {
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
			
			input = device.getInputStream();
			output = device.getOutputStream();
			return true;
			
		}catch (PortInUseException e){
			log.error("Serial port " + sDevice.getRxTxDevice().getName() + " is in use.");
		}catch (Exception e){
			log.error("Can not open port " + sDevice.getRxTxDevice().getName());
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
	public byte read() {
		return read(1)[0];
	}

	@Override
	public byte[] read(int num) {
		byte[] buffer = new byte[num];		
		try {
			
			if( device != null && input != null ){
				input.read(buffer);
			}
			
		} catch (IOException e) {
			log.error("Could not read data from serial port.");
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
			
			if( device != null && output != null ){
				output.write(buffer);
			}
			
		}catch (IOException e){
			log.error("Could not write data to serial port.");
		}
		
		return false;
	}

}
