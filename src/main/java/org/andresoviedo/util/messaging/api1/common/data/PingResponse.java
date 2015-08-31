package org.andresoviedo.util.messaging.api1.common.data;

/**
 * The response to a ping request command.
 * 
 * @author andresoviedo
 */
public class PingResponse extends SignalingCommand {

	private static final long serialVersionUID = 8459666838735011147L;

	/**
	 * Creates a new ping response.
	 * 
	 * @param clientId
	 *            the client id.
	 */
	public PingResponse(String clientId) {
		super(clientId);
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PingResponse [");
		sb.append("clientId=").append(getClientId()).append("]");

		return sb.toString();
	}

}
