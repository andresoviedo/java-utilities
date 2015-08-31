package org.andresoviedo.util.messaging.api1.common.data;

/**
 * A command used to test the connection.
 * 
 * @author andresoviedo
 */
public class PingRequest extends SignalingCommand {

	private static final long serialVersionUID = 5279729498471995645L;

	/**
	 * Creates a new ping request.
	 * 
	 * @param clientId
	 *            the client id.
	 */
	public PingRequest(String clientId) {
		super(clientId);
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PingRequest [");
		sb.append("clientId=").append(getClientId()).append("]");

		return sb.toString();
	}

}
