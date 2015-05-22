package org.andresoviedo.util.messaging.api1.common.service;

import org.andresoviedo.util.messaging.api1.Messenger;
import org.andresoviedo.util.messaging.api1.common.data.Message;

/**
 * The base class for a service.
 * 
 * @author andres
 */
public abstract class Service implements ServiceListener {

	/**
	 * The messenger to let the service send messages.
	 */
	private Messenger messenger;

	/**
	 * Constructs a new service.
	 * 
	 * @param messenger
	 *          the associated messenger.
	 */
	public Service(Messenger messenger) {
		if (messenger == null) {
			throw new IllegalArgumentException("Messenger is null.");
		}
		this.messenger = messenger;
	}

	/**
	 * Returns the associated messenger.
	 * 
	 * @return the associated messenger.
	 */
	public Messenger getMessenger() {
		return this.messenger;
	}

	/**
	 * Returns the id of the service.
	 * 
	 * @return the id of the service.
	 */
	public abstract String getServiceId();

	/*
	 * @see org.andresoviedo.util.messaging.api1.common.service.ServiceListener#processMessage(org.andresoviedo.util.messaging.api1.common.data.Message)
	 */
	public abstract void processMessage(Message message);

}
