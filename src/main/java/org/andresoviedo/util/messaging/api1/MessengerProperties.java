package org.andresoviedo.util.messaging.api1;

/**
 * Utility class to retrieve global messaging properties.
 * 
 * @author andresoviedo
 */
public class MessengerProperties {

	/**
	 * The name of the logger the messaging API uses.
	 */
	public static String LOGGER_NAME = "org.andresoviedo.util.messaging.api1";

	/**
	 * The system property holding the client id to use with the messaging API. This property is mandatory, because no default value is
	 * used.
	 */
	public static String SYSTEM_PROPERTY_NODE_ID = "messaging.nodeId";

	/**
	 * The system property holding the maximum number of parallel Reader threads invoking the listener processMessage method at same time.
	 * Defaults to 10.
	 */
	public static String SYSTEM_PROPERTY_MAX_PARALLEL_THREADS = "messaging.maxParallelThreads";

	/**
	 * Returns the client id for the messaging service.
	 * 
	 * @return the client id for the messaging service.
	 */
	public static int getMaxParallelThreads() {
		return Integer.valueOf(System.getProperty(SYSTEM_PROPERTY_MAX_PARALLEL_THREADS, "10"));
	}

}
