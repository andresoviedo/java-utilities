package org.andresoviedo.util.bean;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class BeanWrapperTest {

	class TransferTO {

		private String pEjemplo = "pEjemploValue";
		private String pejemplo2 = "pejemplo2Value";
		private String PEJEMPLO3 = "PEJEMPLO3Value";

		public String getpEjemplo() {
			return pEjemplo;
		}

		public String getPejemplo2() {
			return pejemplo2;
		}

		public String getPEJEMPLO3() {
			return PEJEMPLO3;
		}

	}

	@Test
	public void testSupportedAttributes() {
		BeanWrapper beanw = new BeanWrapperImpl(new TransferTO());
		Object attributeValue = null;

		attributeValue = beanw.getPropertyValue("pEjemplo");

		attributeValue = beanw.getPropertyValue("pejemplo2");
		Assert.assertEquals("pejemplo2Value", attributeValue);

		attributeValue = beanw.getPropertyValue("PEJEMPLO3");
		Assert.assertEquals("PEJEMPLO3Value", attributeValue);
	}
}
