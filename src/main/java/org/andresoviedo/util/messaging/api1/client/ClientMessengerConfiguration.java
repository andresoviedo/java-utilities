package org.andresoviedo.util.messaging.api1.client;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

import org.andresoviedo.util.messaging.api1.MessengerProperties;
import org.andresoviedo.util.messaging.api1.common.configuration.BasicConfiguration;

/**
 * An object holding client messenger configuration parameters.
 * 
 * @author andresoviedo
 */
public class ClientMessengerConfiguration extends BasicConfiguration {

	/**
	 * A static reference to the logger object.
	 */
	private static Logger logger = Logger.getLogger(MessengerProperties.LOGGER_NAME);

	/**
	 * Secondary remote host system property name.
	 */
	public static final String SYSTEM_PROPERTY_ADDRESS = "messaging.client.address";

	/**
	 * The name of the configuration file system property.
	 */
	public static final String SYSTEM_PROPERTY_CONFIGURATION_FILE = "messaging.client.properties";

	/**
	 * Persistence directory system property name.
	 */
	public static final String SYSTEM_PROPERTY_PERSISTENCE_DIRECTORY = "messaging.persistenceDirectory";

	/**
	 * Reconnection delay system property name.
	 */
	public static final String SYSTEM_PROPERTY_RECONNECTION_DELAY = "messaging.client.reconnectionDelay";

	/**
	 * Remote host configuration file property name.
	 */
	private static final String PROPERTY_ADDRESS = "address";

	/**
	 * Persistence directory configuration file property name.
	 */
	private static final String PROPERTY_PERSISTENCE_DIRECTORY = "messaging.persistenceDirectory";

	/**
	 * Reconnection delay configuration file property name.
	 */
	private static final String PROPERTY_RECONNECTION_DELAY = "reconnection-delay";

	/**
	 * Configuration file's default value.
	 */
	private static final String DEFAULT_CONFIGURATION_FILE = "messaging.client.properties";

	/**
	 * The default list of addresses to connect to.
	 */
	private static final InetSocketAddress[] DEFAULT_ADDRESSES = { new InetSocketAddress("localhost", 41982),
			new InetSocketAddress("127.0.0.1", 41982), new InetSocketAddress("127.0.0.2", 41982) };

	/**
	 * The default persistence directory.
	 */
	private static final String DEFAULT_PERSISTENCE_DIRECTORY = ".\\messages-client";

	/**
	 * The default reconnection delay.
	 */
	private static final int DEFAULT_RECONNECTION_DELAY = 30000;

	/**
	 * The list of addresses to connect to.
	 */
	private InetSocketAddress[] addresses;

	/**
	 * The time to wait before trying to reconnect after a failed connection attempt.
	 */
	private int reconnectionDelay;

	/**
	 * The persistence directory.
	 */
	private File persistenceDirectory;

	/**
	 * Client id. Must be unique for all clients.
	 */
	private String clientId;

	/**
	 * Constructs a new client messenger configuration.
	 */
	public ClientMessengerConfiguration(String configFile) {
		// Try to load properties from a file.
		super.load(SYSTEM_PROPERTY_CONFIGURATION_FILE, configFile != null ? configFile : DEFAULT_CONFIGURATION_FILE);

		this.clientId = getProperty(MessengerProperties.SYSTEM_PROPERTY_NODE_ID, UUID.randomUUID().toString());

		// Get the addresses to connect to.
		int i = 1;
		String address;
		String[] tokens;
		List<InetSocketAddress> temp = new Vector<InetSocketAddress>();
		while ((address = getProperty(PROPERTY_ADDRESS + i, SYSTEM_PROPERTY_ADDRESS + i, null)) != null) {
			tokens = address.split("@");
			try {
				temp.add(new InetSocketAddress(tokens[0], Integer.parseInt(tokens[1])));
			} catch (Exception e) {
				logger.warning("Invalid address specified: " + address + " (" + e.getMessage() + ")");
			}
			i++;
		}

		// No addresses specified, used the default ones.
		if (temp.isEmpty()) {
			logger.info("No addresses specified, using default ones...");
			this.addresses = DEFAULT_ADDRESSES;
		} else {
			this.addresses = (InetSocketAddress[]) temp.toArray(new InetSocketAddress[temp.size()]);
		}

		// Set the reconnection delay.
		try {
			reconnectionDelay = Integer.parseInt(getProperty(PROPERTY_RECONNECTION_DELAY, SYSTEM_PROPERTY_RECONNECTION_DELAY,
					String.valueOf(DEFAULT_RECONNECTION_DELAY)));
		} catch (NumberFormatException e) {
			reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
		}
		// Set the persistence direetory.
		persistenceDirectory = new File(getProperty(PROPERTY_PERSISTENCE_DIRECTORY, SYSTEM_PROPERTY_PERSISTENCE_DIRECTORY,
				DEFAULT_PERSISTENCE_DIRECTORY));
	}

	/**
	 * Returns the list of addresses to connect to.
	 * 
	 * @return the list of addresses to connect to.
	 */
	public InetSocketAddress[] getAddresses() {
		return addresses;
	}

	/**
	 * Sets the list of addresses to connect to.
	 * 
	 * @param addresses
	 *            the list of addresses to connect to.
	 */
	public void setAddresses(InetSocketAddress[] addresses) {
		this.addresses = addresses;
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
	 * Returns the reconnection delay (in milliseconds).
	 * 
	 * @return the reconnection delay (in milliseconds).
	 */
	public int getReconnectionDelay() {
		return reconnectionDelay;
	}

	/**
	 * Sets the reconnection delay (in milliseconds).
	 * 
	 * @param reconnectionDelay
	 *            the new reconnection delay (in milliseconds).
	 */
	public void setReconnectionDelay(int reconnectionDelay) {
		this.reconnectionDelay = reconnectionDelay;
	}

	public String getClientId() {
		return clientId;
	}

}