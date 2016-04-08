package org.andresoviedo.util.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExpressionUtilsTest {

	ExpressionEngine sut;

	@Before
	public void before() {
		sut = new ExpressionEngine();
	}

	@After
	public void after() {
		sut = null;
	}

	@Test
	public void testReplaceVariables() {

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("hola", "buenas");
		vars.put("mundo", "andres");

		@SuppressWarnings("unchecked")
		final Map<String, String> ret = sut.evaluate(Collections.singletonMap("${hola}", "${mundo}"), vars);

		Assert.assertEquals("andres", ret.get("buenas"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEvalVariables() throws ScriptException {

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("hola", "buenas");
		vars.put("mundo", "andres");

		Map<String, String> ret = sut.evaluate(Collections.singletonMap("${hola}", "${mundo}"), vars);

		Assert.assertEquals("andres", ret.get("buenas"));

		// second round to see if results were cached
		vars = new HashMap<String, Object>();
		vars.put("hola", "hello");
		vars.put("mundo", "world");

		ret = sut.evaluate(Collections.singletonMap("${hola}", "${mundo}"), vars);

		Assert.assertEquals("world", ret.get("hello"));

		// third round. check every invocation has its context
		try {
			ret = sut.evaluate(Collections.singletonMap("${hola}", "${mundo}"), Collections.<String, Object>emptyMap());
			Assert.fail("This should have failed because we have nothing in the context");
		} catch (Exception ex) {
			Assert.assertEquals("Problem evaluating expression '${hola}'.", ex.getMessage());
			Assert.assertTrue(ex.getCause().getMessage(),
					ex.getCause().getMessage().startsWith("ReferenceError: \"hola\" is not defined"));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEvalSecondsFromEpoch() throws ScriptException {

		Map<String, Object> vars = new HashMap<String, Object>();

		Map<String, String> ret = sut.evaluate(Collections.singletonMap("seconds_from_epoch",
				"${Math.floor(new Date(2016,02,05,15,03,43,1).getTime() / 1000).toFixed(0)}"), vars);

		Assert.assertEquals("1457186623", ret.get("seconds_from_epoch"));

		ret = sut.evaluate(Collections.singletonMap("millis_from_epoch",
				"${new Date(2016,02,05,15,03,43,1).getTime().toFixed(0)}"), vars);

		Assert.assertEquals("1457186623001", ret.get("millis_from_epoch"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_generate_random_number() throws ScriptException {
		Map<String, Object> vars = new HashMap<String, Object>();

		Map<String, String> ret = sut.evaluate(Collections.singletonMap("random_number",
				"${('0000000'+(Math.random()*10000000).toFixed(0)).slice(-7)}"), vars);

		Assert.assertTrue(ret.get("random_number").matches("\\d{7}"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_string_functions() throws ScriptException {
		Map<String, Object> vars = new HashMap<String, Object>();

		// test substring(int)
		Map<String, String> ret = sut.evaluate(Collections.singletonMap("substring", "${'hola que tal'.substring(5)}"),
				vars);

		Assert.assertEquals("que tal", ret.get("substring"));

		// test substring(int,int)
		ret = sut.evaluate(Collections.singletonMap("substring", "${'hola que tal'.substring(5,8)}"), vars);

		Assert.assertEquals("que", ret.get("substring"));

		// test indexOf(char)
		ret = sut.evaluate(Collections.singletonMap("indexOf", "${'hola que tal'.indexOf('e')}"), vars);

		Assert.assertEquals("7", ret.get("indexOf"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_math_functions() throws ScriptException {
		Map<String, Object> vars = new HashMap<String, Object>();

		// test substring(int)
		Map<String, String> ret = sut.evaluate(Collections.singletonMap("math", "${3+7}"), vars);

		Assert.assertEquals("10", ret.get("math"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_multiple_functions() throws ScriptException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("hola", "hello");

		// test substring(int)
		Map<String, String> ret = sut
				.evaluate(Collections.singletonMap("multiple", "${((5+6)+'_'+hola).replace(/1/g,'100')}"), vars);

		Assert.assertEquals("100100_hello", ret.get("multiple"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_recursive_expression() throws ScriptException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("hola", "${mundo}");
		vars.put("mundo", "world");

		// test substring(int)
		Map<String, String> ret = sut
				.evaluate(Collections.singletonMap("multiple", "${((5+6)+'_'+hola).replace(/1/g,'100')}"), vars);

		Assert.assertEquals("100100_world", ret.get("multiple"));
	}

	@Test
	public void test_unescapeJson() {
		String json = "https:\\/\\/api.login.yahoo.com\\/oauth\\/v2\\/request_auth?&oauth_callback_confirmed=true&oauth_token=hmb9sqf&oauth_verifier=6751714&crumb=";

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("json", json);

		// test substring(int)
		@SuppressWarnings("unchecked")
		Map<String, String> ret = sut
				.evaluate(Collections.singletonMap("json_escaped", "${json.replace(/\\\\\\//g,'/')}"), vars);

		Assert.assertEquals(
				"https://api.login.yahoo.com/oauth/v2/request_auth?&oauth_callback_confirmed=true&oauth_token=hmb9sqf&oauth_verifier=6751714&crumb=",
				ret.get("json_escaped"));
	}

}
