package org.andresoviedo.util.http.server;

import org.andresoviedo.util.http.NanoHTTPD.IHTTPSession;
import org.andresoviedo.util.http.NanoHTTPD.Response;

/**
 * Nanohttpd request handlers interface. Note: This interface could be generic
 * and could be decouple from nano.
 * 
 * @author aoviedo
 * 
 */
public interface RequestHandler {

	/**
	 * Returns true if this handler can handle the request
	 * 
	 * @param uri
	 *            the request action
	 * @return true if this handler can handle the request, false otherwise.
	 */
	public boolean canHandle(String uri);

	/**
	 * Process the request with the specified parameters
	 * 
	 * @param session
	 * 
	 * @return the nano response
	 */
	public Response handle(IHTTPSession session);
}
