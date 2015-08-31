package org.andresoviedo.util.messaging.api1.server;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import org.andresoviedo.util.messaging.api1.Messenger;
import org.andresoviedo.util.messaging.api1.MessengerException;
import org.andresoviedo.util.messaging.api1.MessengerProperties;
import org.andresoviedo.util.messaging.api1.common.data.Message;
import org.andresoviedo.util.messaging.api1.common.io.Persistence;
import org.andresoviedo.util.messaging.api1.common.net.SocketSession;
import org.andresoviedo.util.messaging.api1.common.service.Service;

/**
 * The server messenger.
 * 
 * @author andresoviedo
 */
public class ServerMessenger extends Messenger {

	/**
	 * A static reference to the logger object.
	 */
	private static Logger logger = Logger.getLogger(MessengerProperties.LOGGER_NAME);

	/**
	 * The single instance of this class.
	 */
	private static ServerMessenger instance;

	/**
	 * The server session managing the server socket and the incoming connections.
	 */
	private ServerSession session;

	/**
	 * The server messenger configuration.
	 */
	private ServerMessengerConfiguration configuration;

	/**
	 * Delegated class to authenticate sessions
	 */
	private SessionAuthenticator sessionAuthenticator;

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the single instance of this class.
	 */
	public static ServerMessenger getInstance() {
		if (instance == null) {
			synchronized (ServerMessenger.class) {
				if (instance == null) {
					instance = new ServerMessenger();
				}
			}
		}
		return instance;
	}

	/**
	 * Creates a server messenger instance.
	 */
	private ServerMessenger() {
		super();
		// Creates the server configuration.
		this.configuration = new ServerMessengerConfiguration();

		setClientId(configuration.getServerId());
		// Create the server session.
		this.session = new ServerSession(this);
		// Create the persistence object.
		this.persistence = new Persistence(this.configuration.getPersistenceDirectory());

		logger.info("Persistence directory: " + this.configuration.getPersistenceDirectory());

		// Initialize services.
		this.initialize();
	}

	/**
	 * Initializes the messenger.
	 */
	private void initialize() {
		if (configuration.getServiceList().isEmpty()) {
			return;
		}

		// Variables needed for reflection.
		Class<?> clazz = null;
		@SuppressWarnings("rawtypes")
		Class[] types = { Messenger.class };
		Object[] args = { this };

		Service service = null;
		String serviceClassName = null;
		for (Iterator<?> it = configuration.getServiceList().iterator(); it.hasNext();) {
			serviceClassName = it.next().toString();
			try {
				// Try to get the class.
				clazz = Class.forName(serviceClassName);
				// Try to instantiate the service.
				service = (Service) clazz.getConstructor(types).newInstance(args);
				// Add it to the listener list.
				setServiceListener(service.getServiceId(), service);
			} catch (Exception e) {
				logger.warning("Exception caught instantiating the service: " + serviceClassName);
			}
		}
	}

	/**
	 * Returns messenger's configuaration object.
	 * 
	 * @return messenger's configuaration object.
	 */
	public ServerMessengerConfiguration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Returns the server session.
	 * 
	 * @return the server session.
	 */
	public ServerSession getServerSession() {
		return session;
	}

	/**
	 * Returns the session authenticator used to authenticate sessions. May be <code>null</code>.
	 * 
	 * @return the session authenticator used to authenticate sessions.
	 */
	public SessionAuthenticator getSessionAuthenticator() {
		return sessionAuthenticator;
	}

	/**
	 * Sets the session authenticator used to authenticate sessions. Can be <code>null</code>.
	 * 
	 * @param sessionAuthenticator
	 *            the session authenticator.
	 */
	public void setSessionAuthenticator(SessionAuthenticator sessionAuthenticator) {
		this.sessionAuthenticator = sessionAuthenticator;
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.Messenger#start()
	 */
	public void start() {
		logger.info("Starting server messenger...");
		try {
			session.open();
			logger.info("Server messenger started.");
		} catch (ServerSessionException e) {
			logger.warning("Exception caught while starting server messenger: " + e.getMessage());
		}
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.Messenger#stop()
	 */
	public void stop() {
		logger.info("Stopping server messenger...");
		session.close();
		super.stop();
		logger.info("Server messenger stopped.");
	}

	@Override
	protected void forwardMessage(Message message, String serviceId) throws MessengerException {
		if (message.getTargetClientId() == null || message.getTargetClientId().equals(clientId)) {
			super.forwardMessage(message, serviceId);
			return;
		}

		sendImpl(message);
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.Messenger#sendImpl(org.andresoviedo.util.messaging.api1.common .data.Message)
	 */
	protected void sendImpl(Message message) throws MessengerException {
		SocketSession clientSession = session.getSession(message.getTargetClientId());
		if (clientSession != null) {
			super.send(message, clientSession);
		} else {
			throw new MessengerException("No client available: " + message.getTargetClientId());
		}
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.Messenger#sendAndReceiveImpl(org.andresoviedo.util.messaging.api1 .common.data.Message,
	 * long)
	 */
	protected Message sendAndReceiveImpl(Message message, long timeout) throws MessengerException, InterruptedException {
		SocketSession clientSession = session.getSession(message.getTargetClientId());
		if (clientSession != null) {
			return super.sendAndReceive(message, clientSession, timeout);
		} else {
			throw new MessengerException("Client not available: " + message.getTargetClientId());
		}
	}

	/**
	 * Invoked from ServerSession when a session has been registered.
	 * 
	 * @param clientId
	 *            the client id.
	 */
	void resendPersistedMessages(String clientId) {
		// We're now connected, then send all persisted messages. Notice
		// messages are not removed from persistence.
		try {
			Message message = null;
			for (Iterator<?> it = persistence.iterator(clientId, false); it.hasNext();) {
				message = (Message) it.next();
				if (message != null) {
					// Send the message. This method will remove this message
					// from persistence if needed.
					send(message, false);
				}
			}
		} catch (MessengerException e) {
			logger.warning("MessengerException: " + e.getMessage());
		}
	}

	/**
	 * Invoked once an ACK has been received, remove the corresponding message from persistence to prevent resending.
	 * 
	 * @param clientId
	 *            the client id.
	 * @param messageId
	 *            the message id.
	 */
	void removePersistedMessage(String clientId, String messageId) {
		logger.fine("Removing message '" + messageId + "' with client id '" + clientId + "' from persistence...");
		if (!persistence.delete(clientId, messageId)) {
			logger.warning("Message '" + messageId + "' with client id '" + clientId + "' could not be deleted from persistence.");
		}
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.Messenger#getServicePersistenceDirectory()
	 */
	protected File getServicePersistenceDirectory() {
		return this.configuration.getPersistenceDirectory();
	}

}