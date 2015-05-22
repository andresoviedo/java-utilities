package org.andresoviedo.util.messaging.api1.client;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import org.andresoviedo.util.messaging.api1.Messenger;
import org.andresoviedo.util.messaging.api1.MessengerException;
import org.andresoviedo.util.messaging.api1.MessengerProperties;
import org.andresoviedo.util.messaging.api1.common.data.LoginRequest;
import org.andresoviedo.util.messaging.api1.common.data.Message;
import org.andresoviedo.util.messaging.api1.common.io.Persistence;
import org.andresoviedo.util.messaging.api1.common.net.SocketSession;
import org.andresoviedo.util.messaging.api1.common.net.SocketSessionException;

/**
 * The client messenger connects to a remote server and starts receiving and
 * sending messages.
 * 
 * @author andres
 */
public class ClientMessenger extends Messenger {

	/**
	 * A static reference to the logger object.
	 */
	private static Logger logger = Logger
			.getLogger(MessengerProperties.LOGGER_NAME);

	/**
	 * The single instance of this class.
	 */

	/**
	 * The socket session.
	 */
	private SocketSession session;

	/**
	 * The client messenger configuration.
	 */
	private ClientMessengerConfiguration configuration;

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the single instance of this class.
	 */
	public static ClientMessenger getInstance(String configFile) {
		ClientMessenger instance = null;
		synchronized (ClientMessenger.class) {
			if (instance == null) {
				instance = new ClientMessenger(configFile);
			}
		}
		return instance;
	}

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the single instance of this class.
	 */
	public static ClientMessenger getInstance() {
		return getInstance(null);
	}

	/**
	 * Creates a new client messenger.
	 * 
	 * @param configFile
	 */
	private ClientMessenger(String configFile) {
		super();
		// Creates the client configuration.
		this.configuration = new ClientMessengerConfiguration(configFile);

		super.setClientId(configuration.getClientId());
		// Create the socket session.
		this.session = new SocketSession(this.configuration.getClientId(),
				this.configuration.getAddresses(), this);
		logger.info("Server addresses: "
				+ Arrays.toString(this.configuration.getAddresses()));

		this.session.setReconnectionDelay(this.configuration
				.getReconnectionDelay());
		// Create the persistence object.
		this.persistence = new Persistence(
				this.configuration.getPersistenceDirectory());
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.Messenger#start()
	 */
	public void start() {
		super.start();
		logger.info("Starting client messenger ["
				+ this.configuration.getClientId() + "] ...");
		try {
			session.open();
		} catch (SocketSessionException e) {
			// There's no need to throw an exception because session manages
			// reconnections.
		}
		logger.info("Client messenger started.");
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.Messenger#stop()
	 */
	public void stop() {
		logger.info("Stopping client messenger...");
		session.close();
		super.stop();
		logger.info("Client messenger stopped.");
	}

	/*
	 * @see
	 * org.andresoviedo.util.messaging.api1.Messenger#sendImpl(org.andresoviedo
	 * .util.messaging.api1.common .data.Message)
	 */
	protected void sendImpl(Message message) throws MessengerException {
		super.send(message, session);
	}

	/*
	 * @see
	 * org.andresoviedo.util.messaging.api1.Messenger#sessionOpened(org.andresoviedo
	 * .util.messaging.api1.common .net.SocketSession)
	 */
	public void sessionOpened(SocketSession session) {
		super.sessionOpened(session);
		// The session has been opened, send a login command first.
		LoginRequest logon = new LoginRequest(this.configuration.getClientId());
		try {
			session.send(logon);
		} catch (SocketSessionException e) {
			logger.info(e.getMessage());
		}
	}

	/*
	 * @see
	 * org.andresoviedo.util.messaging.api1.Messenger#sendAndReceiveImpl(org
	 * .andresoviedo.util.messaging.api1 .common.data.Message, long)
	 */
	protected Message sendAndReceiveImpl(Message message, long timeout)
			throws MessengerException, InterruptedException {
		// The message is not persisted in case of an exception since the call
		// is synchronous.
		return super.sendAndReceive(message, session, timeout);
	}

	/*
	 * @see
	 * org.andresoviedo.util.messaging.api1.Messenger#getServicePersistenceDirectory
	 * ()
	 */
	protected File getServicePersistenceDirectory() {
		return this.configuration.getPersistenceDirectory();
	}

}