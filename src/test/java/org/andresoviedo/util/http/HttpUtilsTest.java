package org.andresoviedo.util.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.andresoviedo.util.http.NanoHTTPD.IHTTPSession;
import org.andresoviedo.util.http.NanoHTTPD.Response;
import org.andresoviedo.util.http.NanoHTTPD.Response.Status;
import org.andresoviedo.util.http.server.HttpService;
import org.andresoviedo.util.http.server.RequestHandler;
import org.andresoviedo.util.http.server.impl.HttpServiceImpl;
import org.junit.Assert;
import org.junit.Test;

public class HttpUtilsTest {

	@Test
	public void test_get_http() throws MalformedURLException {
		String response = HttpUtils.GET(new URL("http://www.google.es"), 5000);
		System.out.println(response);
		Assert.assertNotNull(response);
	}

	@Test
	public void test_get_https() throws MalformedURLException {
		String response = HttpUtils.GET(new URL("https://www.google.es/"), 5000);
		System.out.println(response);
		Assert.assertNotNull(response);
	}

	private static int freePort() throws IOException {
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}

	@Test
	public void test_get_https_insecure() throws IOException, InterruptedException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException {
		int port = freePort();
		HttpService httpService = new HttpServiceImpl(port);
		httpService.addHandler(new RequestHandler() {

			@Override
			public Response handle(IHTTPSession session) {
				return NanoHTTPD.newFixedLengthResponse(Status.OK, "text/plain", "pong!");
			}

			@Override
			public boolean canHandle(String uri) {
				return uri.equals("/ping");
			}
		});
		httpService.makeSecure(NanoHTTPD.makeSSLSocketFactory("/server.jks", "password".toCharArray()));
		System.out.println("Starting https server at port '" + port + "'...");
		httpService.start();

		// Thread.sleep(30000);

		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(HttpProxyTest.class.getResourceAsStream("/client.jks"), "password".toCharArray());

		String response = HttpUtils.GET(new URL("https://localhost:" + port + "/ping"), keyStore, Proxy.NO_PROXY, 5000);
		System.out.println(response);
		Assert.assertNotNull(response);

		httpService.stop();
	}

}
