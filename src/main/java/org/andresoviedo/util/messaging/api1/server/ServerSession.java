package org.andresoviedo.util.messaging.api1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.andresoviedo.util.messaging.api1.Messenger;
import org.andresoviedo.util.messaging.api1.MessengerException;
import org.andresoviedo.util.messaging.api1.MessengerProperties;
import org.andresoviedo.util.messaging.api1.common.data.Command;
import org.andresoviedo.util.messaging.api1.common.data.LoginRequest;
import org.andresoviedo.util.messaging.api1.common.data.LoginResponse;
import org.andresoviedo.util.messaging.api1.common.data.Message;
import org.andresoviedo.util.messaging.api1.common.data.SignalingCommand;
import org.andresoviedo.util.messaging.api1.common.net.SocketSession;
import org.andresoviedo.util.messaging.api1.common.net.SocketSessionController;
import org.andresoviedo.util.messaging.api1.common.net.SocketSessionException;


/**
 * Server session.
 * 
 * @author andres
 */
public class ServerSession implements Runnable, SocketSessionController {

	/**
	 * A static reference to the logger object.
	 */
	private static Logger logger = Logger
			.getLogger(MessengerProperties.LOGGER_NAME);

	/**
	 * The server socket.
	 */
	private ServerSocket serverSocket;

	/**
	 * The thread accepting incoming connections.
	 */
	private Thread serverThread;

	/**
	 * The map of client sessions (stored with the client id as the key). Only
	 * authenticated sessions are stored.
	 */
	private Map<Object, SocketSession> sessions;

	/**
	 * Indicates whether this session is opened or not.
	 */
	private boolean opened;

	/**
	 * Indicates whether the messenger should keep listening for incoming
	 * connections.
	 */
	private boolean listening;

	/**
	 * The server messsenger to forward messages to.
	 */
	private ServerMessenger messenger;

	/**
	 * Creates a new server session.
	 */
	ServerSession(ServerMessenger messenger) {
		this.messenger = messenger;
		this.sessions = new Hashtable<Object, SocketSession>();
	}

	/**
	 * Opens this session.
	 * 
	 * @throws ServerSessionException
	 *             if something goes wrong.
	 */
	void open() throws ServerSessionException {
		// Don't do anything if the session is already opened.
		if (opened) {
			return;
		}

		// Try to create the server socket.
		try {
			logger.info("Opening server socket at port: "
					+ messenger.getConfiguration().getPort());
			serverSocket = new ServerSocket(messenger.getConfiguration()
					.getPort());
		} catch (IOException e) {
			throw new ServerSessionException(
					"Exception caught while creating server socket.", e);
		}

		// Start a thread to accept incoming connections (update the status of
		// the connector).
		listening = true;
		serverThread = new Thread(this, "Server session thread");
		serverThread.start();

		// We now consider that this session is opened.
		opened = true;
	}

	/**
	 * Closes this session.
	 */
	void close() {
		// Don't do anything if the session is already closed.
		if (!opened) {
			return;
		}

		// Close the thread waiting for incoming connections.
		listening = false;
		// Close the server socket and set it to null.
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				logger.warning("Exception caught while closing the server socket: "
						+ e.getMessage());
			}
			serverSocket = null;
		}

		// if (serverThread != null) {
		// serverThread.interrupt();
		serverThread = null;
		// }

		// Close all sessions.
		closeAllSessions();

		// We now consider that this session is closed.
		opened = false;
	}

	/**
	 * Adds a new socket session to the table. Its
	 * <code>CLIENT_ID_PROPERTY</code> is used as the key.
	 * 
	 * @param session
	 *            the session to add.
	 */
	private void putSession(SocketSession session) {
		synchronized (sessions) {
			sessions.put(
					session.getProperty(Messenger.SESSION_CLIENT_ID_PROPERTY),
					session);
		}
	}

	/**
	 * Removes a socket session from the table. A first check ensuring that the
	 * session is in the table is performed.
	 * 
	 * @param session
	 *            the session to remove.
	 */
	private void removeSession(SocketSession session) {
		synchronized (sessions) {
			if (sessions.containsValue(session)) {
				sessions.remove(session
						.getProperty(Messenger.SESSION_CLIENT_ID_PROPERTY));
			}
		}
	}

	/**
	 * Returns the list of all socket sessions. A new list is created, so it can
	 * be manipulated as desired.
	 * 
	 * @return the list of all socket sessions.
	 */
	public List<SocketSession> getSessions() {
		synchronized (sessions) {
			return new Vector<SocketSession>(sessions.values());
		}
	}

	/**
	 * Gets the socket session stored with the specified key.
	 * 
	 * @param key
	 *            the session's key.
	 * @return the session with the specified key, or <code>null</code> if not
	 *         found.
	 */
	public SocketSession getSession(Object key) {
		if (key != null) {
			synchronized (sessions) {
				return (SocketSession) sessions.get(key);
			}
		}
		return null;
	}

	/**
	 * Returns the number of registered sessions.
	 * 
	 * @return the number of registered sessions.
	 */
	public int getSessionCount() {
		synchronized (sessions) {
			return sessions.size();
		}
	}

	/**
	 * Returns whether the session is registered or not.
	 * 
	 * @param session
	 *            the session to check.
	 * @return <code>true</code> if this session is registered,
	 *         <code>false</code> otherwise.
	 */
	public boolean isSessionRegistered(SocketSession session) {
		synchronized (sessions) {
			return sessions.containsValue(session);
		}
	}

	/**
	 * Closes all client sessions. This method is invoked when this server
	 * session is closed.
	 */
	private void closeAllSessions() {
		// Close each client session. We use a copy of the collection to avoid a
		// ConcurrentModificationException.
		for (Iterator<SocketSession> it = getSessions().iterator(); it.hasNext();) {
			((SocketSession) it.next()).close();
		}
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		logger.info(Thread.currentThread().getName() + " starts.");
		while (listening) {// !Thread.currentThread().isInterrupted()) {
			try {
				new ServerSessionClientThread(new SocketSession(messenger
						.getConfiguration().getServerId(),
						serverSocket.accept(), this)).start();
				// SocketSession session = new
				// SocketSession(serverSocket.accept(), this);
				// // No reconnection wanted.
				// session.setReconnectionDelay(0);
				// try {
				// session.open();
				// } catch (SocketSessionException e) {
				// // Close the session, just in case.
				// session.close();
				// session = null;
				// logger.warning("SocketSessionException caught: " +
				// e.getMessage());
				// }
			} catch (NullPointerException ex) {
				logger.info("Server thread finalized!");
			} catch (IOException e) {
				logger.warning("IOException caught: " + e.getMessage());
			}
		}
		logger.info(Thread.currentThread().getName() + " dies.");
	}

	/**
	 * Registers the specified session. If an old session was registered with
	 * the same client id, it will be closed.
	 * 
	 * @param session
	 *            the socket session.
	 * @param clientId
	 *            the client id.
	 */
	private void registerSession(SocketSession session, String clientId) {
		// Check whether the session is already in the table.
		SocketSession oldSession = getSession(clientId);
		logger.fine("Session object for clientId '" + clientId + "' is "
				+ oldSession + ".");
		if ((oldSession != null) && (oldSession != session)) {
			// A previous session stored with that client id exists, accept the
			// new one and close the old.
			logger.warning("A session with client id '" + clientId
					+ "' already exists. The old one will be closed.");
			// The session will be removed when sessionClosed() is invoked.
			oldSession.close();
			// removeSession(oldSession);
			oldSession = null;
		}
		// If 'oldSession' is null, register the session.
		if (oldSession == null) {
			// Store the client id property for later use and mark the session
			// as authenticated.
			session.putProperty(Messenger.SESSION_AUTHENTICATED_PROPERTY,
					Boolean.TRUE);
			session.putProperty(Messenger.SESSION_CLIENT_ID_PROPERTY, clientId);
			// Add the session to the table.
			putSession(session);
			// Resend persisted messages.
			messenger.resendPersistedMessages(clientId);
		}
		// Fire the event.
		messenger.fireSessionOpened(session);
	}

	/**
	 * Implements signaling logic. Only login is handled.
	 * 
	 * @param session
	 *            the socket session.
	 * @param command
	 *            the signaling command.
	 */
	private void signalingCommandReceived(SocketSession session,
			SignalingCommand command) throws SocketSessionException {
		logger.fine("Signaling command received: " + command.toString());
		if (command instanceof LoginRequest) {
			LoginRequest request = (LoginRequest) command;
			// Get the authenticator and validate the client id.
			SessionAuthenticator authenticator = messenger
					.getSessionAuthenticator();
			if ((authenticator == null)
					|| authenticator.authenticate(request.getClientId())) {
				// Login successful, send back the ack and register the session.
				logger.info("Successful logon attempt from clientId ["
						+ request.getClientId() + "]");
				session.send(new LoginResponse(request.getClientId(),
						LoginResponse.LOGIN_OK));
				registerSession(session, request.getClientId());
			} else {
				// Login failed, send back the ack and close the session.
				logger.info("Invalid logon attempt from clientId ["
						+ request.getClientId() + "]. Session will be closed.");
				session.send(new LoginResponse(request.getClientId(),
						LoginResponse.LOGIN_NOT_OK));
				session.close();
			}
		} else {
			// Any other signaling commands are processed at messenger level.
			messenger.commandReceived(session, command);
		}
	}

	/*
	 * @see
	 * org.andresoviedo.util.messaging.api1.common.net.SocketSessionController#commandReceived
	 * (org.andresoviedo.util.messaging.api1.common.net.SocketSession,
	 * org.andresoviedo.util.messaging.api1.common.data.Command)
	 */
	public void commandReceived(SocketSession session, Command command) {
		if (command instanceof SignalingCommand) {
			try {
				signalingCommandReceived(session, (SignalingCommand) command);
			} catch (SocketSessionException e) {
				logger.warning("SocketSessionException at signalingCommandReceived ["
						+ e.getMessage() + "]");
			}
		} else if (command instanceof Message) {
			// Check whether the session is registered or not.
			if (!isSessionRegistered(session)) {
				// That shouldn't happen at all...
				logger.warning("Message received from an unregistered session, closing it...");
				session.close();
				return;
			}

			Message message = (Message) command;
			// That shouldn't happen if client messaging system is used.
			try {
				messenger.validate(message);
			} catch (MessengerException e) {
				logger.warning("Cannot process the message: " + e.getMessage());
				return;
			}

			// TODO: implement bridge feature.
			// Forward the event to the messenger.
			messenger.commandReceived(session, message);
		}
	}

	/*
	 * @see
	 * org.andresoviedo.util.messaging.api1.common.net.SocketSessionController#commandSent(
	 * org.andresoviedo.util.messaging.api1.common.net.SocketSession,
	 * org.andresoviedo.util.messaging.api1.common.data.Command)
	 */
	public void commandSent(SocketSession session, Command message) {
		// Forward the event to the messenger.
		messenger.commandSent(session, message);
	}

	/*
	 * @see
	 * org.andresoviedo.util.messaging.api1.common.net.SocketSessionController#sessionClosed
	 * (org.andresoviedo.util.messaging.api1.common.net.SocketSession, boolean)
	 */
	public void sessionClosed(SocketSession session, boolean forced) {
		// WARNING! We may receive a "session closed" event from a session that
		// hasn't been registered.
		removeSession(session);
		// Forward the event to the messenger.
		messenger.sessionClosed(session, forced);
	}

	/*
	 * @see
	 * org.andresoviedo.util.messaging.api1.common.net.SocketSessionController#sessionOpened
	 * (org.andresoviedo.util.messaging.api1.common.net.SocketSession)
	 */
	public void sessionOpened(SocketSession session) {
		// Forward the event to the messenger.
		messenger.sessionOpened(session);
	}

	/**
	 * A thread to handle incoming connection requests.
	 */
	private class ServerSessionClientThread extends Thread {

		private SocketSession session;

		public ServerSessionClientThread(SocketSession session) {
			super("Server session client thread");
			this.session = session;
			// No reconnection wanted.
			this.session.setReconnectionDelay(0);
		}

		/*
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {
				session.open();
			} catch (SocketSessionException e) {
				logger.warning("SocketSessionException caught: "
						+ e.getMessage());
				// Close the session, just in case.
				session.close();
				session = null;
			}
		}

	}

}