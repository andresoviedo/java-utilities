package org.andresoviedo.util.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;

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
		String response = HttpUtils.GET(new URL("http://www.google.es"), null, 5000);
		System.out.println(response);
		Assert.assertNotNull(response);
	}

	@Test
	public void test_get_with_proxy() throws IOException {
		int httpProxyPort;
		HttpProxy sut;

		httpProxyPort = freePort();
		sut = new HttpProxy(httpProxyPort);
		sut.start();

		String response = HttpUtils.GET(new URL("http://www.google.es"), new InetSocketAddress(httpProxyPort), 5000);
		System.out.println(response);
		Assert.assertNotNull(response);

		if (sut != null) {
			sut.stop();
		}
	}

}
