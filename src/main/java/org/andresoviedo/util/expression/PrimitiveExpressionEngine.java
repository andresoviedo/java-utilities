package org.andresoviedo.util.expression;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimitiveExpressionEngine {

	private static final Logger LOG = Logger.getLogger(PrimitiveExpressionEngine.class.getName());

	public static final Pattern VARIABLE_REGEX = Pattern.compile("\\$\\{([^\\$]+?)\\}");

	private static final Pattern STRING_REGEX = Pattern.compile("^'(.+?)'$");

	/**
	 * Math operation regexp
	 */
	private static final Pattern MATH_REGEX = Pattern.compile("^(\\d+)([\\+-])(\\d+)$");

	private static final Pattern STRING_FUNCTION_2_ARGS = Pattern.compile("^'(.+?)'\\.(.+?)\\((.+?),(.+?)\\)$");

	private static final Pattern STRING_FUNCTION_1_ARGS = Pattern.compile("^'(.+?)'\\.(.+?)\\((.+?)\\)$");

	private static final Pattern STRING_FUNCTION_0_ARGS = Pattern.compile("^'(.+?)'\\.(.+?)\\(\\)$");

	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> replaceVariables(Map<String, T> expressions, Map<String, Object>... vars) {
		if (expressions == null) {
			return null;
		}
		if (expressions.size() == 0) {
			return Collections.emptyMap();
		}

		Map<String, T> ret = new HashMap<String, T>();
		for (Map.Entry<String, T> entry : expressions.entrySet()) {
			if (entry.getValue() instanceof String) {
				ret.put(replaceVariables((String) entry.getKey(), vars),
						((T) replaceVariables((String) entry.getValue(), vars)));
			} else {
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		return Collections.unmodifiableMap(ret);
	}

	public static String replaceVariables(String expression, Map<String, Object>... vars) {
		LOG.finest("Replacing variables for '" + expression + "'.... variables '" + Arrays.toString(vars) + "'");
		if (expression == null) {
			return null;
		}
		if (vars == null || vars.length == 0) {
			return expression;
		}

		String ret = expression;
		Matcher m = VARIABLE_REGEX.matcher(expression);
		while (m.find()) {
			String match = m.group(0);
			String varName = m.group(1);

			if ("__func(SECONDS_FROM_EPOCH)".equals(varName)) {
				String secondsFromEpoch = String.valueOf(System.currentTimeMillis() / 1000);
				LOG.finest("Replacing '" + match + "' with seconds from epoch '" + secondsFromEpoch + "'...");
				// ret = ret.replaceFirst(varName,
				// String.valueOf(System.currentTimeMillis() / 1000));
				ret = ret.replaceFirst(Pattern.quote(match), secondsFromEpoch);
				continue;
			}

			if ("__func(MILLIS_FROM_EPOCH)".equals(varName)) {
				String secondsFromEpoch = String.valueOf(System.currentTimeMillis());
				LOG.finest("Replacing '" + match + "' with millis from epoch '" + secondsFromEpoch + "'...");
				// ret = ret.replaceFirst(varName,
				// String.valueOf(System.currentTimeMillis() / 1000));
				ret = ret.replaceFirst(Pattern.quote(match), secondsFromEpoch);
				continue;
			}

			if ("__func(RANDOM(7))".equals(varName)) {
				String randomString = UUID.randomUUID().toString().substring(0, 7);
				LOG.finest("Replacing '" + match + "' with random string '" + randomString + "'...");
				// ret = ret.replaceFirst(varName,
				// String.valueOf(System.currentTimeMillis() / 1000));
				ret = ret.replaceFirst(Pattern.quote(match), randomString);
				continue;
			}

			Matcher m3 = STRING_FUNCTION_2_ARGS.matcher(varName);
			if (m3.find()) {
				LOG.finest("Replacing '" + match + "' with function (2 args) '" + varName + "'...");
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

			Matcher m1 = STRING_FUNCTION_1_ARGS.matcher(varName);
			if (m1.find()) {
				LOG.finest("Replacing '" + match + "' with function (1 args) '" + varName + "'...");
				String funcValue = m1.group(1);
				String function = m1.group(2);
				String arg = m1.group(3);
				if ("lastIndexOf".equals(function)) {
					Matcher m11 = STRING_REGEX.matcher(arg);
					if (!m11.find()) {
						throw new IllegalArgumentException("Function '" + varName + "' couldn't be parsed. Check arg '"
								+ arg + "' is enclosed with apostrophe");
					}
					ret = ret.replaceFirst(Pattern.quote(match), String.valueOf(funcValue.lastIndexOf(m11.group(1))));
				} else if ("substring".equals(function)) {
					ret = ret.replaceFirst(Pattern.quote(match), funcValue.substring(Integer.parseInt(arg)));
				} else {
					throw new IllegalArgumentException("Function '" + function + "' not supported");
				}
				continue;
			}

			Matcher f0 = STRING_FUNCTION_0_ARGS.matcher(varName);
			if (f0.find()) {
				LOG.finest("Replacing '" + match + "' with function (0 args) '" + varName + "'...");
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

			Matcher mMath = MATH_REGEX.matcher(varName);
			if (mMath.find()) {
				LOG.finest("Replacing '" + match + "' with math function (2 args) '" + varName + "'...");
				String arg1 = mMath.group(1);
				String operation = mMath.group(2);
				String arg2 = mMath.group(3);
				if ("+".equals(operation)) {
					ret = ret.replaceFirst(Pattern.quote(match),
							String.valueOf(Integer.parseInt(arg1) + Integer.parseInt(arg2)));
				} else {
					throw new IllegalArgumentException("Math operation '" + operation + "' not supported");
				}
				continue;
			}

			// Try to replace with the collected in-memory variables
			boolean didMatch = false;
			String replacement = null;
			for (Map<String, Object> variableMap : vars) {
				if (variableMap.containsKey(varName)) {
					LOG.finest("Replacing '" + match + "' with '" + variableMap.get(varName) + "'...");
					replacement = Matcher.quoteReplacement(variableMap.get(varName).toString());
					ret = ret.replaceFirst(Pattern.quote(match), replacement);
					didMatch = true;
				}
			}

			if (!didMatch) {
				throw new IllegalArgumentException("Function '" + varName + "' not supported");
			}
		}

		return VARIABLE_REGEX.matcher(ret).find() && !expression.equals(ret) ? replaceVariables(ret, vars) : ret;
	}
}
