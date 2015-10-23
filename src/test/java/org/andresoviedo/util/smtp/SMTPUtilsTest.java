package org.andresoviedo.util.smtp;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class SMTPUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDispatch() {

		final String googleAppPwd = "<put_gapps_password_here>";

		Assert.assertTrue(SMTPUtils.dispatch("andresoviedo@gmail.com", "andresoviedo@gmail.com", "smtp.gmail.com", 587, "andresoviedo",
				googleAppPwd, true, "prueba sénding €m@il", "<html><body><h1>nuevo email!</h1>", "text/html; charset=utf-8", null));

		Assert.assertTrue(SMTPUtils.dispatch("andresoviedo@gmail.com", "andresoviedo@gmail.com", "smtp.gmail.com", 587, "andresoviedo",
				googleAppPwd, true, "prueba sénding €m@il with attachment", "<html><body><h1>nuevo email!</h1>",
				"text/html; charset=utf-8", new File("README.md")));

	}
}
