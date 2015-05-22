package org.andresoviedo.util.messaging.api1.common.data;

/**
 * The base class for commands used internally for signaling. Signaling commands are transparent to users, and of course should not be
 * persisted.
 * 
 * @author andres
 */
public abstract class SignalingCommand implements Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3970649938226501705L;
	/**
	 * Id of the client sending this command (non-null).
	 */
	private String clientId;

	/**
	 * Constructs a new signalling command.
	 * 
	 * @param clientId
	 *          the client id.
	 */
	public SignalingCommand(String clientId) {
		if (clientId == null) {
			throw new IllegalArgumentException("The client id is null.");
		}
		this.clientId = clientId;
	}

	/**
	 * Returns the client id.
	 * 
	 * @return the client id.
	 */
	public String getClientId() {
		return clientId;
	}

}
