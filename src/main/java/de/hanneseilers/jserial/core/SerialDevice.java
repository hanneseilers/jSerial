package de.hanneseilers.jserial.core;

import gnu.io.CommPortIdentifier;
import net.sf.yad2xx.Device;

public class SerialDevice {

	private String deviceName = null;
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
		deviceName = device;
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
	public String getDeviceName() {
		return deviceName;
	}
	
	public String toString(){
		if( yad2xxDevice != null ){
			return yad2xxDevice.getDescription()
					+ "(" + yad2xxDevice.getSerialNumber() + ")";
		}
		else if( deviceName != null ){
			return deviceName;
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
