package org.andresoviedo.util.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.apache.log4j.Logger;

public final class HttpUtils {

	private static final Logger LOG = Logger.getLogger(HttpProxy.class);

	public static String GET(URL url, int timeout) {
		return GET(url, null, timeout);
	}

	public static String GET(URL url, InetSocketAddress proxyAddress, int timeout) {

		final StringBuffer response = new StringBuffer();
		int responseCode = -1;
		try {

			Proxy proxy = proxyAddress != null ? new Proxy(Proxy.Type.HTTP, proxyAddress) : Proxy.NO_PROXY;
			LOG.debug("Connecting to '" + url + "'... using proxy '" + proxy + "'...");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
			conn.setConnectTimeout(timeout);
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
}
