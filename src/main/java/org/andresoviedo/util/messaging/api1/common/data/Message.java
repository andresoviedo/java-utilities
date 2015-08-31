package org.andresoviedo.util.messaging.api1.common.data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * The abstract class for a message.
 * 
 * @author andresoviedo
 */
public abstract class Message implements Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2194424806812889269L;

	/**
	 * Id of the client sending this message (non-null).
	 */
	protected String clientId;

	/**
	 * Id of this message. This id must be non-null and unique for this client.
	 */
	protected String messageId;

	/**
	 * Id of the service sending this message (non-null).
	 */
	protected String serviceId;

	/**
	 * Id of the client this message is addressed to (null permitted).
	 */
	protected String targetClientId;

	/**
	 * Id of the service this message is addressed to (non-null).
	 */
	protected String targetServiceId;

	/**
	 * Id of the client that originated this message response (null permitted).
	 */
	protected String correlationClientId;

	/**
	 * Id of the message that originated this message response (null permitted).
	 */
	protected String correlationMessageId;

	/**
	 * Timeout of this message. 0 means infinite timeout, and a negative number means immediate expiration.
	 */
	protected long timeout;

	/**
	 * Timestamp this message was created.
	 */
	protected Date timeCreated;

	/**
	 * Timestamp this message was sent.
	 */
	protected Date timeSent;

	/**
	 * Timestamp this message was received.
	 */
	protected Date timeReceived;

	/**
	 * Indicates wheter the body of this message is compressed.
	 */
	protected boolean compressed;

	/**
	 * Additional non-standard properties.
	 */
	protected Map<String, Serializable> properties;

	/**
	 * Indicates whether the receiver has to acknowledge the message on reception or not.
	 */
	protected boolean needsAck;

	/**
	 * Package protected constructor, since messages are created from a factory.
	 */
	Message() {
		this.timeCreated = new Date();
		this.properties = new HashMap<String, Serializable>(0);
		this.needsAck = true;
	}

	/**
	 * Returns the client id.
	 * 
	 * @return the client id.
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Sets the client id.
	 * 
	 * @param clientId
	 *            the new client id.
	 */
	void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * Returns whether the body of this message is compressed.
	 * 
	 * @return <code>true</code> if the body of this message is compressed, <code>false</code> otherwise.
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * Sets whether the body of this message has to be compressed.
	 * 
	 * @param compressed
	 *            <code>true</code> if the body of this message has to be compressed, <code>false</code> otherwise.
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/**
	 * Returns the id of the client that originated this message response (may be <code>null</code>).
	 * 
	 * @return the id of the client that originated this message response.
	 */
	public String getCorrelationClientId() {
		return correlationClientId;
	}

	/**
	 * Sets the id of the client that originated this message response.
	 * 
	 * @param correlationClientId
	 *            the new id of the client that originated this message response.
	 */
	void setCorrelationClientId(String correlationClientId) {
		this.correlationClientId = correlationClientId;
	}

	/**
	 * Returns the id of the message that originated this message response (may be <code>null</code>).
	 * 
	 * @return the id of the message that originated this message response.
	 */
	public String getCorrelationMessageId() {
		return correlationMessageId;
	}

	/**
	 * Sets the id of the message that originated this message response.
	 * 
	 * @param correlationMessageId
	 *            the new id of the message that originated this message response.
	 */
	void setCorrelationMessageId(String correlationMessageId) {
		this.correlationMessageId = correlationMessageId;
	}

	/**
	 * Returns the id of the message.
	 * 
	 * @return the id of the message.
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * Sets the id of the message.
	 * 
	 * @param messageId
	 *            the new id of the message.
	 */
	void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * Returns additional non-standard properties.
	 * 
	 * @return additional non-standard properties.
	 */
	public Map<String, Serializable> getProperties() {
		return properties;
	}

	/**
	 * Sets additional non-standard properties.
	 * 
	 * @param properties
	 *            the new additional non-standard properties.
	 */
	public void setProperties(Hashtable<String, ? extends Serializable> properties) {
		this.properties.clear();
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	/**
	 * Returns the service id of the message.
	 * 
	 * @return the service id of the message.
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * Sets the service id of the message.
	 * 
	 * @param serviceId
	 *            the new service id of the message.
	 */
	void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * Returns the id of the client this message is addressed to (may be <code>null</code>).
	 * 
	 * @return the id of the client this message is addressed to (may be <code>null</code>).
	 */
	public String getTargetClientId() {
		return targetClientId;
	}

	/**
	 * Sets the id of the client this message is addressed to.
	 * 
	 * @param targetClientId
	 *            the new id of the client this message is addressed to.
	 */
	public void setTargetClientId(String targetClientId) {
		this.targetClientId = targetClientId;
	}

	/**
	 * Returns the id of the service this message is addressed to.
	 * 
	 * @return the id of the service this message is addressed to.
	 */
	public String getTargetServiceId() {
		return targetServiceId;
	}

	/**
	 * Sets the id of the service this message is addressed to.
	 * 
	 * @param targetServiceId
	 *            the new id of the service this message is addressed to.
	 */
	void setTargetServiceId(String targetServiceId) {
		this.targetServiceId = targetServiceId;
	}

	/**
	 * Returns the timestamp this message was created.
	 * 
	 * @return the timestamp this message was created.
	 */
	public Date getTimeCreated() {
		return timeCreated;
	}

	/**
	 * Sets the timestamp this message was created.
	 * 
	 * @param timeCreated
	 *            the new timestamp this message was created.
	 */
	void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	/**
	 * Returns the timeout of the message (in milliseconds). 0 means infinite timeout, and a negative number means immediate expiration.
	 * 
	 * @return the timeout of the message.
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Sets the timeout of the message (in milliseconds). 0 means infinite timeout, and a negative number means immediate expiration.
	 * 
	 * @param timeout
	 *            the new timeout of the message.
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * Returns the timestamp this message was received.
	 * 
	 * @return the timestamp this message was received.
	 */
	public Date getTimeReceived() {
		return timeReceived;
	}

	/**
	 * Sets the timestamp this message was received.
	 * 
	 * @param timeReceived
	 *            the new timestamp this message was received.
	 */
	public void setTimeReceived(Date timeReceived) {
		this.timeReceived = timeReceived;
	}

	/**
	 * Returns the timestamp this message was sent.
	 * 
	 * @return the timestamp this message was sent.
	 */
	public Date getTimeSent() {
		return timeSent;
	}

	/**
	 * Sets the timestamp this message was sent.
	 * 
	 * @param timeSent
	 *            the new timestamp this message was sent.
	 */
	public void setTimeSent(Date timeSent) {
		this.timeSent = timeSent;
	}

	/**
	 * Stores the specified serializable property in the properties map.
	 * 
	 * @param key
	 *            the property's key (<code>null</code> permitted).
	 * @param value
	 *            the property's value (<code>null</code> permitted).
	 */
	public void putProperty(String key, Serializable value) {
		properties.put(key, value);
	}

	/**
	 * Returns the object value of the property stored with the specified key. Note that the object is serializable.
	 * 
	 * @param key
	 *            the property's key (<code>null</code> permitted).
	 * @return the object value of the property stored with the specified key.
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}

	/**
	 * Stores the specified string property in the properties map.
	 * 
	 * @param key
	 *            the property's key (<code>null</code> permitted).
	 * @param value
	 *            the property's value (<code>null</code> permitted).
	 */
	public void putStringProperty(String key, String value) {
		putProperty(key, (Serializable) value);
	}

	/**
	 * Returns the string value of the property stored with the specified key.
	 * 
	 * @param key
	 *            the property's key (<code>null</code> permitted).
	 * @return the string value of the property stored with the specified key.
	 */
	public String getStringProperty(String key) {
		return (String) getProperty(key);
	}

	/**
	 * Returns whether the receiver has to acknowledge the message on reception or not.
	 * 
	 * @return <code>true</code> if this message has to be acknowledged, <code>false</code> otherwise.
	 */
	public boolean isNeedsAck() {
		return needsAck;
	}

	/**
	 * Sets whether the receiver has to acknowledge the message on reception or not.
	 * 
	 * @param needsAck
	 *            <code>true</code> if this message has to be acknowledged, <code>false</code> otherwise.
	 */
	public void setNeedsAck(boolean needsAck) {
		this.needsAck = needsAck;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("Message:");
		sb.append(" clientId[" + getClientId() + "]");
		sb.append(" messageId[" + getMessageId() + "]");
		sb.append(" serviceId[" + getServiceId() + "]");

		return sb.toString();
	}

}