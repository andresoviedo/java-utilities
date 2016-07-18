package org.andresoviedo.util.configuration;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationUtilsTest {

	@Test
	public void testBindConfiguration() {
		Params params = new Params();
		ConfigurationUtils.propertiesToPojo(params,
			this.getClass().getResource("/org/andresoviedo/util/configuration/configuration.properties").getFile(), "rtb.");

		Assert.assertEquals(111.0, params.maxCpcOldUser, 0f);
		Assert.assertEquals(222.0, params.maxCpcRegistered, 0f);
		Assert.assertEquals(333.0, params.maxCpcSolicitar, 0f);
		Assert.assertEquals(444.0, params.maxCpc2Url, 0f);
		Assert.assertEquals(555.0, params.maxCpc1UrlNotPop, 0f);
		Assert.assertTrue(params.onlySets);
		Assert.assertEquals(54321, params.setHours);
		Assert.assertEquals(12345, params.deleteHours);
		Assert.assertEquals("api.host.com", params.host);
		Assert.assertEquals(new File("/path/to/reports"), params.reportsFolder);
	}
}


class Params {

	/**
	 * specifies a period of time where unique visitors will be selected to no longer be retargetted
	 */
	int deleteHours = 24;
	/**
	 * specifies a period of time where unique visitors will be selected to be retargetted
	 */
	int setHours = 24;
	/**
	 * if true then no deletes are sent
	 */
	boolean onlySets = false;
	/**
	 * Reports folder
	 */
	File reportsFolder;
	/**
	 * MaxCpc for old
	 */
	double maxCpcOldUser;
	/**
	 * MaxCpc for registered
	 */
	double maxCpcRegistered;
	/**
	 * MaxCpc for user visited solicitar
	 */
	double maxCpcSolicitar;
	/**
	 * MaxCpc for 2 url (not solicitar)
	 */
	double maxCpc2Url;
	/**
	 * MaxCpc for 1 url not pop
	 */
	double maxCpc1UrlNotPop;
	/**
	 * Host
	 */
	String host = "default.host.com";
}
