package org.andresoviedo.util.messaging.api1.common.data;

/**
 * A login request command.
 * 
 * @author andresoviedo
 */
public class LoginRequest extends SignalingCommand {

	private static final long serialVersionUID = 5647993415529384248L;

	/**
	 * Creates a new login request command.
	 * 
	 * @param clientId
	 *            the client id.
	 */
	public LoginRequest(String clientId) {
		super(clientId);
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LoginRequest [");
		sb.append("clientId=").append(getClientId()).append("]");

		return sb.toString();
	}

}
