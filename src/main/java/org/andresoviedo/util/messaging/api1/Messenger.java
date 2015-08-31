package org.andresoviedo.util.messaging.api1;

import java.io.File;
import java.util.Date;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import org.andresoviedo.util.messaging.api1.common.data.Command;
import org.andresoviedo.util.messaging.api1.common.data.LoginResponse;
import org.andresoviedo.util.messaging.api1.common.data.Message;
import org.andresoviedo.util.messaging.api1.common.data.MessageAck;
import org.andresoviedo.util.messaging.api1.common.data.MessageFactory;
import org.andresoviedo.util.messaging.api1.common.data.PingRequest;
import org.andresoviedo.util.messaging.api1.common.data.PingResponse;
import org.andresoviedo.util.messaging.api1.common.data.SignalingCommand;
import org.andresoviedo.util.messaging.api1.common.io.Persistence;
import org.andresoviedo.util.messaging.api1.common.io.PersistenceException;
import org.andresoviedo.util.messaging.api1.common.net.SocketSession;
import org.andresoviedo.util.messaging.api1.common.net.SocketSessionController;
import org.andresoviedo.util.messaging.api1.common.net.SocketSessionException;
import org.andresoviedo.util.messaging.api1.common.service.ServiceListener;
import org.andresoviedo.util.messaging.api1.common.service.ServiceListenerList;

/**
 * Abstract messenger.
 * 
 * @author andresoviedo
 */
public abstract class Messenger implements SocketSessionController {

	/**
	 * Authenticated sessions will contain this property.
	 */
	public static final String SESSION_AUTHENTICATED_PROPERTY = "authenticated";

	/**
	 * Sessions client id property name.
	 */
	public static final String SESSION_CLIENT_ID_PROPERTY = "client-id";

	/**
	 * A static reference to the logger object.
	 */
	private static Logger logger = Logger.getLogger(MessengerProperties.LOGGER_NAME);

	/**
	 * The service listener list.
	 */
	protected ServiceListenerList listenerList;

	/**
	 * The map holding lock objects used in synchronous calls.
	 */
	protected Map<String, MessageLock> locks;

	/**
	 * The map holding <code>Persistence</code> objects, one for each registered service.
	 */
	protected Map<String, Persistence> servicePersistences;

	/**
	 * The persistence object to the storage and retrieval of messages.
	 */
	protected Persistence persistence;

	/**
	 * The list of messenger listeners.
	 */
	protected EventListenerList messengerListenerList;

	/**
	 * A static map with registered classes (needed to access some classes when using different class loaders).
	 */
	private static Map<String, Class<?>> registeredClasses = new Hashtable<String, Class<?>>();

	/**
	 * Maximum number of service parallel threads invoking the listeners processMessage method.
	 */
	private int maxParallelThreads = MessengerProperties.getMaxParallelThreads();

	/**
	 * This flag is set to true when all ServiceThreads should pause it's execution
	 */
	private boolean serviceThreadsPaused = false;

	/**
	 * Number of threads currently executing
	 */
	private int serviceThreadsExecuting = 0;

	/**
	 * Services thread pool to forward messages
	 */
	private Map<String, List<ServiceThread>> serviceThreads = new Hashtable<String, List<ServiceThread>>();

	/**
	 * A list of SessionMessages received by all SocketSessions ready to be processed by service threads
	 */
	private Map<String, List<SessionMessage>> serviceMsgs = new Hashtable<String, List<SessionMessage>>();

	/**
	 * clientId
	 */
	protected String clientId;

	/**
	 * Protected constructor.
	 */
	protected Messenger() {
		// Create the listener list.
		this.listenerList = new ServiceListenerList();
		// Create the locks table.
		this.locks = new Hashtable<String, MessageLock>();
		// Create persistence objects table.
		this.servicePersistences = new Hashtable<String, Persistence>();
		// Create the list of messenger listeners.
		this.messengerListenerList = new EventListenerList();
	}

	protected void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public MessageFactory createMessageFactory() {
		return MessageFactory.getInstance(clientId);
	}

	/**
	 * Adds a messenger listener to the list of listeners.
	 * 
	 * @param l
	 *            the listener to add.
	 * @since 2.0.10
	 */
	public void addMessengerListener(MessengerListener l) {
		messengerListenerList.add(MessengerListener.class, l);
	}

	/**
	 * Removes a messenger listener from the list of listeners.
	 * 
	 * @param l
	 *            the listener to remove.
	 * @since 2.0.10
	 */
	public void removeMessengerListener(MessengerListener l) {
		messengerListenerList.remove(MessengerListener.class, l);
	}

	/**
	 * Starts this messenger.
	 */
	public void start() {
	}

	/**
	 * Stops this messenger.
	 */
	public void stop() {
		// Release all locks.
		Map<String, MessageLock> copy = new Hashtable<String, MessageLock>(locks);
		MessageLock lock = null;
		for (Iterator<MessageLock> it = copy.values().iterator(); it.hasNext();) {
			lock = (MessageLock) it.next();
			synchronized (lock) {
				lock.notify();
			}
		}
		for (List<ServiceThread> l : serviceThreads.values()) {
			for (ServiceThread st : l) {
				st.interrupt();
			}
		}
		for (List<ServiceThread> l : serviceThreads.values()) {
			for (ServiceThread st : l) {
				try {
					st.join(1000);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	/**
	 * Sends the specified message.
	 * 
	 * @param message
	 *            the message to send.
	 * @throws MessengerException
	 *             if the message to be sent is not a valid message or an error occurs while sending the message.
	 */
	public void send(Message message) throws MessengerException {
		// Notice the message will only be persisted if needed.
		send(message, (message.isNeedsAck() || (message.getTimeout() >= 0)));
	}

	/**
	 * Sends the specified message.
	 * 
	 * @param message
	 *            the message to send.
	 * @param persist
	 *            indicates whether the message has to be persisted or not.
	 * @throws MessengerException
	 *             if the message to be sent is not a valid message or an error occurs while sending the message.
	 */
	protected void send(Message message, boolean persist) throws MessengerException {
		// First of all, validate the message.
		validate(message);
		// Although the socket session will set message's sent date, set it
		// there just in case an exception is thrown.
		message.setTimeSent(new Date());
		// Add persistence if we're asked to do so.
		if (persist) {
			try {
				logger.fine("Adding message with id '" + message.getMessageId() + "' to the underlying persistence mechanism...");
				persistence.add(message);
			} catch (PersistenceException e) {
				logger.severe("Error persisting message with id '" + message.getMessageId() + "': " + e.getMessage());
				logger.throwing(getClass().getName(), "send", e);
			}
		}
		// Invoke the implementation method (throws an exception).
		sendImpl(message);
		// Try ALWAYS to delete the message from persistence if the message does
		// not need ACK, since it has been successfully sent.
		if (!message.isNeedsAck()) {
			if (!persistence.delete(message)) {
				logger.warning("Message with id '" + message.getMessageId() + "' could not be deleted.");
			}
		}
	}

	/**
	 * The method inherited classes have to implement to send the message.
	 * 
	 * @param message
	 *            the message to send.
	 * @throws MessengerException
	 *             if an error occurs while sending the message.
	 */
	protected abstract void sendImpl(Message message) throws MessengerException;

	/**
	 * Sends the specified message through the specified socket session.
	 * 
	 * @param message
	 *            the message to send.
	 * @param session
	 *            the session to send the message through.
	 * @throws MessengerException
	 *             if an error occurs while sending the message.
	 */
	protected void send(Message message, SocketSession session) throws MessengerException {
		// Check whether this session is authenticated or not.
		if (!session.containsProperty(SESSION_AUTHENTICATED_PROPERTY)) {
			throw new MessengerException("Session is not authenticated. Cannot send message with id '" + message.getMessageId() + "'.");
		}
		try {
			session.send(message);
		} catch (SocketSessionException e) {
			logger.warning("Exception caught while sending the message: " + e.getMessage());
			throw new MessengerException("Exception caught while sending the message.", e);
		}
	}

	/**
	 * Sends a message and waits for a response to be received. May return <code>null</code> if the message cannot be sent.
	 * 
	 * @param message
	 *            the message to send.
	 * @return the response message.
	 * @throws MessengerException
	 *             if an error occurs while performing the operation.
	 * @throws InterruptedException
	 *             if the current thread is interrupted waiting for a response.
	 */
	public Message sendAndReceive(Message message) throws MessengerException, InterruptedException {
		return sendAndReceive(message, 0);
	}

	/**
	 * Sends a message and waits for a response to be received. May return <code>null</code> if the message cannot be sent.
	 * 
	 * @param message
	 *            the message to send.
	 * @param timeout
	 *            the maximum number of milliseconds to wait until a message is received. Zero means infinite timeout.
	 * @return the response message.
	 * @throws IllegalArgumentException
	 *             if timeout is less than zero.
	 * @throws MessengerException
	 *             if an error occurs while performing the operation.
	 * @throws InterruptedException
	 *             if the current thread is interrupted waiting for a response.
	 */
	public Message sendAndReceive(Message message, long timeout) throws MessengerException, InterruptedException {
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout less than zero.");
		}
		// First of all, validate the message.
		validate(message);
		// Although the socket session will set message's sent date, set it
		// there just in case an exception is thrown.
		message.setTimeSent(new Date());
		// Invoke the implementation method.
		return sendAndReceiveImpl(message, timeout);
	}

	/**
	 * Sends a message and waits for a response to be received. May return <code>null</code> if the message cannot be sent.
	 * 
	 * @param message
	 *            the message to send.
	 * @param timeout
	 *            the maximum number of milliseconds to wait until a message is received.
	 * @return the response message.
	 * @throws MessengerException
	 *             if an error occurs while performing the operation.
	 * @throws InterruptedException
	 *             if the current thread is interrupted waiting for a response.
	 */
	protected abstract Message sendAndReceiveImpl(Message message, long timeout) throws MessengerException, InterruptedException;

	/**
	 * Sends a message and waits for a response to be received. May return <code>null</code> if the message cannot be sent.
	 * 
	 * @param message
	 *            the message to send.
	 * @param session
	 *            the session to send the message through.
	 * @param timeout
	 *            the maximum number of milliseconds to wait until a message is received.
	 * @return the response message.
	 * @throws MessengerException
	 *             if an error occurs while performing the operation.
	 * @throws InterruptedException
	 *             if the current thread is interrupted waiting for a response.
	 */
	protected Message sendAndReceive(Message message, SocketSession session, long timeout) throws MessengerException, InterruptedException {
		// Check whether this session is authenticated or not.
		if (!session.containsProperty(SESSION_AUTHENTICATED_PROPERTY)) {
			throw new MessengerException("Session is not authenticated. Cannot send message with id '" + message.getMessageId() + "'.");
		}
		// Wait for the message response with a lock object.
		MessageLock lock = new MessageLock();
		locks.put(message.getMessageId(), lock);
		synchronized (lock) {
			try {
				// Send the message.
				session.send(message);
				// Wait until a message is received.
				lock.wait(timeout);
				// We've been notified, so return the response message.
				return lock.result;
			} catch (SocketSessionException e) {
				logger.warning("Exception caught while sending the message: " + e.getMessage());
				throw new MessengerException("Exception caught while sending the message.", e);
			} finally {
				// Ensure the lock is always removed from table.
				locks.remove(message.getMessageId());
			}
		}
	}

	/**
	 * Validates the message.
	 * 
	 * @param message
	 *            the message to validate.
	 * @throws MessengerException
	 *             if the message is <code>null</code> or considered to be invalid.
	 */
	public void validate(Message message) throws MessengerException {
		if (message == null) {
			throw new MessengerException("Invalid message: null.");
		}
		if ((message.getClientId() == null) || (message.getClientId().length() == 0)) {
			throw new MessengerException("Invalid message: no client id found.");
		}
		if ((message.getMessageId() == null) || (message.getMessageId().length() == 0)) {
			throw new MessengerException("Invalid message: no message id found.");
		}
		if ((message.getServiceId() == null) || (message.getServiceId().length() == 0)) {
			throw new MessengerException("Invalid message: no service id found.");
		}
		if ((message.getTargetServiceId() == null) || (message.getTargetServiceId().length() == 0)) {
			throw new MessengerException("Invalid message: no target service id found.");
		}
	}

	/**
	 * Associates a secondary service id to a primary service id.
	 * 
	 * @param primaryServiceId
	 *            the primary service id.
	 * @param secondaryServiceId
	 *            the secondary service id.
	 * @since 2.0.8
	 */
	public void addSecondaryService(String primaryServiceId, String secondaryServiceId) {
		try {
			listenerList.addSecondaryService(primaryServiceId, secondaryServiceId);
			// Get the persistence object.
			Persistence persistence = getServicePersistence(secondaryServiceId);
			// Get persisted messages and forward them. Notice messages are not
			// deleted.
			Message message = null;
			for (Iterator<?> it = persistence.iterator(false); it.hasNext();) {
				message = (Message) it.next();
				if (message != null) {
					// Forward the message.
					forwardMessage(message, secondaryServiceId);
					// Delete the message, now that we're sure it has been
					// successfully processed.
					if (!persistence.delete(message)) {
						logger.warning("Message with id '" + message.getMessageId() + "' could not be deleted.");
					}
				}
			}
		} catch (MessengerException e) {
			// That should never happen.
			logger.severe("Exception caught while forwarding the message: " + e.getMessage());
			logger.throwing(getClass().getName(), "addSecondaryService", e.getCause());
		}
	}

	/**
	 * Associates the specified listener to a service id. This method does nothing if either <code>serviceId</code> or <code>l</code> are
	 * <code>null</code>. Persisted messages will be forwarded to the listener.
	 * 
	 * @param serviceId
	 *            the service id.
	 * @param l
	 *            the listener to be added.
	 */
	public void setServiceListener(String serviceId, ServiceListener l) {
		synchronized (ServiceThread.class) {
			serviceThreadsPaused = true;
			try {
				while (serviceThreadsExecuting > 0) {
					ServiceThread.class.wait();
				}
				listenerList.setServiceListener(serviceId, l);
			} catch (InterruptedException ex) {
				logger.warning("Thread interrupted while waiting for threads to finish.");
				return;
			} finally {
				serviceThreadsPaused = false;
				ServiceThread.class.notifyAll();
			}
		}

		try {
			// Get the persistence object.
			Persistence persistence = getServicePersistence(serviceId);
			// Get persisted messages and forward them. Notice messages are not
			// deleted.
			Message message = null;
			for (Iterator<?> it = persistence.iterator(false); it.hasNext();) {
				message = (Message) it.next();
				if (message != null) {
					// Forward the message.
					forwardMessage(message, serviceId);
					// Delete the message, now that we're sure it has been
					// successfully processed.
					if (!persistence.delete(message)) {
						logger.warning("Message with id '" + message.getMessageId() + "' could not be deleted.");
					}
				}
			}
		} catch (MessengerException e) {
			// That should never happen.
			logger.severe("Exception caught while forwarding the message: " + e.getMessage());
			logger.throwing(getClass().getName(), "setServiceListener", e.getCause());
		}
	}

	/**
	 * Removes the listener associated to the specified service id. This method does nothing if <code>serviceId</code> is null.
	 * 
	 * @param serviceId
	 *            the service id.
	 */
	public void removeServiceListener(String serviceId) {
		synchronized (ServiceThread.class) {
			serviceThreadsPaused = true;
			try {
				while (serviceThreadsExecuting > 0) {
					ServiceThread.class.wait();
				}
				this.listenerList.removeServiceListener(serviceId);
			} catch (InterruptedException ex) {
				logger.warning("Thread interrupted while waiting for threads to finish.");
			} finally {
				serviceThreadsPaused = false;
				ServiceThread.class.notifyAll();
			}
		}
	}

	public void commandReceived(SocketSession session, Command command) {
		logger.fine("Command received: " + command);
		if (command instanceof SignalingCommand) {
			// Common signaling protocol handling for both client and server.
			signalingCommandReceived(session, (SignalingCommand) command);
		} else {
			Message message = (Message) command;
			String targetServiceId = message.getTargetServiceId().toLowerCase();

			List<SessionMessage> smsgsList = serviceMsgs.get(targetServiceId);
			// get service message list like this for getting optimized
			// performance
			if (smsgsList == null) {
				synchronized (serviceMsgs) {
					smsgsList = serviceMsgs.get(targetServiceId);
					if (smsgsList == null) {
						List<ServiceThread> serviceThreadsList = new Vector<ServiceThread>(maxParallelThreads);
						serviceMsgs.put(targetServiceId, smsgsList = new Vector<SessionMessage>());
						serviceThreads.put(targetServiceId, serviceThreadsList);
						for (int i = 0; i < maxParallelThreads; i++) {
							ServiceThread st = new ServiceThread(smsgsList, targetServiceId + "_" + i);
							serviceThreadsList.add(st);
							st.start();
						}
					}
				}
			}
			SessionMessage smsg = new SessionMessage(session, message);
			synchronized (smsgsList) {
				smsgsList.add(smsg);
				smsgsList.notify();
			}
		}
	}

	/*
	 * @see org.andresoviedo.util.messaging.api1.common.net.SocketSessionController#commandReceived
	 * (org.andresoviedo.util.messaging.api1.common.net.SocketSession, org.andresoviedo.util.messaging.api1.common.data.Command)
	 */
	public void messageReceived(SocketSession session, Message message) {
		// This flag will let us know if an ACK has to be sent.
		boolean sendAck = message.isNeedsAck();
		try {
			if (message.getCorrelationMessageId() == null) {
				forwardMessage(message, message.getTargetServiceId());
			} else {
				MessageLock lock = (MessageLock) locks.get(message.getCorrelationMessageId());
				if (lock != null) {
					synchronized (lock) {
						lock.result = message;
						lock.notify();
					}
				} else {
					forwardMessage(message, message.getTargetServiceId());
				}
			}
			// At this point, we know the message has been successfully
			// forwarded. The ACK will be sent.
		} catch (MessengerException e) {
			logger.info("Exception caught while forwarding the message: " + e.getMessage());
			// Persist the message.
			try {
				addMessageToServicePersistence(message, message.getTargetServiceId());
			} catch (PersistenceException e1) {
				logger.severe("Exception caught while persisting the message: " + e1.getMessage());
				logger.throwing(getClass().getName(), "commandReceived", e1);
				// The message has not been persisted, so don't send the ACK to
				// let the client resend it.
				sendAck = false;
			}
		}
		// Send the ACK if needed.
		if (sendAck) {
			logger.fine("Message received with id '" + message.getMessageId() + "', sending acknowledgement...");
			try {
				session.send(new MessageAck(message.getClientId(), message.getMessageId()));
				// Forward the message to secondary listeners if and only if
				// everything is OK.
				forwardMessageToSecondaryServices(message, message.getTargetServiceId());
			} catch (SocketSessionException e) {
				logger.warning("SocketSessionException caught: " + e.getMessage());
			}
		}
	}

	/**
	 * Common signaling handling for client and server is implemented here.
	 * 
	 * @param session
	 *            the socket session.
	 * @param command
	 *            the signalling command.
	 */
	private void signalingCommandReceived(SocketSession session, SignalingCommand command) {
		if (command instanceof LoginResponse) {
			// Notice that the server will never enter here.
			LoginResponse response = (LoginResponse) command;
			if (response.getResult() == LoginResponse.LOGIN_OK) {
				// Set the session as authenticated.
				session.putProperty(SESSION_AUTHENTICATED_PROPERTY, Boolean.TRUE);
				// We're now connected, then send all persisted messages. Notice
				// the messages are not removed from persistence.
				logger.fine("Login OK, getting messages from persistence...");
				Message message = null;
				for (Iterator<?> it = persistence.iterator(clientId, false); it.hasNext();) {
					message = (Message) it.next();
					if (message != null) {
						logger.fine("Sending persisted message with id '" + message.getMessageId() + "'...");
						// This method will remove this message from persistence
						// if needed.
						try {
							send(message, false);
						} catch (MessengerException e) {
							logger.warning("MessengerException caught: " + e.getMessage());
						}
					}
				}
				// Fire the event.
				fireSessionOpened(session);
			}
		} else if (command instanceof MessageAck) {
			MessageAck ack = (MessageAck) command;
			logger.fine("Message with id '" + ack.getMessageId() + "' acknowledged, deleting it from persistence...");
			if (!persistence.delete(ack.getClientId(), ack.getMessageId())) {
				logger.warning("Message with id '" + ack.getMessageId() + "' could not be deleted.");
			}
		} else if (command instanceof PingRequest) {
			PingRequest request = (PingRequest) command;
			try {
				session.send(new PingResponse(request.getClientId()));
			} catch (SocketSessionException e) {
				logger.warning("SocketSessionException caught: " + e.getMessage());
			}
		}
	}

	/*
	 * @see ttm.platform.services.messaging.common.net.SocketSessionController#
	 * messageSent(ttm.platform.services.messaging.common.net.SocketSession , ttm.platform.services.messaging.common.data.Message)
	 */
	public void commandSent(SocketSession session, Command command) {
		logger.fine("Command sent: " + command);
	}

	/*
	 * @see ttm.platform.services.messaging.common.net.SocketSessionController#
	 * sessionClosed(ttm.platform.services.messaging.common.net.SocketSession , boolean)
	 */
	public void sessionClosed(SocketSession session, boolean forced) {
		logger.fine("Session closed: " + session.getRemoteHostName());
		// Session is no longer authenticated. Notice that the event is fired if
		// the session was authenticated.
		if (session.containsProperty(SESSION_AUTHENTICATED_PROPERTY)) {
			session.removeProperty(SESSION_AUTHENTICATED_PROPERTY);
			fireSessionClosed(session, forced);
		}
	}

	/*
	 * @see ttm.platform.services.messaging.common.net.SocketSessionController#
	 * sessionOpened(ttm.platform.services.messaging.common.net.SocketSession )
	 */
	public void sessionOpened(SocketSession session) {
		logger.fine("Session opened: " + session.getRemoteHostName());
		// Notice that we don't fire a session opened event since the session is
		// not yet authenticated.
	}

	/**
	 * Fires a session closed event to all registered listeners.
	 * 
	 * @param session
	 *            the session that has been closed.
	 * @param forced
	 *            <code>true</code> if the session has been forced to be closed using its <code>close()</code> method.
	 * @since 2.0.10
	 */
	public void fireSessionClosed(SocketSession session, boolean forced) {
		EventListener[] listeners = messengerListenerList.getListeners(MessengerListener.class);
		for (int i = listeners.length - 1; i >= 0; i -= 1) {
			((MessengerListener) listeners[i]).sessionClosed(session, forced);
		}
	}

	/**
	 * Fires a session opened event to all registered listeners.
	 * 
	 * @param session
	 *            the session that has been opened.
	 * @since 2.0.10
	 */
	public void fireSessionOpened(SocketSession session) {
		EventListener[] listeners = messengerListenerList.getListeners(MessengerListener.class);
		for (int i = listeners.length - 1; i >= 0; i -= 1) {
			((MessengerListener) listeners[i]).sessionOpened(session);
		}
	}

	/**
	 * Forwards a message to the listener registered to the specified service. If no listener is registered, the message is persisted.
	 * 
	 * @param message
	 *            the message to forward.
	 * @param serviceId
	 *            the service id.
	 * @throws MessengerException
	 *             if no service listener is available or an uncaught exception is thrown by the service.
	 */
	protected void forwardMessage(Message message, String serviceId) throws MessengerException {
		try {
			if (!listenerList.forwardMessage(message, serviceId)) {
				// No listener was found, so the message has to be persisted.
				throw new MessengerException("No service with id '" + serviceId + "' is available.");
			}
		} catch (MessengerException e) {
			throw e;
		} catch (InterruptedException ex) {
			logger.warning("Thread interrupted while getting lock");
			throw new MessengerException(ex);
		} catch (Exception e) {
			// Uncaught exception in service. The message should be persisted
			// too.
			logger.severe("Exception caught while processing message: " + e.getMessage());
			logger.throwing(getClass().getName(), "forwardMessage", e);
			throw new MessengerException(e);
		}
	}

	/**
	 * Forwards the specified message to services associated to a primary service (the so-called secondary services).
	 * 
	 * @param message
	 *            the message.
	 * @param serviceId
	 *            the primary service id.
	 * @since 2.0.8
	 */
	protected void forwardMessageToSecondaryServices(Message message, String serviceId) {
		Set<?> secondaryServiceIds = listenerList.getSecondaryServiceIds(serviceId);
		if (secondaryServiceIds == null) {
			return;
		}
		String secondaryServiceId = null;
		ServiceListener listener = null;
		for (Iterator<?> it = secondaryServiceIds.iterator(); it.hasNext();) {
			secondaryServiceId = it.next().toString();
			listener = listenerList.getServiceListener(secondaryServiceId);
			try {
				listener.processMessage(message);
			} catch (Exception e) {
				if (listener != null) {
					// Uncaught exception in service. The message should be
					// persisted.
					logger.severe("Exception caught while processing message: " + e.getMessage());
					logger.throwing(getClass().getName(), "forwardMessageToSecondaryServices", e);
				}
				try {
					addMessageToServicePersistence(message, secondaryServiceId);
				} catch (PersistenceException e1) {
					logger.severe("Exception caught while persisting the message: " + e1.getMessage());
				}
			}
		}
	}

	/**
	 * @return the maximum number of parallel Reader threads that can run at same time.
	 */
	public int getMaxParallelThreads() {
		return maxParallelThreads * serviceThreads.size();
	}

	/**
	 * @return the number of Reader threads that are currently owning a lock.
	 */
	public int currentThreadsRunning() {
		return serviceThreadsExecuting;
	}

	/**
	 * @return the number of Reader threads that are currently owning a lock.
	 */
	public int currentMessagesPending() {
		int ret = 0;
		synchronized (serviceMsgs) {
			for (List<SessionMessage> sml : serviceMsgs.values()) {
				ret += sml.size();
			}
		}
		return ret;
	}

	/**
	 * Adds the specified message to the persistence object associated to the given service id. Only messages with a timeout greater or
	 * equal to 0 are added.
	 * 
	 * @param message
	 *            the message to add.
	 * @param serviceId
	 *            the service id.
	 * @throws PersistenceException
	 *             if an error occurs while trying to persist the message.
	 */
	protected void addMessageToServicePersistence(Message message, String serviceId) throws PersistenceException {
		if (message.getTimeout() >= 0) {
			Persistence persistence = getServicePersistence(serviceId);
			// Notice the persistence object is guaranteed to be non-null.
			persistence.add(message);
		}
	}

	/**
	 * Returns the service persistence root directory. The path of the directory where persisted messages for a particular service id will
	 * be stored to and read from is constructed by concatenating the service id. For instance, if this directory points to "C:\messages\",
	 * messages associated to the service id "service-id1" will be read from "C:\messages\service-id1\".
	 */
	protected abstract File getServicePersistenceDirectory();

	/**
	 * Returns the persistence object that will be used to store to and read from messages associated to the specified service id.
	 * 
	 * @param serviceId
	 *            the service id.
	 * @return the persistence object that will be used to store to and read from messages associated to the specified service id.
	 */
	protected Persistence getServicePersistence(String serviceId) {
		Persistence persistence = (Persistence) servicePersistences.get(serviceId);
		if (persistence == null) {
			servicePersistences.put(serviceId, persistence = new Persistence(new File(getServicePersistenceDirectory(), serviceId)));
		}
		return persistence;
	}

	/**
	 * Registers the specified class.
	 * 
	 * @param clazz
	 *            the class to register.
	 * @throws IllegalArgumentException
	 *             if <code>clazz</code> is <code>null</code>.
	 */
	public static void registerClass(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("The class cannot be null.");
		}
		registeredClasses.put(clazz.getName(), clazz);
	}

	/**
	 * Returns the registered class with the specified name.
	 * 
	 * @param className
	 *            the class name.
	 * @return the registered class with the specified name, or <code>null</code> if not found.
	 */
	public static Class<?> getRegisteredClass(String className) {
		return (Class<?>) registeredClasses.get(className);
	}

	/**
	 * A simple object holding a message used for synchronous calls.
	 */
	protected static class MessageLock {
		public Message result;
	}

	/**
	 * A received Message sent through a SocketSession
	 */
	private class SessionMessage {
		SocketSession session;
		Message message;

		public SessionMessage(SocketSession session, Message message) {
			this.session = session;
			this.message = message;
		}
	}

	/**
	 * A Thread that process a messages list received through a SocketSession for a specified registered service.
	 * 
	 * @author andresoviedo
	 * 
	 */
	private class ServiceThread extends Thread {

		List<SessionMessage> msgs;

		ServiceThread(List<SessionMessage> msgs, String threadName) {
			super(threadName);
			this.msgs = msgs;
		}

		@Override
		public void run() {
			try {
				while (true) {
					SessionMessage msg;
					synchronized (msgs) {
						while (msgs.isEmpty())
							msgs.wait();
						msg = msgs.remove(0);
					}
					synchronized (ServiceThread.class) {
						while (serviceThreadsPaused)
							ServiceThread.class.wait();
						serviceThreadsExecuting++;
						ServiceThread.class.notifyAll();
					}
					try {
						messageReceived(msg.session, msg.message);
					} catch (Exception ex) {
						logger.severe("Exception caught while processing the received message: " + ex.getMessage());
					}
					synchronized (ServiceThread.class) {
						serviceThreadsExecuting--;
						ServiceThread.class.notifyAll();
					}
				}
			} catch (InterruptedException ex) {
				logger.info("Thread interrupted while waiting to deliver messages");
			}
		}
	}

}