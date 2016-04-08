package org.andresoviedo.util.program;

import org.andresoviedo.util.program.ProgramUtils.RequestsPerSecondController;
import org.junit.Assert;
import org.junit.Test;

public class ProgramUtilsTest {

	@Test
	public void testRequestsPerSecondController() throws InterruptedException {
		RequestsPerSecondController sut = new RequestsPerSecondController(100);

		long now = System.currentTimeMillis();

		sut.start();
		// we need to do 500 requests
		for (int i = 0; i < 500; i++) {
			sut.newRequest();
			// emulate processing time
			Thread.sleep(5);
		}
		long end = System.currentTimeMillis();

		// assert that 10 seconds have passed since we started making requests
		Assert.assertTrue("Took: " + (end - now), end - now >= 5000);

	}

	@Test
	public void testRequestsPerSecondController_enough_bandwidth() throws InterruptedException {
		RequestsPerSecondController sut = new RequestsPerSecondController(1000);

		long now = System.currentTimeMillis();

		sut.start();
		// we need to do 500 requests
		for (int i = 0; i < 500; i++) {
			sut.newRequest();
			// emulate processing time
			Thread.sleep(5);
		}
		long end = System.currentTimeMillis();

		// assert that 10 seconds have passed since we started making requests
		Assert.assertTrue("Took: " + (end - now), end - now < 2600);

	}

	@Test
	public void testRequestsPerSecondController_program_too_slow() throws InterruptedException {
		RequestsPerSecondController sut = new RequestsPerSecondController(1);

		long now = System.currentTimeMillis();

		sut.start();
		// we need to do 500 requests
		for (int i = 0; i < 5; i++) {
			sut.newRequest();
			// emulate processing time
			Thread.sleep(1100);
		}
		long end = System.currentTimeMillis();

		// assert that 10 seconds have passed since we started making requests
		Assert.assertTrue("Took: " + (end - now), end - now >= 5500);

	}

	@Test
	public void testRequestsPerSecondController_program_misc_behaviour() throws InterruptedException {
		RequestsPerSecondController sut = new RequestsPerSecondController(1);

		long now = System.currentTimeMillis();

		sut.start();
		// we need to do 500 requests
		for (int i = 0; i < 1; i++) {
			sut.newRequest();
		}
		for (int i = 0; i < 3; i++) {
			sut.newRequest();
		}
		for (int i = 0; i < 1; i++) {
			sut.newRequest();
		}
		long end = System.currentTimeMillis();

		// assert that 10 seconds have passed since we started making requests
		final long took = end - now;
		Assert.assertTrue("Took: " + took, took >= 5000 && took < 5500);
	}
}
