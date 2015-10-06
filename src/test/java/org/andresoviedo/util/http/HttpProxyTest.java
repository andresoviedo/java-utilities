package org.andresoviedo.util.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ServerSocket;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.junit.Assert;
import org.junit.Test;

public class HttpProxyTest {

	private static int freePort() throws IOException {
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}

	@Test
	public void test_get_without_proxy() throws MalformedURLException {
		String response = HttpUtils.GET(new URL("http://www.google.es"), 5000);
		System.out.println(response);
		Assert.assertNotNull(response);
	}

	@Test
	public void test_get_with_proxy() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		int httpProxyPort;
		HttpProxy proxy;

		httpProxyPort = freePort();
		proxy = new HttpProxy(httpProxyPort);
		proxy.start();

		String response = HttpUtils.GET(new URL("http://www.google.es"), null, new Proxy(Type.HTTP, new InetSocketAddress(httpProxyPort)),
				5000);
		System.out.println(response);
		Assert.assertNotNull(response);

		if (proxy != null) {
			proxy.stop();
		}
	}

}
