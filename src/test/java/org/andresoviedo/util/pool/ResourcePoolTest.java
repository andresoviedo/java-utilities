package org.andresoviedo.util.pool;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResourcePoolTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_testUserPool_Constructor() {
		try {
			new ResourcePool<String>(Collections.<String> emptyList());
			Assert.fail();
		} catch (RuntimeException ex) {
		}
		new ResourcePool<String>(Collections.singletonList("resource_1"));
	}

	@Test
	public void test_testUserPool_get_operation_happyPath() throws TimeoutException {
		ResourcePool<String> testUserPool = new ResourcePool<String>(Arrays.asList("user1", "user2"));
		String user = testUserPool.get();
		Assert.assertNotNull(user);
	}

	@Test
	public void test_testUserPool_production_simulation() throws TimeoutException {
		ResourcePool<String> testUserPool = new ResourcePool<String>(Arrays.asList("user1", "user2"));
		String user1 = testUserPool.get();
		Assert.assertNotNull(user1);
		Assert.assertEquals("user1", user1);
		String user2 = testUserPool.get();
		Assert.assertNotNull(user2);
		Assert.assertEquals("user2", user2);
		testUserPool.setTimeout(1000);
		try {
			testUserPool.get();
			Assert.fail();
		} catch (Exception ex) {
		}
		testUserPool.putBack(user2);
		testUserPool.putBack(user1);
		user2 = testUserPool.get();
		Assert.assertNotNull(user2);
		Assert.assertEquals("user2", user2);
		user1 = testUserPool.get();
		Assert.assertNotNull(user1);
		Assert.assertEquals("user1", user1);

	}

	@Test
	public void test_testUserPool_get_operation_happyPath_with_wait() throws TimeoutException {
		final ResourcePool<String> testUserPool = new ResourcePool<String>(Arrays.asList("user1"));
		final String user = testUserPool.get();
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
				}
				testUserPool.putBack(user);
			}
		});
		long startTime = System.currentTimeMillis();
		t.start();
		while (!t.isAlive()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			}
		}
		Assert.assertNotNull(testUserPool.get());
		Assert.assertTrue((System.currentTimeMillis() - startTime) >= 1000);
	}

	@Test
	public void test_testUserPool_get_operation_timed_out() throws TimeoutException {
		final ResourcePool<String> testUserPool = new ResourcePool<String>(Arrays.asList("user1"));
		Assert.assertNotNull(testUserPool.get());
		long now = System.currentTimeMillis();
		try {
			// we hack the timeout to not to wait the default 1 minute
			testUserPool.setTimeout(1000);
			Assert.assertNotNull(testUserPool.get());
			Assert.fail("This should explode because there is no users available");
		} catch (RuntimeException ex) {
			Assert.assertTrue((System.currentTimeMillis() - now) >= testUserPool.getTimeout());
		}

	}

	@Test
	public void test_testUserPool_putBack_operation_happyPath() throws TimeoutException {
		ResourcePool<String> testUserPool = new ResourcePool<String>(Arrays.asList("user1"));
		String user = testUserPool.get();
		Assert.assertNotNull(user);
		testUserPool.putBack(user);
		String user2 = testUserPool.get();
		Assert.assertNotNull(user2);
		Assert.assertEquals(user, user2);
	}

}
