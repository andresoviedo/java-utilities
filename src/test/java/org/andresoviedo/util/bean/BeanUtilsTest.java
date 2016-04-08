package org.andresoviedo.util.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

	@Test
	public void testReflectionToStringObject() {
		// System.out.println(ToStringBuilder.reflectionToString(new
		// SomeBeanWithAllKindOfAttributes()));
		// System.out.println(ReflectionToStringBuilder.toString(new
		// SomeBeanWithAllKindOfAttributes(),
		// new RecursiveToStringStyle(5)));
		// Date var_Date = new Date(0);
		final String reflectionToString = BeanUtils.reflectionToString(new SomeBeanWithAllKindOfAttributes(), false);
		System.out.println(reflectionToString);
		Assert.assertEquals(
				"org.andresoviedo.util.bean.SomeBeanWithAllKindOfAttributes [var_Parent=1,var_Parent2=2,var_String=Hola,var_Number=10,var_Boolean=true,var_List [var_List[0]=lista_item1,var_List[1]=lista_item2,],var_Date="
						+ new Date(0).toString()
						+ ",var_Contrato [entidad=<null>,dc=<null>,area=<null>,oficina=2100,modalidad=0630,contrato=1234567890,dcInterno=<null>,],var_ImporteDivisa [importe=500.0,divisa=COP,],var_int=100,var_Integer=200,var_Integer_array [var_Integer_array[0]=200,var_Integer_array[1]=250,],var_Collection [var_Collection[0]=col_item1,var_Collection[1]=col_item2,],m [m[key1] [a=namespace_bean,],m[key2]="
						+ new Date(0).toString()
						+ ",],var_int_array [var_int_array[0]=100,var_int_array[1]=150,],beanRef [a=namespace_bean,],locale [(java.util.Locale)=co_ES,],]",
				reflectionToString);
	}

	@Test
	public void testReflectionToStringObjectMultiline() {
		final String reflectionToString = BeanUtils.reflectionToString(new SomeBeanWithAllKindOfAttributes(), true);
		System.out.println(reflectionToString);
		Assert.assertEquals(
				"org.andresoviedo.util.bean.SomeBeanWithAllKindOfAttributes [\n\n\t\tvar_Parent=1\n\t\tvar_Parent2=2\n\t\tvar_String=Hola\n\t\tvar_Number=10\n\t\tvar_Boolean=true\n\t\tvar_List\n\t\t\t\tvar_List[0]=lista_item1\n\t\t\t\tvar_List[1]=lista_item2\n\t\tvar_Date="
						+ new Date(0).toString()
						+ "\n\t\tvar_Contrato\n\t\t\t\tentidad=<null>\n\t\t\t\tdc=<null>\n\t\t\t\tarea=<null>\n\t\t\t\toficina=2100\n\t\t\t\tmodalidad=0630\n\t\t\t\tcontrato=1234567890\n\t\t\t\tdcInterno=<null>\n\t\tvar_ImporteDivisa\n\t\t\t\timporte=500.0\n\t\t\t\tdivisa=COP\n\t\tvar_int=100\n\t\tvar_Integer=200\n\t\tvar_Integer_array\n\t\t\t\tvar_Integer_array[0]=200\n\t\t\t\tvar_Integer_array[1]=250\n\t\tvar_Collection\n\t\t\t\tvar_Collection[0]=col_item1\n\t\t\t\tvar_Collection[1]=col_item2\n\t\tm\n\t\t\t\tm[key1]\n\t\t\t\t\t\ta=namespace_bean\n\t\t\t\tm[key2]="
						+ new Date(0).toString()
						+ "\n\t\tvar_int_array\n\t\t\t\tvar_int_array[0]=100\n\t\t\t\tvar_int_array[1]=150\n\t\tbeanRef\n\t\t\t\ta=namespace_bean\n\t\tlocale(java.util.Locale)=co_ES]",
				reflectionToString);

	}

	@Test
	public void testRecursiveBean() {
		Assert.assertEquals(
				"org.andresoviedo.util.bean.SomeRecursiveBean [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [obj [... (continues)],],],],],],],],],],],],],],],],],],],],]",
				BeanUtils.reflectionToString(new SomeRecursiveBean(), false));
	}

	@Test
	public void testBeanHeredableDeHash() {
		GenericHashMapBean map = new GenericHashMapBean();
		Assert.assertEquals(
				"org.andresoviedo.util.bean.GenericHashMapBean [HMB [<parent>=[{prueba2=10, prueba=10}], field1=value_field1,],SB [var_Parent=1,var_Parent2=2,],]",
				BeanUtils.reflectionToString(map, false));
		Assert.assertEquals(
				"org.andresoviedo.util.bean.GenericHashMapBean [\n\n\t\tHMB\n\t\t\t\t<parent>=[{prueba2=10, prueba=10}]\n\t\t\t\tfield1=value_field1\n\t\tSB\n\t\t\t\tvar_Parent=1\n\t\t\t\tvar_Parent2=2]",
				BeanUtils.reflectionToString(map, true));
	}
}

class GenericHashMapBean {

	HashMapBean HMB;
	SomeBeanWithAllKindOfAttributesParent SB;

	public GenericHashMapBean() {
		HMB = new HashMapBean("prueba", 10);
		SB = new SomeBeanWithAllKindOfAttributesParent();
	}
}

class HashMapBean extends LinkedHashMap<String, Integer> {

	private static final long serialVersionUID = 1L;
	public String field1;

	public HashMapBean(String k, int v) {
		super(2);
		this.put(k + "2", v);
		this.put(k, v);

		field1 = "value_field1";
	}
}

class SomeRecursiveBean {
	Object obj = this;
}

class SomeBeanWithAllKindOfAttributesParent {
	int var_Parent = 1;
	String var_Parent2 = "2";
}

class SomeBeanWithAllKindOfAttributes extends SomeBeanWithAllKindOfAttributesParent {

	String var_String = "Hola";
	Number var_Number = new BigDecimal(10L);
	Boolean var_Boolean = Boolean.TRUE;
	List<Object> var_List = new ArrayList<Object>();
	Date var_Date = new Date(0);
	ContratoDeTest var_Contrato = new ContratoDeTest("2100", "0630", "1234567890");
	ImporteDivisaDeTest var_ImporteDivisa = new ImporteDivisaDeTest(500F, "COP");

	int var_int = 100;
	Integer var_Integer = 200;

	Integer[] var_Integer_array = new Integer[] { 200, 250 };
	Collection<Object> var_Collection = new ArrayList<Object>();
	Map<Object, Object> m = new LinkedHashMap<Object, Object>();
	int[] var_int_array = new int[] { 100, 150 };

	SomeAnotherBean beanRef = new SomeAnotherBean();

	Locale locale = new Locale("co", "ES");

	public SomeBeanWithAllKindOfAttributes() {
		super();
		m.put("key1", new SomeAnotherBean());
		m.put("key2", new Date(0));
		var_List.add("lista_item1");
		var_List.add("lista_item2");
		var_Collection.add("col_item1");
		var_Collection.add("col_item2");
	}
}

class SomeAnotherBean {
	String a = "namespace_bean";
}

// class RecursiveToStringStyle extends ToStringStyle {
//
// private static final int INFINITE_DEPTH = -1;
//
// /**
// * Setting {@link #maxDepth} to 0 will have the same effect as using original
// {@link #ToStringStyle}: it will print
// * all 1st level values without traversing into them. Setting to 1 will
// traverse up to 2nd level and so on.
// */
// private int maxDepth;
//
// private int depth;
//
// public RecursiveToStringStyle() {
// this(INFINITE_DEPTH);
// }
//
// public RecursiveToStringStyle(int maxDepth) {
// setUseShortClassName(true);
// setUseIdentityHashCode(false);
//
// this.maxDepth = maxDepth;
// }
//
// @Override
// protected void appendDetail(StringBuffer buffer, String fieldName, Object
// value) {
// if (value.getClass().getName().startsWith("java.lang.") || (maxDepth !=
// INFINITE_DEPTH && depth >= maxDepth)) {
// buffer.append(value);
// } else {
// depth++;
// buffer.append(ReflectionToStringBuilder.toString(value, this));
// depth--;
// }
// }
// }

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
