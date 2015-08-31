package org.andresoviedo.util.messaging.api1.common.data;

import java.io.Serializable;

/**
 * A singleton class used to create messages. Notice that messages cannot be created directly since their constructors are protected. This
 * class must always be used to create messages.
 * 
 * @author andresoviedo
 */
public class MessageFactory {

	/**
	 * The last generated id.
	 */
	private long lastId = -1;

	/**
	 * Client id.
	 */
	private String clientId;

	private MessageFactory(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the single instance of this class.
	 */
	public static MessageFactory getInstance(String clientId) {
		return new MessageFactory(clientId);
	}

	/**
	 * Creates a text message with immediate expiration (no timeout).
	 * 
	 * @param serviceId
	 *            the service id.
	 * @param text
	 *            the text.
	 * @return the resulting text message.
	 */
	public Message createTextMessage(String serviceId, String text) {
		return createTextMessage(serviceId, text, -1);
	}

	/**
	 * Creates a text message with the specified timeout.
	 * 
	 * @param serviceId
	 *            the service id.
	 * @param text
	 *            the text.
	 * @param timeout
	 *            the timeout.
	 * @return the resulting text message.
	 */
	public Message createTextMessage(String serviceId, String text, long timeout) {
		return createTextMessage(serviceId, serviceId, text, timeout);
	}

	/**
	 * Creates a text message with the specified target service id and timeout.
	 * 
	 * @param serviceId
	 *            the service id.
	 * @param targetServiceId
	 *            the target service id.
	 * @param text
	 *            the text.
	 * @param timeout
	 *            the timeout.
	 * @return the resulting text message.
	 */
	public Message createTextMessage(String serviceId, String targetServiceId, String text, long timeout) {
		TextMessage message = new TextMessage(text);
		configureMessage(message, serviceId, targetServiceId, timeout);

		return message;
	}

	/**
	 * Creates an object message with immediate expiration (no timeout).
	 * 
	 * @param serviceId
	 *            the service id.
	 * @param object
	 *            the serializable object.
	 * @return the resulting object message.
	 */
	public Message createObjectMessage(String serviceId, Serializable object) {
		return createObjectMessage(serviceId, object, -1);
	}

	/**
	 * Creates an object message with the specified timeout.
	 * 
	 * @param serviceId
	 *            the service id.
	 * @param object
	 *            the serializable object.
	 * @param timeout
	 *            the timeout.
	 * @return the resulting object message.
	 */
	public Message createObjectMessage(String serviceId, Serializable object, long timeout) {
		return createObjectMessage(serviceId, serviceId, object, timeout);
	}

	/**
	 * Creates an object message with the specified target service id and timeout.
	 * 
	 * @param serviceId
	 *            the service id.
	 * @param targetServiceId
	 *            the target service id.
	 * @param object
	 *            the serializable object.
	 * @param timeout
	 *            the timeout.
	 * @return the resulting object message.
	 */
	public Message createObjectMessage(String serviceId, String targetServiceId, Serializable object, long timeout) {
		ObjectMessage message = new ObjectMessage(object);
		configureMessage(message, serviceId, targetServiceId, timeout);

		return message;
	}

	/**
	 * Creates a reply text message with immediate expiration (no timeout).
	 * 
	 * @param text
	 *            the text.
	 * @param original
	 *            the original message.
	 * @return the resulting text message.
	 */
	public Message createTextMessageReply(String text, Message original) {
		return createTextMessageReply(text, -1, original);
	}

	/**
	 * Creates a reply text message with the specified timeout.
	 * 
	 * @param text
	 *            the text.
	 * @param timeout
	 *            the timeout.
	 * @param original
	 *            the original message.
	 * @return the resulting text message.
	 */
	public Message createTextMessageReply(String text, long timeout, Message original) {
		TextMessage reply = new TextMessage(text);
		reply.setClientId(clientId);
		reply.setMessageId(generateId());

		configureReply(reply, original, timeout);

		return reply;
	}

	/**
	 * Creates a reply object message with immediate expiration (no timeout).
	 * 
	 * @param original
	 *            the original message.
	 * @param object
	 *            the serializable object.
	 * @return the resulting object message.
	 */
	public Message createObjectMessageReply(Serializable object, Message original) {
		return createObjectMessageReply(object, -1, original);
	}

	/**
	 * Creates a reply object message with the specified timeout.
	 * 
	 * @param original
	 *            the original message.
	 * @param timeout
	 *            the timeout.
	 * @param object
	 *            the serializable object.
	 * @return the resulting object message.
	 */
	public Message createObjectMessageReply(Serializable object, long timeout, Message original) {
		ObjectMessage reply = new ObjectMessage(object);
		reply.setClientId(clientId);
		reply.setMessageId(generateId());

		configureReply(reply, original, timeout);

		return reply;
	}

	/**
	 * Convinience method to set some properties when creating a message.
	 * 
	 * @param message
	 *            the message to configure.
	 * @param serviceId
	 *            the service id.
	 * @param targetServiceId
	 *            the target service id.
	 * @param timeout
	 *            the timeout.
	 */
	private void configureMessage(Message message, String serviceId, String targetServiceId, long timeout) {
		message.setClientId(clientId);
		message.setServiceId(serviceId);
		message.setTargetServiceId(targetServiceId);
		message.setMessageId(generateId());
		message.setTimeout(timeout);
	}

	/**
	 * Configure the reply message with information contained in the original message.
	 * 
	 * @param reply
	 *            the reply message.
	 * @param original
	 *            the original message.
	 * @param timeout
	 *            the timeout.
	 */
	private void configureReply(Message reply, Message original, long timeout) {
		reply.setServiceId(original.getTargetServiceId());
		reply.setTargetClientId(original.clientId);
		reply.setTargetServiceId(original.getServiceId());
		reply.setCorrelationClientId(clientId);
		reply.setCorrelationMessageId(original.getMessageId());
		reply.setTimeout(timeout);
	}

	/**
	 * Generates an id that can be used as a unique id for a message.
	 * 
	 * @return an id that can be used as a unique id for a message.
	 */
	private synchronized String generateId() {
		long now = System.currentTimeMillis();
		if (now > lastId) {
			lastId = now;
		} else {
			lastId++;
		}
		return String.valueOf(lastId);
	}

}