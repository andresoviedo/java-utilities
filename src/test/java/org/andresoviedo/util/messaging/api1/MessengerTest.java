package org.andresoviedo.util.messaging.api1;

import java.io.IOException;
import java.util.logging.LogManager;

import org.andresoviedo.util.messaging.api1.client.ClientMessenger;
import org.andresoviedo.util.messaging.api1.common.data.Message;
import org.andresoviedo.util.messaging.api1.common.data.MessageFactory;
import org.andresoviedo.util.messaging.api1.common.data.TextMessage;
import org.andresoviedo.util.messaging.api1.common.service.ServiceListener;
import org.andresoviedo.util.messaging.api1.server.ServerMessenger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MessengerTest {

	private ServerMessenger serverMessenger;
	private ClientMessenger clientMessenger1;
	private MessageFactory messageFactory1;
	private ClientMessenger clientMessenger2;
	private MessageFactory messageFactory2;

	@BeforeClass
	public static void beforeClass() throws SecurityException, IOException {
		LogManager.getLogManager().readConfiguration(MessengerTest.class.getResourceAsStream("/logging.properties"));
	}

	@Before
	public void setUp() {
		serverMessenger = ServerMessenger.getInstance();
		serverMessenger.start();

		clientMessenger1 = ClientMessenger.getInstance("messaging.client1.properties");
		messageFactory1 = clientMessenger1.createMessageFactory();
		clientMessenger1.start();

		clientMessenger2 = ClientMessenger.getInstance("messaging.client2.properties");
		messageFactory2 = clientMessenger2.createMessageFactory();
		clientMessenger2.start();
	}

	@After
	public void tearDown() {
		clientMessenger1.stop();
		clientMessenger1 = null;

		clientMessenger2.stop();
		clientMessenger2 = null;

		serverMessenger.stop();
		serverMessenger = null;
	}

	@Test
	public void testContextOK() {
		Assert.assertNotNull(serverMessenger);
		Assert.assertNotNull(clientMessenger1);
		Assert.assertNotNull(clientMessenger2);
	}

	@Test
	public void testSend_PeerToPeer() throws MessengerException, InterruptedException {

		// let some time for clients to connect...
		Thread.sleep(1000);

		clientMessenger2.setServiceListener("testService2", new ServiceListener() {

			@Override
			public void processMessage(Message message) {
				if (!(message instanceof TextMessage)) {
					throw new IllegalArgumentException("Unexpected message: " + message);
				}
				String text = ((TextMessage) message).getText();
				if (!"ping".equals(text)) {
					throw new IllegalArgumentException("Unexpected message: " + text);
				}
				Message reply = (TextMessage) messageFactory2.createTextMessageReply("pong", message);
				try {
					clientMessenger2.send(reply);
				} catch (MessengerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Message pingRequest = messageFactory1.createTextMessage("testService1", "testService2", "ping", 1000L);
		pingRequest.setTargetClientId("client2");
		Assert.assertEquals("pong", ((TextMessage) clientMessenger1.sendAndReceive(pingRequest)).getText());

	}

}
