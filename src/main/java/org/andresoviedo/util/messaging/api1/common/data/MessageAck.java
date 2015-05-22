package org.andresoviedo.util.messaging.api1.common.data;

/**
 * A message acknowledgement command.
 * 
 * @author andres
 */
public class MessageAck extends SignalingCommand {

	private static final long serialVersionUID = 9063909037929215916L;

	/**
	 * The id of the acknowledged message. This field is immutable.
	 */
	private String messageId;

	/**
	 * Constructs a new message acknowledgement command.
	 * 
	 * @param messageId
	 *          the id of the acknowledged message.
	 */
	public MessageAck(String clientId, String messageId) {
		super(clientId);
		if (messageId == null) {
			throw new IllegalArgumentException("The message id is null.");
		}
		this.messageId = messageId;
	}

	/**
	 * Returns acknowledged message's id.
	 * 
	 * @return acknowledged message's id.
	 */
	public String getMessageId() {
		return messageId;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("MessageAck [");
		sb.append("clientId=").append(getClientId()).append(", ");
		sb.append("messageId=").append(messageId).append("]");

		return sb.toString();
	}

}
