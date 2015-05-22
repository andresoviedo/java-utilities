package org.andresoviedo.util.messaging.api1.server;

/**
 * The interface an object able to authenticate a session has to implement.
 * 
 * @author andres
 */
public interface SessionAuthenticator {

	/**
	 * Returns <code>true</code> if the specified client id is allowed, <code>false</code> otherwise.
	 * 
	 * @param clientId
	 *          the client id.
	 * @return <code>true</code> if the specified client id is allowed, <code>false</code> otherwise.
	 */
	public boolean authenticate(String clientId);

}
