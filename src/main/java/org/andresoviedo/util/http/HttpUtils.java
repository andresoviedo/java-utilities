package org.andresoviedo.util.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyStore;

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
}
