package org.andresoviedo.util.jgroups;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Arrays;

import org.andresoviedo.util.http.NanoHTTPD;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JGroupsClusterTest {

	JGroupsCluster instance1;
	JGroupsCluster instance2;

	@Before
	public void setUp() {
		instance1 = new JGroupsCluster(new InetSocketAddress("127.0.0.1", 18000), Arrays.asList(new InetSocketAddress("127.0.0.1", 18001)));
		instance2 = new JGroupsCluster(new InetSocketAddress("127.0.0.1", 18001), Arrays.asList(new InetSocketAddress("127.0.0.1", 18000)));

		instance1.init();
		instance2.init();
	}

	@After
	public void tearDown() {
		try {
			instance1.close();
		} catch (Exception ex) {
		}
		instance1 = null;

		try {
			instance2.close();
		} catch (Exception ex) {
		}
		instance2 = null;
	}

	@Test
	public void test_chat() throws Exception {
		instance1.broadcast("" + System.currentTimeMillis() + " - hola de 1 para 2");
		instance2.broadcast("" + System.currentTimeMillis() + " - hola de 2 para 1");

//		Thread.sleep(2000);
		System.out.println("...sleep ended");
	}

	@Test
	public void test_start_when_tcp_port_occupied() throws Exception {
		NanoHTTPD httpServer1 = start_httpserver_1();
		NanoHTTPD httpServer2 = start_httpserver_2();

		test_chat();

		test_http();

		httpServer1.stop();
		httpServer2.stop();
	}

	private void test_http() throws Exception {
		Assert.assertEquals("pong 1", sendGet("http://127.0.0.1:18000/ping"));
		Assert.assertEquals("pong 2", sendGet("http://127.0.0.1:18001/ping"));
	}

	// HTTP GET request
	private String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", "java");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();

	}

	// Scanner scanner = new Scanner(System.in);
	// for (int i = 0; i < 10; i++) {
	// ch.send(null, scanner.next());
	// }
	// scanner.close();

	private NanoHTTPD start_httpserver_2() throws IOException {
		NanoHTTPD httpServer2 = new NanoHTTPD(18001) {

			@Override
			public Response serve(IHTTPSession session) {
				return newFixedLengthResponse("pong 2");
			}
		};
		httpServer2.start();
		return httpServer2;
	}

	private NanoHTTPD start_httpserver_1() throws IOException {
		NanoHTTPD httpServer1 = new NanoHTTPD(18000) {

			@Override
			public Response serve(IHTTPSession session) {
				return newFixedLengthResponse("pong 1");
			}
		};
		httpServer1.start();
		return httpServer1;
	}

	// for (int i = 0; i < 5; i++) {
	// Thread.sleep((long) (1000d * Math.random()));
	// LockService lock_service = new LockService(ch);
	// Lock lock = lock_service.getLock("mylock");
	// if (lock.tryLock(3000, TimeUnit.MILLISECONDS)) {
	// try {
	// Thread.sleep(1000);
	// ch.send(null, "" + System.currentTimeMillis() + " - " + String.valueOf("Lock acquired by: " + bind_address.getPort()));
	// Thread.sleep(1000);
	// } finally {
	// lock.unlock();
	// }
	// }
	// }
	// ch.close();
	// }
}