package de.hanneseilers.jserial.core.connectors;

import de.hanneseilers.jserial.core.*;
import com.fazecast.jSerialComm.*;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class jSerialCommConnector extends AbstractConnector implements SerialPortDataListener {

	private SerialPort device;
	private InputStream input;
	private OutputStream output;

    private Baudrates baudrate = Baudrates.BAUD_9600;
	private DataBits dataBits;
	private StopBits stopBits;
	private Parity parity;
	private int timeout = 500;

	public jSerialCommConnector() {
		log = LogManager.getLogger();
		connectorName = "jSerialComm (fazecast)";
		connectorLibDir = "jserialcomm";
        log.info("Loaded {}", getConnectorName());
	}

	@Override
	synchronized  public List<SerialDevice> getAvailableDevices() {
		List<SerialDevice> devices = new ArrayList<>();

        for (SerialPort port : List.of(SerialPort.getCommPorts())) {
            if (port != null) {
                devices.add(new SerialDevice(port.getSystemPortName() + ": " + port.getDescriptivePortName()));
            }
        }
		
		return devices;
	}

	@Override
	public boolean connect(SerialDevice sDevice) {
		try{

            String deviceName = sDevice.getDeviceName().split(":")[0];
			device = SerialPort.getCommPort(deviceName);
            device.setComPortParameters(baudrate.baud, dataBits.bits_rxtx, stopBits.bits_rxtx, parity.parity_rxtx);
            device.addDataListener(this);
            device.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeout, timeout);

			input = device.getInputStream();
			output = device.getOutputStream();
			return true;

		}catch (Exception e){
			log.error("Can not open port {}", sDevice.getDeviceName());
		}
		return false;
	}

	@Override
	public boolean disconnect() {
		if( device != null ){
			try{
				device.closePort();
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
        return true;
	}

	@Override
	public boolean write(byte b) {
		return write( new byte[]{b} );
	}
	
	@Override
	synchronized public boolean write(byte[] buffer) {
		if( device != null && output != null ){
			try{
				output.write(buffer);
			}catch (IOException e){
				log.error("Could not write data to serial port {}", device.getSystemPortName());
			}
		}
		
		return false;
	}

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE | SerialPort.LISTENING_EVENT_DATA_WRITTEN | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
    }

    @Override
	public void serialEvent(SerialPortEvent event) {
        log.debug("Received serial port event {}", event);
    }
}
