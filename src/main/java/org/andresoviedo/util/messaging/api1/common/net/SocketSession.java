package org.andresoviedo.util.messaging.api1.common.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.andresoviedo.util.messaging.api1.Messenger;
import org.andresoviedo.util.messaging.api1.MessengerProperties;
import org.andresoviedo.util.messaging.api1.common.data.Command;
import org.andresoviedo.util.messaging.api1.common.data.Message;
import org.andresoviedo.util.messaging.api1.common.data.PingRequest;

/**
 * A socket session used to connect to a remote server and exchange message objects.
 * 
 * @author andresoviedo
 */
public class SocketSession {

	/**
	 * A static reference to the logger object.
	 */
	private static Logger logger = Logger.getLogger(MessengerProperties.LOGGER_NAME);

	/**
	 * The default connection timeout.
	 */
	private static final int DEFAULT_CONNECTION_TIMEOUT = 20000;

	/**
	 * The amount of time to wait before pings are sent to check the connection.
	 */
	private static final int DEFAULT_PING_DELAY = 10000;

	/**
	 * The default reconnection delay (30 seconds).
	 */
	private static final int DEFAULT_RECONNECTION_DELAY = 30000;

	/**
	 * The time to wait until the object input serialization stream header is read.
	 */
	private static final int INITIAL_RECEIVE_TIMEOUT = 10000;

	/**
	 * The number of calls before resetting the object output stream.
	 */
	private static final int RESET_FREQUENCY = 1;

	/**
	 * The socket session controller.
	 */
	private SocketSessionController controller;

	/**
	 * The client socket.
	 */
	private Socket socket;

	/**
	 * Lock to access the socket.
	 */
	private Object socketLock = new Object();

	/**
	 * The input stream to read from.
	 */
	private ObjectInputStream ois;

	/**
	 * The output stream to write from.
	 */
	private ObjectOutputStream oos;

	/**
	 * The reader thread.
	 */
	private Reader reader;

	/**
	 * The sender thread.
	 */
	private Writer writer;

	/**
	 * A map to store additional helpful information associated to the session.
	 */
	private Map<Object, Object> properties;

	/**
	 * The thread managing reconnections.
	 */
	private Connector connector;

	/**
	 * A thread used to test the connection.
	 */
	private ConnectionTester tester;

	/**
	 * The list of socket addresses to connect to. Will be <code>null</code> if the socket constructor is used.
	 */
	private InetSocketAddress[] addresses;

	/**
	 * The reconnection delay.
	 */
	private int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;

	/**
	 * A flag indicating whether the session is closed or not.
	 */
	private boolean closed = true;

	/**
	 * Counter used to reset the object output stream every <code>RESET_FREQUENCY</code> write calls.
	 */
	private int counter = 0;

	/**
	 * The date when this session was successfully opened for the last time.
	 */
	private long lastConnection = 0;

	/**
	 * client id. unique for all clients
	 */
	private String clientId;

	/**
	 * Creates a new socket session.
	 * 
	 * @param address
	 *            the address of the remote server to connect to.
	 * @param port
	 *            the remote port.
	 * @param controller
	 *            the controller whishing to receive session events.
	 * @throws IllegalArgumentException
	 *             if either <code>address</code> or <code>controller</code> are <code>null</code>, or the port is not valid.
	 */
	public SocketSession(String clientId, InetAddress address, int port, SocketSessionController controller) {
		this.clientId = clientId;
		if (address == null) {
			throw new IllegalArgumentException("Address is null.");
		}
		if (controller == null) {
			throw new IllegalArgumentException("Controller is null.");
		}
		// Generate socket addresses.
		this.addresses = new InetSocketAddress[1];
		this.addresses[0] = new InetSocketAddress(address, port);
		// Initialize variables.
		this.controller = controller;
		this.properties = new HashMap<Object, Object>();
	}

	/**
	 * Creates a new socket session.
	 * 
	 * @param host
	 *            the remote host name or IP address to connect to.
	 * @param port
	 *            the remote port.
	 * @param controller
	 *            the controller whishing to receive session events.
	 * @throws IllegalArgumentException
	 *             if <code>controller</code> is <code>null</code> or <code>host</code> is null, or the port is not valid.
	 */
	public SocketSession(String clientId, String host, int port, SocketSessionController controller) {
		this.clientId = clientId;
		if (controller == null) {
			throw new IllegalArgumentException("Controller is null.");
		}
		if (host == null) {
			throw new IllegalArgumentException("Host is null.");
		}
		// Generate socket addresses.
		this.addresses = new InetSocketAddress[1];
		this.addresses[0] = new InetSocketAddress(host, port);
		// Initialize variables.
		this.controller = controller;
		this.properties = new HashMap<Object, Object>();
	}

	/**
	 * Creates a new socket session.
	 * 
	 * @param addresses
	 *            the list of addresses to connect to.
	 * @param controller
	 *            the controller whishing to receive session events.
	 * @throws IllegalArgumentException
	 *             if <code>addresses</code> is <code>null</code>, no addresses are specified, the addresses array contains nulls, or
	 *             <code>controller</code> is null.
	 */
	public SocketSession(String clientId, InetSocketAddress[] addresses, SocketSessionController controller) {
		this.clientId = clientId;
		if (addresses == null) {
			throw new IllegalArgumentException("The list of addresses is null.");
		}
		if (addresses.length == 0) {
			throw new IllegalArgumentException("No addresses specified.");
		}
		for (int i = 0; i < addresses.length; i++) {
			if (addresses[i] == null) {
				throw new IllegalArgumentException("No null addresses allowed in the addresses array.");
			}
		}
		if (controller == null) {
			throw new IllegalArgumentException("Controller is null.");
		}
		// Initialize variables.
		this.addresses = addresses;
		this.controller = controller;
		this.properties = new HashMap<Object, Object>();
	}

	/**
	 * Creates a new socket session.
	 * 
	 * @param socket
	 *            the socket to connect to.
	 * @param controller
	 *            the controller whishing to receive session events.
	 * @throws IllegalArgumentException
	 *             if either <code>socket</code> or <code>controller</code> are <code>null</code>.
	 */
	public SocketSession(String clientId, Socket socket, SocketSessionController controller) {
		this.clientId = clientId;
		if (socket == null) {
			throw new IllegalArgumentException("Socket is null.");
		}
		if (controller == null) {
			throw new IllegalArgumentException("Controller is null.");
		}
		// Initialize variables.
		this.socket = socket;
		this.controller = controller;
		this.properties = new HashMap<Object, Object>();
	}

	/**
	 * Returns whether this session is opened or not.
	 * 
	 * @return <code>true</code> if the session is opened, <code>false</code> otherwise.
	 */
	public synchronized boolean isOpened() {
		return !closed;
	}

	/**
	 * Opens this session. This method does nothing if the session is already opened.
	 */
	public synchronized void open() throws SocketSessionException {
		if (!closed) {
			return;
		}

		try {
			// Create the socket (a non-socket constructor has been used).
			if (this.addresses != null) {
				Socket tempSocket = null;
				// Loop through all possible addresses.
				for (int i = 0; i < addresses.length; i++) {
					logger.info("Opening session [" + i + "]: " + addresses[i].getHostName() + "@" + addresses[i].getPort());
					try {
						// Create an unbound socket.
						tempSocket = new Socket();
						// This method won't block more than
						// DEFAULT_CONNECTION_TIMEOUT milliseconds.
						logger.fine("Invoking socket's connect() method...");
						tempSocket.connect(addresses[i], DEFAULT_CONNECTION_TIMEOUT);
						logger.fine("Connected.");
						// Keep a reference to the socket.
						synchronized (socketLock) {
							this.socket = tempSocket;
						}
						// We're now connected, so exit the for loop.
						break;
					} catch (IOException e) {
						logger.warning("Connect exception: " + e.getMessage());
						if (i == (addresses.length - 1)) {
							// We've tried all possible addresses.
							throw new IOException("unable to connect to any of the supplied addresses.");
						}
					}
				}
			}
			// Note the socket is guaranteed to be non-null.
			logger.fine("Opening output stream...");
			// Get the output stream (using new
			// BufferedOutputStream(socket.getOutputStream()) hangs up!).
			oos = new ObjectOutputStream(socket.getOutputStream());
			// Flush the stream so that the serialization header is sent, since
			// the constructor writes the serialization stream header to the
			// underlying stream. If we don't do that, getting the input stream
			// may block.
			oos.flush();
			logger.fine("Output stream opened.");
			// Get the input stream. Wait maximum INITIAL_RECEIVE_TIMEOUT millis
			// to read the serialization stream header.
			this.socket.setSoTimeout(INITIAL_RECEIVE_TIMEOUT);
			logger.fine("Opening input stream...");
			ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream())) {
				/*
				 * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass )
				 */
				protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
					String name = desc.getName();
					try {
						return super.resolveClass(desc);
					} catch (ClassNotFoundException e) {
						Class<?> clazz = (Class<?>) Messenger.getRegisteredClass(name);
						if (clazz != null) {
							return clazz;
						} else {
							throw e;
						}
					}
				}
			};
			logger.fine("Input stream opened.");
			// Clear the timeout so further read() calls will block forever.
			this.socket.setSoTimeout(0);
			logger.info("Session opened: " + getRemoteHostName() + "@" + getRemotePort());
		} catch (IOException e) {
			logger.warning("Couldn't open the session: " + e.getMessage());
			// Close the streams, just in case.
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e1) {
				}
				oos = null;
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e1) {
				}
				ois = null;
			}
			// Start reconnecting and throw an exception.
			startConnector();
			throw new SocketSessionException(e);
		}

		// Stop reconnecting.
		stopConnector();

		// Start the writer.
		writer = new Writer(oos);
		writer.start();

		// Start the reader.
		reader = new Reader(ois);
		reader.start();

		// Start the connection tester thread.
		tester = new ConnectionTester();
		tester.start();

		// Store the last connection date.
		lastConnection = System.currentTimeMillis();

		// The session is now considered to be opened.
		closed = false;

		// Notify the controller.
		controller.sessionOpened(this);
	}

	/**
	 * Closes this session. This method does nothing if the session is already closed.
	 */
	public synchronized void close() {
		close(true);
	}

	/**
	 * Closes this session. This method does nothing if the session is already closed.
	 * 
	 * @param forced
	 *            indicates whether the session has been closed by explicitly calling <code>close()</code> from the outside.
	 */
	private synchronized void close(boolean forced) {
		if (this.closed) {
			// logger.info("Session " + getRemoteHostName() + "@" +
			// getRemotePort() + " was already closed.");
			return;
		}
		logger.info("Closing session " + getRemoteHostName() + "@" + getRemotePort() + "...");

		// Stop the tester thread.
		if (tester != null) {
			tester.interrupt();
			tester = null;
		}

		// Stop the reader thread.
		if (reader != null) {
			reader.started = false;
			reader.interrupt();
			reader = null;
		}

		// Stop the writer thread.
		if (writer != null) {
			writer.interrupt();
			writer = null;
		}

		// Close the output stream (guaranteed to be non-null).
		try {
			oos.close();
		} catch (IOException e) {
		}
		oos = null;
		// Close the input stream (guaranteed to be non-null).
		try {
			ois.close();
		} catch (IOException e) {
		}
		ois = null;

		// The session is now considered to be closed.
		closed = true;

		// Notify the controller.
		controller.sessionClosed(this, forced);

		// If the session hasn't been explicitly closed, start reconnecting.
		if (!forced) {
			startConnector();
		}
	}

	/**
	 * Sends a command.
	 * 
	 * @param command
	 *            the command to send.
	 * @throws SocketSessionException
	 *             if the session hasn't been opened yet, or the session is closed.
	 */
	public synchronized void send(Command command) throws SocketSessionException {
		if (closed) {
			throw new SocketSessionException("This session is closed.");
		}

		// Update message's sent date BEFORE sending it.
		if (command instanceof Message) {
			((Message) command).setTimeSent(new Date());
		}

		// Add the command to the sender.
		writer.add(command);

		// Notify the controller.
		controller.commandSent(this, command);
	}

	/**
	 * Adds a new property to the table.
	 * 
	 * @param key
	 *            the property key (<code>null</code> permitted).
	 * @param value
	 *            the property value (<code>null</code> permitted).
	 */
	public void putProperty(Object key, Object value) {
		properties.put(key, value);
	}

	/**
	 * Adds all properties stored into the map.
	 * 
	 * @param map
	 *            the properties to add.
	 */
	public void putAllProperties(Map<?, ?> map) {
		if (map != null) {
			properties.putAll(map);
		}
	}

	/**
	 * Returns the property stored with the specified key.
	 * 
	 * @param key
	 *            the property key (<code>null</code> permitted).
	 */
	public Object getProperty(Object key) {
		return properties.get(key);
	}

	/**
	 * Returns the properties map. No modifications should be made to the returned map.
	 * 
	 * @return the properties map.
	 */
	public Map<Object, Object> getProperties() {
		return properties;
	}

	/**
	 * Returns <code>true</code> if the properties map contains a property stored with the specified key.
	 * 
	 * @param key
	 *            the key to check.
	 * @return <code>true</code> if the properties map contains a property stored with the specified key, <code>false</code> otherwise.
	 */
	public boolean containsProperty(Object key) {
		return properties.containsKey(key);
	}

	/**
	 * Removes the specified property and returns its old value. If it does not exist, returns <code>null</code>.
	 * 
	 * @param key
	 *            the property key (<code>null</code> permitted).
	 */
	public Object removeProperty(Object key) {
		return properties.remove(key);
	}

	/**
	 * Returns the date when this session was successfully opened for the last time.
	 * 
	 * @return the date when this session was successfully opened for the last time.
	 */
	public long getLastConnection() {
		return lastConnection;
	}

	/**
	 * Returns the local address to which this session is bound, or <code>null</code> if this session is not bound yet.
	 * 
	 * @return the local address to which this session is bound.
	 */
	public String getLocalHostAddress() {
		synchronized (socketLock) {
			return (socket != null) ? socket.getLocalAddress().getHostAddress() : null;
		}
	}

	/**
	 * Gets the local host name to which this session is bound, or <code>null</code> if this session is not bound yet.
	 * 
	 * @return the local host name.
	 */
	public String getLocalHostName() {
		synchronized (socketLock) {
			return (socket != null) ? socket.getLocalAddress().getHostName() : null;
		}
	}

	/**
	 * Returns the local port to which this session is bound, or -1 if this session is not bound yet.
	 * 
	 * @return the local port to which this session is bound.
	 */
	public int getLocalPort() {
		synchronized (socketLock) {
			return (socket != null) ? socket.getLocalPort() : -1;
		}
	}

	/**
	 * Returns the remote address to which this session is connected, or <code>null</code> if the session is not connected.
	 * 
	 * @return the remote address to which this session is bound.
	 */
	public String getRemoteHostAddress() {
		synchronized (socketLock) {
			try {
				return socket.getInetAddress().getHostAddress();
			} catch (Exception e) {
			}
			return null;
		}
	}

	/**
	 * Returns the remote host name, or <code>null</code> if the session is not connected..
	 * 
	 * @return the remote host name.
	 */
	public String getRemoteHostName() {
		synchronized (socketLock) {
			try {
				return socket.getInetAddress().getHostName();
			} catch (Exception e) {
			}
			return null;
		}
	}

	/**
	 * Gets the remote port, or 0 if the session is not connected.
	 * 
	 * @return the remote port.
	 */
	public int getRemotePort() {
		synchronized (socketLock) {
			return (socket != null) ? socket.getPort() : 0;
		}
	}

	/**
	 * Returns the reconnection delay, in milliseconds.
	 * 
	 * @return the reconnection delay, in milliseconds.
	 */
	public int getReconnectionDelay() {
		return reconnectionDelay;
	}

	/**
	 * Sets the reconnection delay, a positive integer representing the number of milliseconds to wait between each failed connection
	 * attempt. The default value is 30 seconds. Setting this value to 0 or a negative integer turns off reconnection capability.
	 * 
	 * @param reconnectionDelay
	 *            the new reconnection delay.
	 */
	public void setReconnectionDelay(int reconnectionDelay) {
		this.reconnectionDelay = reconnectionDelay;
	}

	/**
	 * Starts the reconnecting thread.
	 */
	private void startConnector() {
		if ((connector == null) && (reconnectionDelay > 0)) {
			logger.info("Starting connector...");

			connector = new Connector();
			connector.setDaemon(true);
			connector.setPriority(Thread.MIN_PRIORITY);
			connector.start();
		}
	}

	/**
	 * Stops the reconnecting thread.
	 */
	private void stopConnector() {
		if (connector != null) {
			logger.info("Stopping connector...");

			connector.started = false;
			connector.interrupt();
			connector = null;
		}
	}

	/**
	 * The thread performing reconnections.
	 */
	private class Connector extends Thread {

		/**
		 * Control flag to let the thread work.
		 */
		boolean started = true;

		/**
		 * Creates a new connector thread.
		 */
		public Connector() {
			super("TMS@Connector thread@" + SocketSession.this.hashCode());
		}

		/*
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while (started) {
				try {
					// Sleep for a while.
					Thread.sleep(reconnectionDelay);
					// Try to open the session again.
					try {
						open();
					} catch (SocketSessionException e) {
						logger.warning("Couldn't open the session: " + e.getMessage());
					}
				} catch (Exception e) {
					// We've been interrupted.
					return;
				}
			}
		}

	}

	/**
	 * The reader thread.
	 */
	private class Reader extends Thread {

		/**
		 * Control flag to let the thread work.
		 */
		boolean started = true;

		/**
		 * The object input stream to read objects from.
		 */
		private ObjectInputStream ois;

		/**
		 * Creates a new reader.
		 * 
		 * @param ois
		 *            the object input stream to read objects from.
		 */
		public Reader(ObjectInputStream ois) {
			super("TMS@Reader@" + SocketSession.this.hashCode());
			this.ois = ois;
		}

		/*
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			logger.fine("Thread '" + Thread.currentThread().getName() + "' starts.");
			try {
				// Set the initial timeout.
				while (started) {
					// Get the message.
					Command command = (Command) ois.readObject();
					if (command instanceof Message) {
						((Message) command).setTimeReceived(new Date());
					}
					// Notify the controller.
					controller.commandReceived(SocketSession.this, command);
				}
			} catch (ClassNotFoundException e) {
				logger.severe("ClassNotFoundException caught: " + e.getMessage());
			} catch (InvalidClassException e) {
				logger.warning("InvalidClassException caught: " + e.getMessage());
			} catch (StreamCorruptedException e) {
				logger.warning("StreamCorruptedException caught: " + e.getMessage());
			} catch (OptionalDataException e) {
				logger.warning("OptionalDataException caught: " + e.getMessage());
			} catch (IOException e) {
				logger.warning("IOException caught: " + e.getMessage());
			}
			// Close the session, just in case.
			close(false);
			logger.fine("Thread '" + Thread.currentThread().getName() + "' dies.");
		}

	}

	/**
	 * The writer thread.
	 */
	private class Writer extends Thread {

		/**
		 * The list of messages.
		 */
		private List<Command> messages;

		/**
		 * The object output stream to write objects to.
		 */
		private ObjectOutputStream oos;

		/**
		 * Creates a new writer.
		 * 
		 * @param oos
		 *            the object output stream to write objects to.
		 */
		public Writer(ObjectOutputStream oos) {
			super("TMS@Writer@" + SocketSession.this.hashCode());
			this.messages = new Vector<Command>();
			this.oos = oos;
		}

		/**
		 * Adds a command to the list of commands that have to be sent.
		 * 
		 * @param command
		 *            the command to send.
		 */
		public void add(Command command) {
			messages.add(command);
			synchronized (messages) {
				messages.notify();
			}
		}

		/*
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			logger.fine("Thread '" + Thread.currentThread().getName() + "' starts.");

			try {
				while (true) {
					synchronized (messages) {
						while (messages.size() == 0) {
							messages.wait();
						}
					}
					// For sure there are messages to send
					Command command;
					while (messages.size() > 0) {
						command = (Command) messages.remove(0);
						// Send the message.
						// logger.finest("About to write object to socket...");
						oos.writeObject(command);
						// logger.finest("Object written.");
						oos.flush();
						// logger.finest("Object flushed.");
						// Reset the stream.
						if (++counter >= RESET_FREQUENCY) {
							counter = 0;
							oos.reset();
						}
					}
				}
			} catch (InterruptedException e) {
				logger.fine("Thread '" + Thread.currentThread().getName() + "' interrupted.");
			} catch (IOException e) {
				logger.warning("IOException caught: " + e);
			} catch (Exception e) {
				logger.warning("Exception caught: " + e);
			}
			// Close the session, just in case.
			close(false);
			logger.fine("Thread '" + Thread.currentThread().getName() + "' dies.");
		}

	}

	/**
	 * A thread used to test the connection. To test it, it sends an enquire link request every 5 seconds.
	 */
	private class ConnectionTester extends Thread {

		/**
		 * Creates a new connection tester thread.
		 */
		public ConnectionTester() {
			super("TMS@Connection tester@" + SocketSession.this.hashCode());
			setDaemon(true);
		}

		/*
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			logger.fine("Thread '" + Thread.currentThread().getName() + "' starts.");
			try {
				while (true) {
					// Sleep for a while before sending an enquire link command.
					Thread.sleep(DEFAULT_PING_DELAY);
					// Send the command.
					try {
						send(new PingRequest(clientId));
					} catch (SocketSessionException e) {
					}
				}
			} catch (InterruptedException e) {
			}
			logger.fine("Thread '" + Thread.currentThread().getName() + "' dies.");
		}

	}

}