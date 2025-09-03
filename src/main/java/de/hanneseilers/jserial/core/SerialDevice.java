package de.hanneseilers.jserial.core;

import gnu.io.CommPortIdentifier;
import net.sf.yad2xx.Device;

public class SerialDevice {

	private String jd2xxDevice = null;
	private Device yad2xxDevice = null;
	private CommPortIdentifier rxtxDevice = null;
	
	/**
	 * Constructor
	 * @param device {@link Device}
	 */
	public SerialDevice(Device device) {
		yad2xxDevice = device;
	}
	
	/**
	 * Constructor
	 * @param device	{@link String} of device description
	 */
	public SerialDevice(String device){
		jd2xxDevice = device;
	}
	
	/**
	 * Constructor
	 * @param device	{@link CommPortIdentifier}
	 */
	public SerialDevice(CommPortIdentifier device){
		rxtxDevice = device;
	}

	/**
	 * @return the yad2xxDevice
	 */
	public Device getYad2xxDevice() {
		return yad2xxDevice;
	}

	/**
	 * @return the jd2xxDevice
	 */
	public String getJd2xxDevice() {
		return jd2xxDevice;
	}
	
	public String toString(){
		if( yad2xxDevice != null ){
			return yad2xxDevice.getDescription()
					+ "(" + yad2xxDevice.getSerialNumber() + ")";
		}
		else if( jd2xxDevice != null ){
			return jd2xxDevice;
		}
		else if( rxtxDevice != null ){
			return rxtxDevice.getName();
		}
		
		return "";
	}

	/**
	 * @return the rxtxDevice
	 */
	public CommPortIdentifier getRxTxDevice() {
		return rxtxDevice;
	}

}
