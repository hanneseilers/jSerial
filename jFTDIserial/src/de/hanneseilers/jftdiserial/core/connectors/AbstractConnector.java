package de.hanneseilers.jftdiserial.core.connectors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hanneseilers.jftdiserial.core.FTDISerial;
import de.hanneseilers.jftdiserial.core.SerialDataRecievedRunnable;
import de.hanneseilers.jftdiserial.core.interfaces.SerialDataRecievedListener;
import de.hanneseilers.jftdiserial.core.interfaces.jFTDIserialConnector;

public abstract class AbstractConnector implements jFTDIserialConnector {

	protected String connectorName = "connector";
	protected String connectorLibDir = "connector";
	protected boolean libLoaded = false;
	protected Logger log = LogManager.getLogger();
	protected List<SerialDataRecievedListener> serialDataRecievedListeners = new ArrayList<SerialDataRecievedListener>();
	
	@Override
	public void addSerialDataRecievedListener(SerialDataRecievedListener listener){
		synchronized (serialDataRecievedListeners) {
			serialDataRecievedListeners.add(listener);
			log.debug("Added listener {}", listener);
		}
	}
	
	@Override
	public void removeSerialDataRecievedListener(SerialDataRecievedListener listener){
		synchronized (serialDataRecievedListeners) {
			serialDataRecievedListeners.remove(listener);
			log.debug("Removed listener {}", listener);
		}
	}
	
	@Override
	public void removeAllSerialDataRecievedListener(){
		synchronized (serialDataRecievedListeners) {
			serialDataRecievedListeners.clear();
			log.debug("Removed all listener");
		}
	}
	
	/**
	 * Notifies all registered {@link SerialDataRecievedListener} about new data.
	 * @param data	Recieved {@link Byte} data 
	 */
	protected void notifySerialDataRecievedListener(byte data){
		synchronized (serialDataRecievedListeners) {
			for( SerialDataRecievedListener listener : serialDataRecievedListeners ){
				new Thread( new SerialDataRecievedRunnable(listener, data) ).start();
				
			}
		}
	}
	
	
	/**
	 * Copies required library files to a valid destination directory and loads library.
	 * @param libFileName	{@link String} connectorName of library file wihtout prefix 'lib' or ending.
	 * @param loadLib		If {@code true} library is loaded into JVM.
	 * @return 				{@code true\ if successfull, {@code false} otherwise.
	 */
	protected boolean loadRequiredLibs(String libFileName, boolean loadLib){
		if( FTDISerial.connectorLibsLoaded ){
			return true;
		}
		
		String libPath = "./;" + System.getProperty("java.library.path");
		StringTokenizer libPathParser = new StringTokenizer(libPath, ";");
		File libSource = getLibSource(libFileName);
		
		if( libSource != null ){
			while( libPathParser.hasMoreElements() ){
				
				// get path for library
				libPath = libPathParser.nextToken();
				File libDestination = new File(libPath + "/" + libSource.getName());
				
				// check if can write in path
				if( libSource.canRead() ){
					
					try{
						// copy lib
						// TODO: check for errors on loading 64bit linux lib
						copyFile( libSource, libDestination, true );
						log.info("Copied lib to {}", libDestination.getPath());
						if( loadLib ){
							System.load(libDestination.getAbsolutePath());
							log.debug("Loaded library " + libDestination.getAbsolutePath());
						} else throw new IOException();						
						return true;
					}catch (IOException e){
						log.debug("Can not copy library to {}", libDestination.getPath());
					}catch (UnsatisfiedLinkError e){
						log.warn("Could not load library {}", libDestination.getPath() + "!");
					}
					
				}
				
			}	
		} else{
			log.warn("{} library doesn't support your operating system!", libFileName);
		}
		
		return false;
	}
	
	/**
	 * Gets file to load depending on the current operating system.
	 * @param libFileName	{@link String} connectorName of library file wihtout prefix 'lib' or ending.
	 * @return Library 		{@link File}, or {@code null} if no library for this os could be found.
	 */
	private File getLibSource(String libFileName){
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
			prefix ="lib";
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
			File libFile = new File("lib/" + connectorLibDir + "/"
					+ os + "/" + bit + "/" + prefix + libFileName + ending);
			log.debug("OS: {}",os);
			log.debug("BIT: {}", bit);
			log.debug("LIB: {}", libFile.getPath());
			
			if( libFile.isFile() ){
				return libFile;
			}
			
		}
		
		return null;
	}

	/**
	 * Copies file from source to destination path.
	 * @param from			{@link File}
	 * @param to			{@link File}
	 * @param overwrite		If {@code true} an existing file will be overwritten.
	 * @throws IOException
	 */
	private static void copyFile(File from, File to, Boolean overwrite) throws IOException {

        try {
            File fromFile = from;
            File toFile = to;

            if (!fromFile.exists()) {
                throw new IOException("File not found: " + from);
            }
            if (!fromFile.isFile()) {
                throw new IOException("Can't copy directories: " + from);
            }
            if (!fromFile.canRead()) {
                throw new IOException("Can't read file: " + from);
            }

            if (toFile.isDirectory()) {
                toFile = new File(toFile, fromFile.getName());
            }

            if (toFile.exists() && !overwrite) {
                throw new IOException("File already exists.");
            } else {
                String parent = toFile.getParent();
                if (parent == null) {
                    parent = System.getProperty("user.dir");
                }
                File dir = new File(parent);
                if (!dir.exists()) {
                    throw new IOException("Destination directory does not exist: " + parent);
                }
                if (dir.isFile()) {
                    throw new IOException("Destination is not a valid directory: " + parent);
                }
                if (!dir.canWrite()) {
                    throw new IOException("Can't write on destination: " + parent);
                }
            }

            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {

                fis = new FileInputStream(fromFile);
                fos = new FileOutputStream(toFile);
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.flush();

            } finally {
                if (from != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                      System.err.println(e);
                    }
                }
                if (to != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            }

        } catch (Exception e) {
            throw new IOException("Problems when copying file.");
        }
    }
	
	@Override
	public String getConnectorName() {
		return connectorName;
	}
	
}
