package org.andresoviedo.util.messaging.api1.common.service;

import org.andresoviedo.util.messaging.api1.common.data.Message;

/**
 * The interface objects interested in receiving messages associated to a particular service have to implement.
 * 
 * @author andresoviedo
 */
public interface ServiceListener {

	/**
	 * Processes the specified message.
	 * 
	 * @param message
	 *            the message to process.
	 */
	public void processMessage(Message message);

}
