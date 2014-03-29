package de.hanneseilers.jftdiserial.core;

import net.sf.yad2xx.Device;

public class SerialDevice {

	private String jd2xxDevice = null;
	private Device yad2xxDevice = null;
	
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
		
		return "";
	}

}
