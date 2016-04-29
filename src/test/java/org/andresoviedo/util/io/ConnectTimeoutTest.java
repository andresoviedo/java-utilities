package org.andresoviedo.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ConnectTimeoutTest {

	private ServerSocket serverSocket;

	private int port;

	@Before
	public void before() throws IOException {
		// server socket with single element backlog queue (1) and dynamicaly
		// allocated port (0)
		serverSocket = new ServerSocket(0, 1);
		// just get the allocated port
		port = serverSocket.getLocalPort();
		// fill backlog queue by this request so consequent requests will be
		// blocked
		// new Socket().connect(serverSocket.getLocalSocketAddress());
	}

	@After
	public void after() throws IOException {
		// some cleanup
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
	}

	@Test
	public void readRequestDataForever() throws IOException, InterruptedException {
		Socket socket = null;
		System.out.println("Waiting to receive calls at port '" + port + "'...");
		while ((socket = serverSocket.accept()) != null) {
			InputStream inputStream = socket.getInputStream();
			int read = 0;
			while ((read = inputStream.read()) != -1) {
				System.out.println("Reading and waiting...");
				Thread.sleep(1000);
			}
		}
	}

	@Test
	public void testConnect() throws IOException {
		URL url = new URL("http://localhost:" + port); // use allocated port
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(1000);
		// connection.setReadTimeout(2000); //irelevant in this case
		try {
			connection.getInputStream();
		} catch (SocketTimeoutException stx) {
			Assert.assertEquals(stx.getMessage(), "connect timed out"); // that's
																		// what
																		// are
																		// we
																		// waiting
																		// for
		}
	}
}