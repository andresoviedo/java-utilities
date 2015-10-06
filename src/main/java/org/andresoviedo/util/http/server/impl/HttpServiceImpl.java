package org.andresoviedo.util.http.server.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andresoviedo.util.http.NanoHTTPD;
import org.andresoviedo.util.http.server.HttpService;
import org.andresoviedo.util.http.server.RequestHandler;
import org.apache.log4j.Logger;

/**
 * Http service implementation using nanohttpd as the http server. This class adds support for registering handlers (or
 * servlets).
 * 
 * @author andres
 *
 */
public final class HttpServiceImpl extends NanoHTTPD implements HttpService {

	private Logger LOG = Logger.getLogger(HttpServiceImpl.class);

	private final List<RequestHandler> handlers = new ArrayList<RequestHandler>();
	private final List<RequestHandler> supportHandlers = new ArrayList<RequestHandler>();

	public HttpServiceImpl(int port) {
		super(port);
	}

	public void addSupportHandler(RequestHandler handler) {
		this.supportHandlers.add(handler);
	}

	public void addHandler(RequestHandler handler) {
		this.handlers.add(handler);
	}

	@Override
	public Response serve(IHTTPSession session) {
		try {
			Map<String, String> files = new HashMap<String, String>();
			Method method = session.getMethod();
			if (Method.PUT.equals(method) || Method.POST.equals(method)) {
				try {
					session.parseBody(files);
				} catch (IOException ioe) {
					return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT,
							"SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
				} catch (ResponseException re) {
					return newFixedLengthResponse(re.getStatus(), NanoHTTPD.MIME_PLAINTEXT, re.getMessage());
				}
			}

			Map<String, String> parms = session.getParms();
			parms.put(NanoHTTPD.QUERY_STRING_PARAMETER, session.getQueryParameterString());

			LOG.info("Processing request... uri '" + session.getUri() + ", method '" + session.getMethod()
					+ "', params '" + session.getParms() + "', headers '" + session.getHeaders() + "'");

			for (RequestHandler handler : supportHandlers) {
				if (handler.canHandle(session.getUri())) {
					Response handlerResponse = handler.handle(session);
					LOG.debug("Support response '" + handlerResponse + "'");
					return handlerResponse;
				}
			}

			// INFO: Handle request with the first capable handler
			synchronized (handlers) {
				for (RequestHandler handler : handlers) {

					if (!handler.canHandle(session.getUri()))
						continue;

					Response handlerResponse = handler.handle(session);
					LOG.debug("Response '" + handlerResponse + "'");
					return handlerResponse;
				}
			}

			// INFO: We don't have a handler for this request. Return error!
			String msg = "No handler can server request";
			LOG.warn(msg);
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", msg);

		} catch (Exception ex) {
			// INFO: handle error to return error description to client
			LOG.error(ex.getMessage(), ex);
			return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", ex.getMessage());
		} finally {
			// TODO: allow settings headers
			// response.addHeader("Date", DATE_FORMAT.format(lastRequestDate));
			// response.addHeader("Last-Modified", DATE_FORMAT.format(lastRequestDate));
		}
	}

}
