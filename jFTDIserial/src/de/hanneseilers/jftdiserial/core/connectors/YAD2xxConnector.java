package de.hanneseilers.jftdiserial.core.connectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
			
			// load library
			
			ftdiInterface = new FTDIInterface();
			libLoaded = true;
			log.info("Loaded " + getConnectorName());
		}catch (UnsatisfiedLinkError e){
			libLoaded = false;
		}
	}
	
	/**
	 * Copies required library files to a valid destination directory and loads library.
	 * @return {@code true\ if successfull, {@code false} otherwise.
	 */
	private boolean loadRequiredLibs(){
		String libPath = "./"+System.getProperty("java.library.path");
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
						log.info("Copied yad2xx lib to " + libDestination.getPath());
						return true;
					}catch (IOException e){
						log.debug("Can not copy yad2xx library to " + libDestination.getPath());
					}catch (UnsatisfiedLinkError e){
						log.warn("Could not load yad2xx library!");
					}
					
				}
				
			}	
		} else{
			log.warn("yad2xx library doesn't support your operating system!");
		}
		
		return false;
	}
	
	/**
	 * @return OS depended library {@link File}, or {@code null} if no library for this os could be found.
	 */
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
			File libFile = new File("lib/yad2xx/" + os + "/" + bit + "/" + prefix + "rxtxSerial" + ending);
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
