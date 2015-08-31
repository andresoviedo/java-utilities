package org.andresoviedo.util.jibx;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.junit.Test;

public class NumberJibxTest extends TestCase {
	@Test
	public void testSerializeNumber() {
		assertNull(NumberJibx.serializeNumber(null));
		assertEquals(NumberJibx.serializeNumber(new BigDecimal(3)), "3");
	}

	@Test
	public void testDeserializeNumber() {
		assertNull(NumberJibx.deserializeNumber(null));
		assertEquals(NumberJibx.deserializeNumber("3").intValue(), 3);
		assertEquals(NumberJibx.deserializeNumber("1234567890").intValue(), 1234567890);
		assertEquals(NumberJibx.deserializeNumber("-3.01").floatValue(), -3.01F);
		assertEquals(NumberJibx.deserializeNumber("-1234567890.0123456789012345").floatValue(), -1234567890.0123456789012345F);
	}
}
