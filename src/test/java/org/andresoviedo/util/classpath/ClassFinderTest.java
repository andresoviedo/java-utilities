package org.andresoviedo.util.classpath;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClassFinderTest {

	public class SubConnector1 implements Connector {

	}

	public class SubConnector2 implements Connector {

	}

	public interface Connector {

	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Set<?> classes = new ClassFinder(Connector.class).getClasses();
		Assert.assertTrue(classes.remove(Connector.class.getName()));
		Assert.assertTrue(classes.remove(SubConnector1.class.getName()));
		Assert.assertTrue(classes.remove(SubConnector2.class.getName()));
	}
}
