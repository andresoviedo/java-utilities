package org.andresoviedo.util.bean;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BeanUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		BeanUtilsTest_Bean1 bean1 = new BeanUtilsTest_Bean1();

		BeanUtils.setProperty(bean1, "i", new Integer(10));
		Assert.assertEquals(10, bean1.i);
		Assert.assertEquals(10, BeanUtils.getProperty(bean1, "i"));

		BeanUtils.setProperty(bean1, "t", new Integer(10));
		Assert.assertEquals(new Integer(10), bean1.t);

		BeanUtils.setProperty(bean1, "bean2.bean3.t3", new Integer(10));
		Assert.assertEquals(new Integer(10), bean1.bean2.bean3.t3);
		Assert.assertEquals(new Integer(10), BeanUtils.getProperty(bean1, "bean2.bean3.t3"));

		BeanUtils.setProperty(bean1, "decimal", new BigDecimal(10000));
		Assert.assertEquals(new BigDecimal(10000), bean1.decimal);

		BeanUtils.setProperty(bean1, "bean2.decimal", new BigDecimal(10000));
		Assert.assertEquals(new BigDecimal(10000), bean1.bean2.decimal);

	}
}

class BeanUtilsTest_Bean1 {

	int i;
	String s;
	Integer t;
	BeanUtilsTest_Bean2 bean2;
	BigDecimal decimal;

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public Integer getT() {
		return t;
	}

	public void setT(Integer t) {
		this.t = t;
	}

	public BeanUtilsTest_Bean2 getBean2() {
		return bean2;
	}

	public void setBean2(BeanUtilsTest_Bean2 bean2) {
		this.bean2 = bean2;
	}

	public BigDecimal getDecimal() {
		return decimal;
	}

	public void setDecimal(BigDecimal decimal) {
		this.decimal = decimal;
	}

}

class BeanUtilsTest_Bean2 {

	int i2;
	String s2;
	Integer t2;
	BeanUtilsTest_Bean3 bean3;
	BigDecimal decimal;

	public int getI2() {
		return i2;
	}

	public void setI2(int i2) {
		this.i2 = i2;
	}

	public String getS2() {
		return s2;
	}

	public void setS2(String s2) {
		this.s2 = s2;
	}

	public Integer getT2() {
		return t2;
	}

	public void setT2(Integer t2) {
		this.t2 = t2;
	}

	public BeanUtilsTest_Bean3 getBean3() {
		return bean3;
	}

	public void setBean3(BeanUtilsTest_Bean3 bean3) {
		this.bean3 = bean3;
	}

	public BigDecimal getDecimal() {
		return decimal;
	}

	public void setDecimal(BigDecimal decimal) {
		this.decimal = decimal;
	}

}

class BeanUtilsTest_Bean3 {

	int i3;
	String s3;
	Integer t3;

	public int getI3() {
		return i3;
	}

	public void setI3(int i3) {
		this.i3 = i3;
	}

	public String getS3() {
		return s3;
	}

	public void setS3(String s3) {
		this.s3 = s3;
	}

	public Integer getT3() {
		return t3;
	}

	public void setT3(Integer t3) {
		this.t3 = t3;
	}

}
