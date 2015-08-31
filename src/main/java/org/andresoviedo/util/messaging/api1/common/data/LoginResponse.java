package org.andresoviedo.util.messaging.api1.common.data;

/**
 * A login response command.
 * 
 * @author andresoviedo
 */
public class LoginResponse extends SignalingCommand {

	private static final long serialVersionUID = -4506247767355639918L;

	/**
	 * A constant indicating a successful login.
	 */
	public static final int LOGIN_OK = 0;

	/**
	 * A constant indicating a login failure.
	 */
	public static final int LOGIN_NOT_OK = 1;

	/**
	 * The result of the login process. This field is immutable.
	 */
	private int result;

	/**
	 * Creates a new login response command.
	 * 
	 * @param clientId
	 *            the client id.
	 * @param result
	 *            the result.
	 */
	public LoginResponse(String clientId, int result) {
		super(clientId);
		if ((result != LOGIN_OK) && (result != LOGIN_NOT_OK)) {
			throw new IllegalArgumentException("Invalid result: " + result);
		}
		this.result = result;
	}

	/**
	 * Returns the result of a previously sent login request.
	 * 
	 * @return the result of a previously sent login request.
	 */
	public int getResult() {
		return result;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LoginResponse [");
		sb.append("clientId=").append(getClientId()).append(", ");
		sb.append("result=").append(result).append("]");

		return sb.toString();
	}

}