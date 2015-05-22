package org.andresoviedo.util.messaging.api1.server;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.andresoviedo.util.messaging.api1.MessengerProperties;
import org.andresoviedo.util.messaging.api1.common.configuration.BasicConfiguration;


/**
 * Server messenger configuration.
 * 
 * @author andres
 */
public class ServerMessengerConfiguration extends BasicConfiguration {

	/**
	 * Configuration file system property name.
	 */
	public static final String SYSTEM_PROPERTY_CONFIGURATION_FILE = "messaging.server.properties";

	/**
	 * Host system property name.
	 */
	public static final String SYSTEM_PROPERTY_HOST = "messaging.server.host";

	/**
	 * Port system property name.
	 */
	public static final String SYSTEM_PROPERTY_PORT = "messaging.server.port";

	/**
	 * Persistence directory system property name.
	 */
	public static final String SYSTEM_PROPERTY_PERSISTENCE_DIRECTORY = "messaging.persistenceDirectory";

	/**
	 * Service system property prefix.
	 */
	public static final String SYSTEM_PROPERTY_PREFIX_SERVICE = "messaging.server.service";

	/**
	 * Host configuration file property name.
	 */
	private static final String PROPERTY_HOST = "host";

	/**
	 * Port configuration file property name.
	 */
	private static final String PROPERTY_PORT = "port";

	/**
	 * Persistence directory configuration file property name.
	 */
	private static final String PROPERTY_PERSISTENCE_DIRECTORY = "messaging.persistenceDirectory";

	/**
	 * Service configuration file property prefix.
	 */
	private static final String PROPERTY_PREFIX_SERVICE = "service";

	/**
	 * Configuration file's default value.
	 */
	private static final String DEFAULT_CONFIGURATION_FILE = "messaging.server.properties";

	/**
	 * The default address to bind the server socket to.
	 */
	private static final String DEFAULT_HOST = "127.0.0.1";

	/**
	 * The default port to bind the server socket to.
	 */
	private static final int DEFAULT_PORT = 41982;

	/**
	 * The default persistence directory.
	 */
	private static final String DEFAULT_PERSISTENCE_DIRECTORY = "."
			+ File.separator + "messages-server";

	/**
	 * The address to bind the server socket to.
	 */
	private String host;

	/**
	 * The port to bind the server socket to.
	 */
	private int port;

	/**
	 * The persistence directory.
	 */
	private File persistenceDirectory;

	/**
	 * The list of services to be instantiated.
	 */
	private List<String> serviceList;
	
	private String serverId;

	/**
	 * Constructs a new client messenger configuration.
	 */
	public ServerMessengerConfiguration() {
		// Try to load properties from a file.
		super.load(SYSTEM_PROPERTY_CONFIGURATION_FILE,
				DEFAULT_CONFIGURATION_FILE);
		
		this.serverId = getProperty(MessengerProperties.SYSTEM_PROPERTY_NODE_ID, UUID.randomUUID().toString());

		// Set the host.
		host = getProperty(PROPERTY_HOST, SYSTEM_PROPERTY_HOST, DEFAULT_HOST);
		// Set the port.
		try {
			port = Integer.parseInt(getProperty(PROPERTY_PORT,
					SYSTEM_PROPERTY_PORT, String.valueOf(DEFAULT_PORT)));
		} catch (NumberFormatException e) {
			port = DEFAULT_PORT;
		}
		// Set the persistence direetory.
		persistenceDirectory = new File(getProperty(
				PROPERTY_PERSISTENCE_DIRECTORY,
				SYSTEM_PROPERTY_PERSISTENCE_DIRECTORY,
				DEFAULT_PERSISTENCE_DIRECTORY));
		// Set the list of services. Check for properties named "service1",
		// "service2", [...] ,"serviceN".
		this.serviceList = new Vector<String>();
		int i = 1;
		String classname = null;
		while ((classname = getProperty(PROPERTY_PREFIX_SERVICE + i,
				SYSTEM_PROPERTY_PREFIX_SERVICE + i, null)) != null) {
			if (classname.length() > 0) {
				serviceList.add(classname);
			}
			i++;
		}
	}

	/**
	 * Returns the IP address to bind the server socket to (unsused for the
	 * moment).
	 * 
	 * @return the IP address to bind the server socket to.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the IP address to bind the server socket to (unsused for the
	 * moment).
	 * 
	 * @param host
	 *            the new IP address to bind the server socket to.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns the persistence directory.
	 * 
	 * @return the persistence directory.
	 */
	public File getPersistenceDirectory() {
		return persistenceDirectory;
	}

	/**
	 * Sets the persistence directory.
	 * 
	 * @param persistenceDirectory
	 *            the new persistence directory.
	 */
	public void setPersistenceDirectory(File persistenceDirectory) {
		this.persistenceDirectory = persistenceDirectory;
	}

	/**
	 * Returns the port to bind the server socket to.
	 * 
	 * @return the port to bind the server socket to.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port to bind the server socket to.
	 * 
	 * @param port
	 *            the new port to bind the server socket to.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the list of service class names.
	 * 
	 * @return the list of service class names.
	 */
	public List<String> getServiceList() {
		return serviceList;
	}

	/**
	 * Sets the list of service class names.
	 * 
	 * @param serviceList
	 *            the new list of service class names.
	 */
	public void setServiceList(List<String> serviceList) {
		this.serviceList = serviceList;
	}

	public String getServerId() {
		return this.serverId;
	}

}