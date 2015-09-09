package org.andresoviedo.util.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HttpProxyTest {

	int httpProxyPort;
	HttpProxy sut;

	@Before
	public void setUp() throws Exception {
		httpProxyPort = freePort();
		sut = new HttpProxy(httpProxyPort);
		sut.start();
	}

	@After
	public void tearDown() throws Exception {
		if (sut != null) {
			sut.stop();
		}
	}

	private static int freePort() throws IOException {
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}

	@Test
	public void test() throws MalformedURLException {
		String response = HttpUtils.GET(new URL("http://www.google.es"), new InetSocketAddress(httpProxyPort), 5000);
		System.out.println(response);
		Assert.assertNotNull(response);
	}

}
