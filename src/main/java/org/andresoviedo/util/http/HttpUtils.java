package org.andresoviedo.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;

public final class HttpUtils {

	private static final Logger LOG = Logger.getLogger(HttpUtils.class);

	public static String GET(URL url, int timeout) {
		return GET(url, null, Proxy.NO_PROXY, timeout);
	}

	public static String GET(URL url, KeyStore keyStore, Proxy proxy, int timeout) {

		final StringBuffer response = new StringBuffer();
		int responseCode = -1;
		try {

			LOG.debug("Connecting to '" + url + "'... using proxy '" + proxy + "'...");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
			conn.setConnectTimeout(timeout);

			// if https set trustStore
			if ("https".equals(url.getProtocol().toLowerCase()) && keyStore != null) {
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(keyStore);
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, tmf.getTrustManagers(), null);
				SSLSocketFactory sslFactory = ctx.getSocketFactory();
				((HttpsURLConnection) conn).setSSLSocketFactory(sslFactory);
			}

			conn.connect();

			responseCode = conn.getResponseCode();
			LOG.debug("Response code '" + responseCode + "'");

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			conn.disconnect();

			LOG.debug("Response msg '" + response.toString() + "'");
		} catch (Exception ex) {
			LOG.error("Oops! There was a problem executing request", ex);
			return null;
		}

		return response.toString();
	}

	/**
	 * Opens a port and read the HTTP GET request. Util to mock a HTTP servers,
	 * read the data sent by the client and then validating the response
	 * 
	 * @param ret
	 *            data readed (output variable)
	 * @param port
	 *            http server port
	 * @param response
	 *            the mock HTTP response
	 * @return the server socket where the HTTP server is started
	 * @throws IOException
	 *             in case there is a problem opening the port
	 */
	public static ServerSocket readGET(final StringBuilder ret, final int port, final String response)
			throws IOException {
		final ServerSocket serverSocket = new ServerSocket(port);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Listening to " + port);
					Socket clientSocket = serverSocket.accept();
					System.out.println("Client connected " + clientSocket.getPort());
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

					// read headers
					String line;
					int contentLength = 0;
					while ((line = in.readLine()) != null) {
						System.out.println(line);
						ret.append(line).append(System.getProperty("line.separator"));

						// detect eof
						if (line.length() == 0) {
							break;
						}
					}

					// parse content-length
					Pattern p = Pattern.compile("(?s)(?i).*Content-Length.*(\\d+).*");
					Matcher contentLengthMatcher = p.matcher(ret.toString());
					if (contentLengthMatcher.matches()) {
						contentLength = Integer.parseInt(contentLengthMatcher.group(1));
						System.out.println("Content-Length:" + contentLength);
					}

					// read body
					char[] buffer = new char[1024];
					int readed;
					readed = 0;
					while (contentLength > 0 && readed < contentLength && (readed = in.read(buffer)) != -1) {
						String inputLine = new String(buffer, 0, readed);
						System.out.println(inputLine);
						ret.append(inputLine).append(System.getProperty("line.separator"));
					}
					clientSocket.getOutputStream().write(response.getBytes());
					in.close();
					clientSocket.close();
					serverSocket.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		t.start();
		return serverSocket;
	}
}
