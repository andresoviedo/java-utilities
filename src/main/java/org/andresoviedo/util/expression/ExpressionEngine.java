package org.andresoviedo.util.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.log4j.Logger;

public class ExpressionEngine {

	private static final Logger LOG = Logger.getLogger(ExpressionEngine.class);

	private final ScriptEngineManager scriptEngineManager;
	private final ScriptEngine scriptEngine;

	public static final Pattern VARIABLE_REGEX = Pattern.compile("\\$\\{([^\\$]+?)\\}");

	public ExpressionEngine() {
		scriptEngineManager = new ScriptEngineManager();
		scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> Map<String, T> evaluate(Map<String, T> expresssions, Map<String, Object>... vars) {

		if (expresssions == null) {
			return null;
		}
		if (expresssions.size() == 0) {
			return Collections.emptyMap();
		}

		Map<String, T> ret = new HashMap<String, T>();
		for (Map.Entry<String, T> entry : expresssions.entrySet()) {
			if (entry.getValue() instanceof String) {
				ret.put(evaluate((String) entry.getKey(), vars), ((T) evaluate((String) entry.getValue(), vars)));
			} else {
				ret.put(evaluate((String) entry.getKey(), vars), entry.getValue());
			}
		}
		return Collections.unmodifiableMap(ret);
	}

	public String evaluate(String expression, Map<String, Object>... vars) {

		LOG.trace("Evaluating value for '" + expression + "'.... variables '" + Arrays.toString(vars) + "'");
		if (expression == null) {
			return null;
		}
		if (vars == null || vars.length == 0) {
			return expression;
		}

		// default response
		String ret = expression;

		// prepare engine with variables
		final ScriptContext scriptContext = new SimpleScriptContext();
		final Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
		for (Map<String, Object> map : vars) {
			for (Map.Entry<String, Object> variable : map.entrySet()) {
				bindings.put(variable.getKey(), variable.getValue());
			}
		}

		// check string for expressions, that is "${expression}"
		Matcher m = VARIABLE_REGEX.matcher(expression);
		while (m.find()) {
			String match = m.group(0);
			String varName = m.group(1);
			Object eval;
			try {
				eval = scriptEngine.eval(varName, scriptContext);
			} catch (ScriptException ex) {
				throw new RuntimeException("Problem evaluating expression '" + expression + "'.", ex);
			}
			LOG.debug("Evaluation result: class '" + eval.getClass() + "', value '" + eval + "'");
			ret = ret.replaceFirst(Pattern.quote(match), Matcher.quoteReplacement(String.valueOf(eval)));
		}

		// return response
		return VARIABLE_REGEX.matcher(ret).find() && !expression.equals(ret) ? evaluate(ret, vars) : ret;
	}

}
