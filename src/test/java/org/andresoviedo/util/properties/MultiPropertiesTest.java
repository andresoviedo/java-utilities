package org.andresoviedo.util.properties;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class MultiPropertiesTest {

	@Test
	public void testGetPropertyString() {
		final Properties defaults = new Properties();
		Properties sut = new MultiProperties(defaults);
		Assert.assertNull(sut.getProperty("key1"));

		// should work without configuration
		defaults.put("key1", "value1");
		sut = new MultiProperties(defaults);
		Assert.assertEquals("value1", sut.getProperty("key1"));

		// should work with environment specified
		defaults.put("environment", "junit");
		defaults.put("[junit].key1", "value1-for-junit");
		sut = new MultiProperties(defaults);
		Assert.assertEquals("value1-for-junit", sut.getProperty("key1"));

		// should work when environment & cluster specified
		defaults.put("cluster.name", "test1");
		defaults.put("[junit#test1].key1", "value1-for-junit-test1");
		sut = new MultiProperties(defaults);
		Assert.assertEquals("value1-for-junit-test1", sut.getProperty("key1"));

	}

}
