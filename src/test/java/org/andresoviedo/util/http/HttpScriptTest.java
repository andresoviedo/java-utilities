package org.andresoviedo.util.http;

import java.util.Collections;
import java.util.Map;

import org.andresoviedo.util.http.HttpScript.HttpClient;
import org.andresoviedo.util.http.HttpScript.ResponseField;
import org.andresoviedo.util.http.HttpScript.Step;
import org.andresoviedo.util.tree.TreeNode;
import org.junit.Before;
import org.junit.Test;

public class HttpScriptTest {

	private final HttpScript httpScript = new HttpScript();

	@Before
	public void setUp() {

		// TODO: fluent API
		httpScript.addConfig(HttpClient.newInstance());

		TreeNode<Step> node = httpScript.addRequest(httpScript.new HttpRequest("start", "http://badoo.com/es/import",
				80, HttpScript.Method.GET));
		node.addChildren(httpScript.new RegexExtractor("regex_1", "rt", "\"rt\":\"(.+?)\",", ResponseField.BODY));
		System.out.println(node);

		node = httpScript.addRequest(httpScript.new HttpRequest("import", "http://badoo.com/es/import/?ws=1&rt=${rt}",
				80, HttpScript.Method.POST, "emails", "${emails}", "rt", "${rt}", "provider_id", "1001"));
		node.addChildren(httpScript.new RegexExtractor("regex_2", "ssid", "share_session_id=(\\d+)",
				ResponseField.HEADERS));
		System.out.println(node);

		node = httpScript.addRequest(httpScript.new HttpRequest("started", "http://badoo.com/es/import/started.phtml",
				80, HttpScript.Method.GET, "share_session_id", "${ssid}", "ws", "1", "rt", "${rt}"));
		System.out.println(node);

		node = httpScript.addRequest(httpScript.new HttpRequest("check", "http://badoo.com/es/import/check.phtml", 80,
				HttpScript.Method.GET, "ids", "${ssid}", "ws", "1", "rt", "${rt}"));
		node.addChildren(httpScript.new RegexExtractor("regex_3", "fullname",
				"\\Q<b class=\\\"block b\\\">\\E(.+)\\Q<\\/b>\\E.*Ya es usuario de Badoo", ResponseField.BODY));
		System.out.println(node);
	}

	@Test
	public void testExecute() {
		Map<String, Object> result = httpScript.execute(Collections.<String, Object> singletonMap("emails",
				"andresoviedo@gmail.com"));
		Map<String, Object> x = (Map<String, Object>) result.get("check");
		Map<String, Object> x2 = (Map<String, Object>) x.get("regex_3");
		System.out.println(x2.get("fullname"));

	}
}
