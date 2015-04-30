package org.andresoviedo.util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

// TODO: set http connection timeouts
public class HttpScript {

	public enum ResponseField {
		BODY, HEADERS
	};

	public enum Method {
		GET, POST, PUT, DELETE
	};

	public enum Lookup {
		MAIN, SUBSAMPLES
	};

	private static final Logger LOG = Logger.getLogger(HttpScript.class);

	// TODO: Test this regexp agains multiple variables
	private static final Pattern VARIABLE_REGEX = Pattern.compile("\\$\\{([^\\$]+?)\\}");

	private static final Pattern FUNCTION_REGEX = Pattern.compile("^__func\\((.+)\\)$");

	private static final Pattern STRING_FUNCTION_2_ARGS = Pattern.compile("^'(.+?)'\\.(.+?)\\((.+?),(.+?)\\)$");

	private static final Pattern STRING_FUNCTION_0_ARGS = Pattern.compile("^'(.+?)'\\.(.+?)\\(\\)$");

	private final Map<String, Object> configs = new HashMap<String, Object>();

	private final Map<String, Object> executionVariables = new HashMap<String, Object>();

	private final List<Node<Step>> steps = new ArrayList<Node<Step>>();

	public void addConfig(Object config) {
		configs.put(config.getClass().getSimpleName(), config);
	}

	public Node<Step> addRequest(Step request) {
		Node<Step> ret = new Node<Step>(request);
		steps.add(ret);
		return ret;
	}

	public Map<String, Object> execute(Map<String, Object> userVariables) {
		if (steps.isEmpty()) {
			throw new IllegalStateException("No steps to execute");
		}

		// Init user variables
		executionVariables.clear();
		if (configs.containsKey(HashMap.class.getSimpleName())) {
			executionVariables.putAll(replaceVariables(
					(Map<String, Object>) configs.get(HashMap.class.getSimpleName()), getSystemAndEnvPropertiesMap()));
		}
		executionVariables.putAll(replaceVariables(userVariables, executionVariables, getSystemAndEnvPropertiesMap()));
		LOG.debug("Execution variables: " + executionVariables);

		// Clear cookies
		((HttpClient) configs.get(HttpClient.class.getSimpleName())).clearCookies();

		Map<String, Object> results = new HashMap<String, Object>();
		for (Node<Step> step : steps) {
			try {
				Map<String, Object> execute_impl = execute_impl(step, null);
				System.out.println("Putting " + step.getData().getName() + ", value: " + execute_impl);
				results.put(step.getData().getName(), execute_impl);
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
				results.put(step.getData().getName(), ex);
				break;
			}
		}
		return results;
	}

	private static Map<String, Object> getSystemAndEnvPropertiesMap() {
		Map<String, Object> systemProps = new HashMap<String, Object>();
		systemProps.putAll(System.getenv());
		for (Entry<Object, Object> x : System.getProperties().entrySet()) {
			systemProps.put(x.getKey().toString(), x.getValue());
		}
		return systemProps;
	}

	private Map<String, Object> execute_impl(Node<Step> stepNode, Map<String, Object> parentResults) {

		Map<String, Object> results = new HashMap<String, Object>();
		if (stepNode.getData() instanceof ForEach) {
			try {
				while (stepNode.getData().execute(parentResults) != null) {
					for (Node<Step> child : stepNode.getChildren()) {
						results.put(child.getData().getName(), execute_impl(child, parentResults));
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException("Exeption executing for '" + stepNode.getData().getName() + "'", ex);
			}
			return results;
		} else if (stepNode.getData() instanceof While) {
			try {
				while ((Boolean) stepNode.getData().execute(parentResults).get("result")) {
					for (Node<Step> child : stepNode.getChildren()) {
						results.put(child.getData().getName(), execute_impl(child, parentResults));
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException("Exeption executing while '" + stepNode.getData().getName() + "'. "
						+ ex.getMessage(), ex);
			}
			return results;
		}

		Map<String, Object> result;
		try {
			result = stepNode.getData().execute(parentResults);
		} catch (Exception ex) {
			LOG.fatal("Exception executing '" + stepNode.getData().getName() + "'", ex);
			throw new RuntimeException("Exeption executing '" + stepNode.getData().getName() + "'", ex);
		}

		result.put("class", stepNode.getData().getClass().getSimpleName());
		if (stepNode.isLeaf()) {
			return result;
		}

		results.put(null, result);
		for (Node<Step> child : stepNode.getChildren()) {
			results.put(child.getData().getName(), execute_impl(child, result));
		}

		return results;
	}

	public static <T> Map<String, T> replaceVariables(Map<String, T> parameters, Map<String, Object>... variables) {
		if (parameters == null) {
			return null;
		}
		if (parameters.size() == 0) {
			return Collections.emptyMap();
		}

		Map<String, T> ret = new HashMap<String, T>();
		for (Map.Entry<String, T> entry : parameters.entrySet()) {
			if (entry.getValue() instanceof String) {
				ret.put(replaceVariables((String) entry.getKey(), variables),
						((T) replaceVariables((String) entry.getValue(), variables)));
			} else {
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		return Collections.unmodifiableMap(ret);
	}

	public static String replaceVariables(String value, Map<String, Object>... variables) {
		LOG.trace("Replacing variables for '" + value + "'.... variables '" + Arrays.toString(variables) + "'");
		if (value == null) {
			return null;
		}
		if (variables == null || variables.length == 0) {
			return value;
		}

		String ret = value;
		Matcher m = VARIABLE_REGEX.matcher(value);
		while (m.find()) {
			String match = m.group(0);
			String varName = m.group(1);

			if ("__func(SECONDS_FROM_EPOCH)".equals(varName)) {
				String secondsFromEpoch = String.valueOf(System.currentTimeMillis() / 1000);
				LOG.trace("Replacing '" + match + "' with seconds from epoch '" + secondsFromEpoch + "'...");
				// ret = ret.replaceFirst(varName, String.valueOf(System.currentTimeMillis() / 1000));
				ret = ret.replaceFirst(Pattern.quote(match), secondsFromEpoch);
				continue;
			}

			if ("__func(RANDOM(7))".equals(varName)) {
				String randomString = UUID.randomUUID().toString().substring(0, 7);
				LOG.trace("Replacing '" + match + "' with random string '" + randomString + "'...");
				// ret = ret.replaceFirst(varName, String.valueOf(System.currentTimeMillis() / 1000));
				ret = ret.replaceFirst(Pattern.quote(match), randomString);
				continue;
			}

			Matcher m2 = FUNCTION_REGEX.matcher(varName);
			if (!m2.find()) {
				String replacement = null;
				for (Map<String, Object> variableMap : variables) {
					if (variableMap.containsKey(varName)) {
						LOG.trace("Replacing '" + match + "' with '" + variableMap.get(varName) + "'...");
						replacement = Matcher.quoteReplacement(variableMap.get(varName).toString());
						ret = ret.replaceFirst(Pattern.quote(match), replacement);
					}
				}
				continue;
			}

			varName = m2.group(1);
			Matcher m3 = STRING_FUNCTION_2_ARGS.matcher(varName);
			if (m3.find()) {
				LOG.trace("Replacing '" + match + "' with function (2 args) '" + varName + "'...");
				String funcValue = m3.group(1);
				String function = m3.group(2);
				Object[] functionArgs = new Integer[] { new Integer(m3.group(3)), new Integer(m3.group(4)) };
				if ("substring".equals(function)) {
					ret = ret.replaceFirst(Pattern.quote(match),
							funcValue.substring((Integer) functionArgs[0], (Integer) functionArgs[1]));
				} else {
					throw new IllegalArgumentException("Function '" + function + "' not supported");
				}
				continue;
			}

			Matcher f0 = STRING_FUNCTION_0_ARGS.matcher(varName);
			if (f0.find()) {
				LOG.trace("Replacing '" + match + "' with function (0 args) '" + varName + "'...");
				String funcValue = f0.group(1);
				String function = f0.group(2);
				if ("decode".equals(function)) {
					try {
						ret = ret.replaceFirst(Pattern.quote(match), URLDecoder.decode(funcValue, "UTF-8"));
					} catch (UnsupportedEncodingException ex) {
						throw new RuntimeException(ex);
					}
				} else if ("unescapeJson".equals(function)) {
					ret = ret.replaceFirst(Pattern.quote(match), funcValue.replaceAll("\\/", "/"));
				} else if ("hashCode".equals(function)) {
					ret = ret.replaceFirst(Pattern.quote(match), String.valueOf(funcValue.hashCode()));
				} else {
					throw new IllegalArgumentException("Function '" + function + "' not supported");
				}
				continue;
			}

			throw new IllegalArgumentException("Function '" + varName + "' not supported");
		}

		return VARIABLE_REGEX.matcher(ret).find() && !value.equals(ret) ? replaceVariables(ret, variables) : ret;
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

		Map<String, Object> execute(Map<String, Object> parentResults) throws Exception;
	}

	public static final class HttpClient {

		private static final int HTTP_CONNECT_TIMEOUT = 60000;
		private static final int HTTP_SOCKET_TIMEOUT = 60000;
		private static final int HTTP_POOL_TIMEOUT = 2000;

		private final org.apache.http.client.HttpClient client;
		private final CookieStore cookieStore;

		{
			RequestConfig globalConfig = RequestConfig.custom().setConnectTimeout(HTTP_CONNECT_TIMEOUT)
					.setSocketTimeout(HTTP_SOCKET_TIMEOUT).setConnectionRequestTimeout(HTTP_POOL_TIMEOUT)
					.setCookieSpec(CookieSpecs.STANDARD).setRedirectsEnabled(false).build();
			cookieStore = new BasicCookieStore();
			// HttpClientContext context = HttpClientContext.create();
			// context.setCookieStore(cookieStore);
			client = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore)
					.build();
			// client = HttpClients.createDefault();
		}

		public static HttpClient newInstance() {
			return new HttpClient();
		}

		public org.apache.http.client.HttpClient getClient() {
			return client;
		}

		public void clearCookies() {
			cookieStore.clear();
		}

		// httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		// httpClient.getParams().setParameter("http.protocol.single-cookie-header", Boolean.TRUE);
	}

	public final class RegexExtractor implements Step {

		private final Pattern CONTENT_TYPE_REGEX = Pattern.compile("(.*);(.*)");

		private final String varName;

		private final Pattern regex;

		private final ResponseField responseField;

		private final String name;

		private final Lookup lookup;

		@SuppressWarnings("unused")
		private final int matchNr;

		private final boolean failIfNotFound;

		public RegexExtractor(String name, String varName, String regex, ResponseField responseField) {
			this(name, varName, regex, responseField, Lookup.MAIN, 0, true);
		}

		public RegexExtractor(String name, String varName, String regex, ResponseField responseField, Lookup lookup) {
			this(name, varName, regex, responseField, lookup, 0, true);
		}

		public RegexExtractor(String name, String varName, String regex, ResponseField responseField, Lookup lookup,
				int matchNr) {
			this(name, varName, regex, responseField, lookup, matchNr, true);
		}

		public RegexExtractor(String name, String varName, String regex, ResponseField responseField, Lookup lookup,
				int matchNr, boolean failIfNotFound) {
			super();
			this.varName = varName;
			this.regex = Pattern.compile(regex);
			this.responseField = responseField;
			this.name = name;
			this.lookup = lookup;
			this.matchNr = matchNr;
			this.failIfNotFound = failIfNotFound;
		}

		public Object execute(Object o) {
			throw new UnsupportedOperationException("not yet implemented");
		}

		@SuppressWarnings("unchecked")
		public Map<String, Object> execute(Map<String, Object> parentResults) throws UnsupportedEncodingException {
			LOG.debug("[regexp-" + getName() + "] Executing... regex '" + regex + "'");

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
				List<byte[]> responseBodies = new ArrayList<byte[]>();
				switch (lookup) {
				case MAIN:
					if (!parentResults.containsKey("responseBody")) {
						throw new IllegalArgumentException("No body to extract regexp");
					}
					responseBodies.add((byte[]) parentResults.get("responseBody"));
					break;
				case SUBSAMPLES:
					if (!parentResults.containsKey("subsample")) {
						throw new IllegalArgumentException("No subsamples to extract regex. parentResults '"
								+ parentResults + "'");
					}
					Map<String, Object> currentSubSample = (Map<String, Object>) parentResults.get("subsample");
					do {
						if (!currentSubSample.containsKey("responseBody")) {
							throw new IllegalArgumentException("No body (in subsample) to extract regexp. subsample '"
									+ currentSubSample + "'");
						}
						responseBodies.add((byte[]) currentSubSample.get("responseBody"));
						currentSubSample = (Map<String, Object>) currentSubSample.get("subsample");
					} while (currentSubSample != null);
					break;
				default:
					throw new UnsupportedOperationException("Not implemented '" + lookup + "'");
				}

				headers = (Map<String, String>) parentResults.get("headers");
				String contentTypeHeader = headers != null ? headers.get("content-type") : null;
				String contentType = "text/html";
				String charset = "UTF-8"; // default
				if (contentTypeHeader != null) {
					Matcher m = CONTENT_TYPE_REGEX.matcher(contentTypeHeader);
					if (m.find()) {
						contentType = m.group(1);
						contentTypeHeader = m.group(2);
					}
				}
				int j = 0;
				for (int i = 0; i < responseBodies.size(); i++) {
					byte[] responseBody = responseBodies.get(i);
					// LOG.debug("Looking for regex in body '" + i + "'");
					body = new String(responseBody, charset);
					Matcher m = regex.matcher(body);
					if (m.find()) {
						// always add default result
						if (j == 0) {
							ret.put(varName, m.group(1)); // TODO: review this. I need this now tu support ForEach.
						}
						ret.put(varName + "_" + j++, m.group(1)); // TODO: review this. I need this now to
																	// support ForEach.
						// ret.put(varName + (m.groupCount() == 1 ? "" : "_" + i), m.group(1));
						while (m.find()) {
							// TODO: inform user that we only support 1 group capture in case multiple capturing
							// groups were specified
							ret.put(varName + "_" + j++, m.group(1));
						}
					}
				}
				break;
			}
			case HEADERS: {
				List<String> allHeaders = new ArrayList<String>();
				switch (lookup) {
				case MAIN:
					if (!parentResults.containsKey("headers")) {
						throw new IllegalArgumentException("No headers to extract regexp");
					}
					headers = (Map<String, String>) parentResults.get("headers");
					for (Map.Entry<String, String> entry : headers.entrySet()) {
						allHeaders.add(entry.getKey() + ": " + entry.getValue());
					}

					break;
				case SUBSAMPLES:
					if (!parentResults.containsKey("subsample")) {
						throw new IllegalArgumentException("No subsamples to extract regex. parentResults '"
								+ parentResults + "'");
					}
					Map<String, Object> currentSubSample = (Map<String, Object>) parentResults.get("subsample");
					do {
						if (!currentSubSample.containsKey("headers")) {
							throw new IllegalArgumentException("No headers to extract regexp");
						}
						headers = (Map<String, String>) currentSubSample.get("headers");
						for (Map.Entry<String, String> entry : headers.entrySet()) {
							allHeaders.add(entry.getKey() + ": " + entry.getValue());
						}
						currentSubSample = (Map<String, Object>) currentSubSample.get("subsample");
					} while (currentSubSample != null);
					break;
				default:
					throw new UnsupportedOperationException("Not implemented '" + lookup + "'");
				}
				List<String> captures = new ArrayList<String>(1);
				for (String header : allHeaders) {
					// TODO: inform user we only support looking regex in header values (not keys)
					Matcher m = regex.matcher(header);
					while (m.find()) {
						captures.add(m.group(1));
					}
				}
				if (captures.size() == 1) {
					ret.put(varName, captures.get(0));
					ret.put(varName + "_0", captures.get(0));
				} else if (captures.size() > 1) {
					ret.put(varName, captures.get(0));
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
				String msg = "[regexp-" + getName() + "] Not found! regex '" + regex + "', "
						+ (responseField == ResponseField.BODY ? "BODY: " + body : "HEADERS: " + headers);
				LOG.warn(msg);
				if (failIfNotFound) {
					throw new RuntimeException(msg);
				}
			} else {
				LOG.info("[regexp-" + getName() + "] Found: regex '" + regex + "', captures '" + ret + "'");
			}

			executionVariables.putAll(ret);

			return ret;

		}

		public String getName() {
			return name;
		}

	}

	public final class HttpRequest implements Step {

		private final String url;

		private final int portNumber;

		private final Method method;

		private final Map<String, String> parameters;

		private final String name;

		private final Map<String, String> headers;

		private final Object body;

		private boolean followRedirects = true;

		public HttpRequest(String name, String url, int portNumber, Method method) {
			this(name, url, portNumber, method, (String[]) null);
		}

		public HttpRequest(String name, String url, int portNumber, Method method, String... params) {
			this(name, url, portNumber, method, stringArrayToMap(params), null);
		}

		public HttpRequest(String name, String url, int portNumber, Method method, Map<String, String> params) {
			this(name, url, portNumber, method, params, Collections.<String, String> emptyMap());
		}

		public HttpRequest(String name, String url, int portNumber, Method method, Map<String, String> params,
				Map<String, String> headers) {
			this.url = url;
			this.portNumber = portNumber;
			this.method = method;
			this.parameters = params != null ? Collections.unmodifiableMap(params) : null;
			this.body = null;
			this.name = name;
			this.headers = headers != null ? Collections.unmodifiableMap(headers) : null;
		}

		public HttpRequest(String name, String url, int portNumber, Method method, Object body,
				Map<String, String> headers) {
			this.url = url;
			this.portNumber = portNumber;
			this.method = method;
			this.parameters = null;
			this.body = body;
			this.name = name;
			this.headers = headers != null ? Collections.unmodifiableMap(headers) : null;
		}

		public HttpRequest setFollowRedirects(boolean doFollow) {
			this.followRedirects = doFollow;
			return this;
		}

		public String getName() {
			return name;
		}

		public String getHost() {
			return url;
		}

		public int getPortNumber() {
			return portNumber;
		}

		public Method getMethod() {
			return method;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		@SuppressWarnings("unchecked")
		public Map<String, Object> execute(Map<String, Object> parentResults) throws HttpException, IOException {

			LOG.debug("[http-" + getName() + "] Executing... url '" + this.url + "', headers '" + headers
					+ "', executionVariables '" + executionVariables + "'");

			Map<String, Object> ret = new HashMap<String, Object>();

			HttpRequestBase httpMethod;
			String url_interpolated = replaceVariables(this.url, executionVariables);
			Map<String, String> parameters = replaceVariables(getParameters(), executionVariables);

			if (!this.url.equals(url_interpolated)) {
				LOG.debug("[http-" + getName() + "] Url '" + url_interpolated + "'...");
			}
			if (parameters != null && !parameters.isEmpty()) {
				LOG.debug("[http-" + getName() + "] Parameters '" + parameters + "'...");
			}

			switch (method) {
			case GET: {
				List<NameValuePair> nvps = null;
				if (getParameters() != null && !getParameters().isEmpty()) {
					nvps = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> param : parameters.entrySet()) {
						nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
					}
				}
				String querystring = nvps != null ? URLEncodedUtils.format(nvps, "UTF-8") : null;
				httpMethod = new HttpGet(querystring != null ? url_interpolated + "?" + querystring : url_interpolated);
				break;
			}
			case POST: {
				httpMethod = new HttpPost(url_interpolated);
				if (parameters != null && !parameters.isEmpty()) {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> param : parameters.entrySet()) {
						nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
					}
					((HttpPost) httpMethod).setEntity(new UrlEncodedFormEntity(nvps));
				}
				break;
			}
			case PUT:
				httpMethod = new HttpPut(url_interpolated);
				if (parameters != null && !parameters.isEmpty()) {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> param : parameters.entrySet()) {
						nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
					}
					((HttpPut) httpMethod).setEntity(new UrlEncodedFormEntity(nvps));
				}
				if (body != null) {
					if (body instanceof String) {
						((HttpPut) httpMethod).setEntity(new StringEntity((replaceVariables(body.toString(),
								executionVariables))));
					}
				}
				break;
			case DELETE:
				httpMethod = new HttpDelete(url_interpolated);
				if (parameters != null && !parameters.isEmpty()) {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> param : parameters.entrySet()) {
						nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
					}
				}

				break;
			default:
				// Without this we should not have compilation errors, but we do
				throw new IllegalArgumentException();
			}

			// INFO: Some web servers fails if it detects we are a robot.
			httpMethod
					.addHeader("User-Agent",
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36");
			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					httpMethod.addHeader(entry.getKey(), replaceVariables(entry.getValue(), executionVariables));
				}
			}

			HttpResponse response = ((HttpClient) configs.get(HttpClient.class.getSimpleName())).getClient().execute(
					httpMethod);
			int statusCode = response.getStatusLine().getStatusCode();
			String reasonPhrase = response.getStatusLine().getReasonPhrase();

			LOG.debug("[http-" + getName() + "] Response: statusCode '" + statusCode + "', responseMsg '"
					+ reasonPhrase + "'");

			ret.put("statusCode", statusCode);
			ret.put("statusLine", reasonPhrase);

			HttpEntity entity = response.getEntity();
			ret.put("responseBody", entity != null ? EntityUtils.toByteArray(entity) : null);

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
						&& response.getHeaders("Location").length >= 0 && followRedirects) {
					String location = response.getHeaders("Location")[0].getValue();
					LOG.debug("[http-" + getName() + "] Redirecting to '" + location + "'");
					HttpRequest httpRedirect = new HttpRequest(getName() + "-redirect", location, portNumber,
							Method.GET);
					ret.put("subsample", httpRedirect.execute(ret));
				}
			}
		}
	}

	public final class ForEach implements Step {

		private final String name;

		private final String inputVariable;

		private final String outputVariable;

		public ForEach(String name, String inputVariable, String outputVariable) {
			super();
			this.name = name;
			this.inputVariable = inputVariable;
			this.outputVariable = outputVariable;
		}

		public String getName() {
			return name;
		}

		public Map<String, Object> execute(Map<String, Object> parentResults) throws Exception {

			String forTempKey = getName() + "_ForEach_idx";

			LOG.debug("[for-" + getName() + "] i: " + executionVariables.get(forTempKey));

			Integer i = executionVariables.containsKey(forTempKey) ? (Integer) executionVariables.get(forTempKey) : 0;
			if (executionVariables.containsKey(inputVariable + "_" + i)) {
				Object var = executionVariables.get(inputVariable + "_" + i);
				LOG.debug("[for-" + getName() + "] '" + outputVariable + "'='" + var + "'");
				executionVariables.put(outputVariable, var.toString());
			} else {
				executionVariables.put(outputVariable, null);
				return null;
			}
			executionVariables.put(forTempKey, ++i);
			return new HashMap<String, Object>();
		}
	}

	public final class While implements Step {

		private final String name;

		private final String expr;

		private final long waitBetween;

		private final long timeout;

		public While(String name, String expr, long waitBetween, long timeout) {
			this.name = name;
			this.expr = expr;
			this.waitBetween = waitBetween;
			this.timeout = timeout;
		}

		public String getName() {
			return name;
		}

		public Map<String, Object> execute(Map<String, Object> parentResults) throws Exception {
			HashMap<String, Object> ret = new HashMap<String, Object>();

			String tempKey = getName() + "_While_Timeout";
			Long endTime = (Long) executionVariables.get(tempKey);
			if (endTime == null) {
				endTime = System.currentTimeMillis() + this.timeout;
				executionVariables.put(tempKey, endTime);
			}
			LOG.debug("[while-" + getName() + "] Executing... expr:" + expr + ", endTime: " + endTime);

			if (System.currentTimeMillis() > endTime) {
				throw new RuntimeException("Timeout after " + timeout + " millis. Breaking while...");
			}

			boolean eval = eval(expr);
			ret.put("result", eval);
			if (eval && waitBetween > 0) {
				Thread.sleep(waitBetween);
			}
			LOG.debug("[while-" + getName() + "]  Returning... ret '" + ret + "'");

			return ret;
		}

		// TODO: use java 6 ScriptEngineManager
		private boolean eval(String expr1) {

			String interpolatedExpr = replaceVariables(expr1, executionVariables);

			if ("'false'=='false'".equals(interpolatedExpr)) {
				return true;
			}
			if ("'true'=='false'".equals(interpolatedExpr)) {
				return false;
			}
			return false;
		}
	}

	public static final class Node<T> {

		private List<Node<T>> children = new ArrayList<Node<T>>();
		private Node<T> parent = null;
		private T data = null;

		public Node(T data) {
			this.data = data;
		}

		public Node(T data, Node<T> parent) {
			this.data = data;
			this.parent = parent;
		}

		public List<Node<T>> getChildren() {
			return children;
		}

		public Node<T> getParent() {
			return parent;
		}

		public void setParent(Node<T> parent) {
			this.parent = parent;
			parent.children.add(this);
		}

		public Node<T> addChild(T data) {
			Node<T> child = new Node<T>(data);
			child.parent = this;
			this.children.add(child);
			return child;
		}

		public void addChild(Node<T> child) {
			child.parent = this;
			this.children.add(child);
		}

		public T getData() {
			return this.data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public boolean isRoot() {
			return (this.parent == null);
		}

		public boolean isLeaf() {
			if (this.children.size() == 0)
				return true;
			else
				return false;
		}

		public void removeParent() {
			this.parent = null;
		}
	}
}
