package org.andresoviedo.util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andresoviedo.util.tree.TreeNode;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public final class HttpScript {

	public enum ResponseField {
		BODY, HEADERS
	};

	public enum Method {
		GET, POST
	};

	private static final Logger LOG = Logger.getLogger(HttpScript.class);

	private static final Pattern VARIABLE_REGEX = Pattern.compile("\\$\\{(.*)\\}");

	private final Map<String, Object> executionVariables = new HashMap<String, Object>();

	private final Map<String, Object> configs = new HashMap<String, Object>();

	private final List<TreeNode<Step>> requests = new ArrayList<TreeNode<Step>>();

	public void addConfig(Object config) {
		configs.put(config.getClass().getSimpleName(), config);
	}

	public TreeNode<Step> addRequest(Step request) {
		TreeNode<Step> ret = new TreeNode<Step>(request);
		requests.add(ret);
		return ret;
	}

	public Map<String, Object> execute(Map<String, Object> userVariables) {
		LOG.info("Executing with '" + userVariables + "'");
		if (requests.isEmpty()) {
			throw new IllegalStateException("No requests to execute");
		}

		Map<String, Object> results = new HashMap<String, Object>();
		for (TreeNode<Step> request : requests) {
			results.put(request.getObject().getName(), execute_impl(request, userVariables, null));
		}
		return results;
	}

	private Object execute_impl(TreeNode<Step> stepNode, Map<String, Object> userVariables,
			Map<String, Object> parentResults) {
		Map<String, Object> result;
		try {
			result = stepNode.getObject().execute(userVariables, executionVariables, parentResults);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		result.put("class", stepNode.getObject().getClass().getSimpleName());
		if (stepNode.isLeaf()) {
			return result;
		}
		Map<String, Object> results = new HashMap<String, Object>();
		results.put(null, result);
		for (TreeNode<Step> child : stepNode.getChildren()) {
			results.put(child.getObject().getName(), execute_impl(child, userVariables, result));
		}
		return results;
	}

	public static Map<String, String> replaceVariables(Map<String, String> parameters, Map<String, Object>... variables) {
		if (parameters == null) {
			return null;
		}
		if (parameters.size() == 0) {
			return Collections.emptyMap();
		}

		Map<String, String> ret = new HashMap<String, String>(parameters);
		for (Map.Entry<String, String> entry : ret.entrySet()) {
			entry.setValue(replaceVariables(entry.getValue(), variables));
		}
		return Collections.unmodifiableMap(ret);
	}

	public static String replaceVariables(String value, Map<String, Object>... variables) {
		if (value == null) {
			return null;
		}
		if (variables == null || variables.length == 0) {
			return value;
		}

		String ret = value;
		Matcher m = VARIABLE_REGEX.matcher(value);
		while (m.find()) {
			String varName = m.group(1);
			for (Map<String, Object> variableMap : variables) {
				if (variableMap.containsKey(varName)) {
					ret = ret.replaceFirst("\\Q${" + varName + "}\\E",
							Matcher.quoteReplacement(variableMap.get(varName).toString()));
				}
			}
		}
		return ret;
	}

	private static Map<String, String> stringArrayToMap(String... params) {
		if (params == null) {
			return Collections.emptyMap();
		}
		if (params != null && params.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Parameters should be multiple of 2. Ex: '<param1>,<value1>,<param2>,<value2>,...'");
		}
		Map<String, String> ret = new HashMap<String, String>();
		for (int i = 0; i < params.length; i += 2) {
			if (params[i] == null || params[i + 1] == null) {
				throw new IllegalArgumentException("neither key nor value can be null");
			}
			ret.put(params[i], params[i + 1]);
		}
		return Collections.unmodifiableMap(ret);
	}

	public interface Step {
		String getName();

		Map<String, Object> execute(Map<String, Object> userVariables, Map<String, Object> executionVariables,
				Map<String, Object> parentResults) throws Exception;
	}

	public static final class HttpClient {

		private final org.apache.http.client.HttpClient client;
		{
			// RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build();
			// CookieStore cookieStore = new BasicCookieStore();
			// context = HttpClientContext.create();
			// context.setCookieStore(cookieStore);

			// httpClient =
			// HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore)
			// .build();
			client = HttpClients.createDefault();
		}

		public static HttpClient newInstance() {
			return new HttpClient();
		}

		public org.apache.http.client.HttpClient getClient() {
			return client;
		}

		// httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		// httpClient.getParams().setParameter("http.protocol.single-cookie-header", Boolean.TRUE);
	}

	public final class RegexExtractor implements Step {

		private final Logger LOG = Logger.getLogger(RegexExtractor.class);

		private final Pattern CONTENT_TYPE_REGEX = Pattern.compile("(.*);(.*)");

		private final String varName;

		private final Pattern regex;

		private final ResponseField responseField;

		private final String name;

		public RegexExtractor(String name, String varName, String regex, ResponseField responseField) {
			super();
			this.varName = varName;
			this.regex = Pattern.compile(regex);
			this.responseField = responseField;
			this.name = name;
		}

		public Object execute(Object o) {
			throw new UnsupportedOperationException("not yet implemented");
		}

		@SuppressWarnings("unchecked")
		public Map<String, Object> execute(Map<String, Object> userVariables, Map<String, Object> executionVariables,
				Map<String, Object> parentResults) throws UnsupportedEncodingException {
			LOG.debug("Executing '" + regex + "' with '" + userVariables + "', " + executionVariables);
			if (parentResults == null) {
				throw new IllegalStateException("parent is null");
			}

			Object parentClass = parentResults.get("class");
			if (!(parentClass instanceof String) || !HttpRequest.class.getSimpleName().equals(parentClass)) {
				throw new IllegalStateException("unknow source '" + parentClass + "'");
			}

			// TODO: Look in subsamples?

			String body = null;
			Map<String, String> headers = null;

			Map<String, Object> ret = new HashMap<String, Object>();
			switch (responseField) {
			case BODY: {
				if (!parentResults.containsKey("responseBody")) {
					throw new IllegalArgumentException("No body to extract regexp");
				}
				headers = (Map<String, String>) parentResults.get("headers");
				String contentTypeHeader = headers != null ? headers.get("content-type") : null;
				@SuppressWarnings("unused")
				String contentType = "text/html";
				String charset = "UTF-8"; // default
				if (contentTypeHeader != null) {
					Matcher m = CONTENT_TYPE_REGEX.matcher(contentTypeHeader);
					if (m.find()) {
						contentType = m.group(1);
						contentTypeHeader = m.group(2);
					}
				}
				body = new String((byte[]) parentResults.get("responseBody"), charset);
				Matcher m = regex.matcher(body);
				int i = 0;
				if (m.find()) {
					ret.put(varName + (m.groupCount() == 1 ? "" : "_" + i), m.group(1));
					while (m.find()) {
						// TODO: inform user that we only support 1 group capture in case multiple capturing
						// groups were specified
						ret.put(varName + (m.groupCount() == 1 ? "" : "_" + i), m.group(1));
					}
				}
				break;
			}
			case HEADERS: {
				if (!parentResults.containsKey("headers")) {
					throw new IllegalArgumentException("No headers to extract regexp");
				}
				headers = (Map<String, String>) parentResults.get("headers");
				List<String> captures = new ArrayList<String>(1);
				for (Map.Entry<String, String> header : headers.entrySet()) {
					// TODO: inform user we only support looking regex in header values (not keys)
					Matcher m = regex.matcher(header.getValue());
					while (m.find()) {
						captures.add(m.group(1));
					}
				}
				if (captures.size() == 1) {
					ret.put(varName, captures.get(0));
				} else if (captures.size() > 1) {
					for (int i = 0; i < captures.size(); i++) {
						ret.put(varName + "_" + i, captures.get(i));
					}
				}
				break;
			}
			default:
				throw new UnsupportedOperationException(responseField.toString());
			}

			if (ret.isEmpty()) {
				LOG.warn("Regexp not found. regex '" + regex + "', "
						+ (responseField == ResponseField.BODY ? body : headers));
			} else {
				LOG.info("Regexp found. regex '" + regex + "', captures '" + ret + "'");
			}

			executionVariables.putAll(ret);

			return ret;

		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "RegexExtractor [name=" + name + ", regex=" + regex + ", varName=" + varName + "]";
		}

	}

	public final class HttpRequest implements Step {

		private final Logger LOG = Logger.getLogger(HttpRequest.class);

		private final String url;

		private final int portNumber;

		private final Method method;

		private final Map<String, String> parameters;

		private final String name;

		public HttpRequest(String name, String url, int portNumber, Method method) {
			this(name, url, portNumber, method, (String[]) null);
		}

		public HttpRequest(String name, String url, int portNumber, Method method, String... params) {
			this(name, url, portNumber, method, stringArrayToMap(params));
		}

		public HttpRequest(String name, String url, int portNumber, Method method, Map<String, String> params) {
			this.url = url;
			this.portNumber = portNumber;
			this.method = method;
			this.parameters = params;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}

		public int getPortNumber() {
			return portNumber;
		}

		public Method getMethod() {
			return method;
		}

		public Map<String, String> getParameters() {
			if (parameters == null) {
				return null;
			}
			return Collections.unmodifiableMap(parameters);
		}

		public Map<String, Object> execute(Map<String, Object> userVariables, Map<String, Object> executionVariables,
				Map<String, Object> parentResults) throws HttpException, IOException {
			LOG.debug("Executing '" + url + "' with '" + userVariables + "', " + executionVariables);

			Map<String, Object> ret = new HashMap<String, Object>();

			HttpRequestBase httpMethod;
			@SuppressWarnings("unchecked")
			String url_interpolated = replaceVariables(this.url, executionVariables, userVariables);
			@SuppressWarnings("unchecked")
			Map<String, String> interpolatedParameters = replaceVariables(parameters, executionVariables, userVariables);

			if (!this.url.equals(url_interpolated)) {
				LOG.debug("Interpolated url '" + url + "'==>'" + url_interpolated + "'...");
			}
			if (!parameters.equals(interpolatedParameters)) {
				LOG.debug("Interpolated parameters '" + parameters + "'==>'" + interpolatedParameters + "'...");
			}

			switch (method) {
			case GET: {
				List<NameValuePair> nvps = null;
				if (!parameters.isEmpty()) {
					nvps = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> param : interpolatedParameters.entrySet()) {
						nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
					}
				}
				String querystring = nvps != null ? URLEncodedUtils.format(nvps, "UTF-8") : null;
				httpMethod = new HttpGet(querystring != null ? url_interpolated + "?" + querystring : url_interpolated);
				break;
			}
			case POST: {
				httpMethod = new HttpPost(url_interpolated);
				if (!parameters.isEmpty()) {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> param : interpolatedParameters.entrySet()) {
						nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
					}
					((HttpPost) httpMethod).setEntity(new UrlEncodedFormEntity(nvps));
				}
				break;
			}
			default:
				// Without this we should not have compilation errors, but we do
				throw new IllegalArgumentException();
			}

			HttpResponse response = ((HttpClient) configs.get(HttpClient.class.getSimpleName())).getClient().execute(
					httpMethod);
			int statusCode = response.getStatusLine().getStatusCode();
			ret.put("statusCode", statusCode);
			ret.put("statusLine", response.getStatusLine().getReasonPhrase());

			HttpEntity entity = response.getEntity();
			byte[] responseBody = EntityUtils.toByteArray(entity);
			ret.put("responseBody", responseBody);

			Map<String, String> headers = new HashMap<String, String>();
			for (Header header : response.getAllHeaders()) {
				headers.put(header.getName().toLowerCase(), header.getValue());
			}
			ret.put("headers", headers);

			httpMethod.releaseConnection();

			try {
				return ret;
			} finally {
				if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY && response.getHeaders("Location") != null
						&& response.getHeaders("Location").length >= 0) {
					String location = response.getHeaders("Location")[0].getValue();
					LOG.debug("Redirecting to '" + location + "'");
					HttpRequest httpRedirect = new HttpRequest(null, location, portNumber, Method.GET);
					ret.put("subsample", httpRedirect.execute(userVariables, executionVariables, ret));
				}
			}
		}

		@Override
		public String toString() {
			return "HttpRequest [name=" + name + ", url=" + url + ", method=" + method + "]";
		}
	}
}
