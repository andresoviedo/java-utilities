package org.andresoviedo.util.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtils {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private JsonUtils() {

	}

	public static String serialize(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}

	public static Map<String, Object> deserialize(String jsonString) {
		try {
			if (StringUtils.isBlank(jsonString)) {
				return Collections.emptyMap();
			}
			Map<String, Object> ret = new HashMap<String, Object>();
			ret = new ObjectMapper().readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
			});
			if (logger.isDebugEnabled()) {
				logger.debug("JSON Parsed '" + jsonString + "' parsed to '" + ret + "'");
			}
			return ret;
		} catch (Exception ex) {
			throw new IllegalArgumentException("La expresión '" + jsonString + "' no es un JSON válido", ex);
		}
	}

	public static boolean assertEquals(JSONObject js1, JSONObject js2) throws JSONException {
		if (js1 == null || js2 == null) {
			return (js1 == js2);
		}

		List<String> l1 = Arrays.asList(JSONObject.getNames(js1));
		Collections.sort(l1);
		List<String> l2 = Arrays.asList(JSONObject.getNames(js2));
		Collections.sort(l2);
		if (!l1.equals(l2)) {
			return false;
		}

		for (String key : l1) {
			Object val1 = js1.get(key);
			Object val2 = js2.get(key);

			if (val1 instanceof JSONObject) {
				if (!(val2 instanceof JSONObject)) {
					return false;
				}
				return assertEquals((JSONObject) val1, (JSONObject) val2);
			}

			if (val1 instanceof JSONArray) {
				if (!(val2 instanceof JSONArray)) {
					return false;
				}
				return assertEquals((JSONArray) val1, (JSONArray) val2);
			}

			if (val1 == null) {
				return val2 == null;
			}

			return val1.equals(val2);
		}
		return true;
	}

	public static boolean assertEquals(JSONArray js1, JSONArray js2) throws JSONException {
		if (js1 == null || js2 == null) {
			return (js1 == js2);
		}

		if (js1.length() != js2.length()) {
			return false;
		}

		List<Object> js1_list = new ArrayList<Object>();
		List<Object> js2_list = new ArrayList<Object>();
		for (int i = 0; i < js1.length(); i++) {
			js1_list.add(js1.get(i));
		}
		for (int i = 0; i < js2.length(); i++) {
			js2_list.add(js2.get(i));
		}

		for (Iterator<Object> it1 = js1_list.iterator(); it1.hasNext();) {
			Object val1 = it1.next();
			if (remove(val1, js2_list)) {
				it1.remove();
				continue;
			}
		}
		return js1_list.isEmpty() && js2_list.isEmpty();
	}

	public static boolean remove(Object val1, List<Object> js2_list) throws JSONException {
		for (Iterator<Object> it2 = js2_list.iterator(); it2.hasNext();) {
			Object val2 = it2.next();
			if (val1 == null && val2 == null) {
				it2.remove();
				return true;
			}
			if (val1 instanceof JSONObject && val2 instanceof JSONObject) {
				if (assertEquals((JSONObject) val1, (JSONObject) val2)) {
					it2.remove();
					return true;
				}
			}
			if (val1 instanceof JSONArray) {
				if (assertEquals((JSONArray) val1, (JSONArray) val2)) {
					it2.remove();
					return true;
				}
			}
			if (val1 != null && val1.equals(val2)) {
				it2.remove();
				return true;
			}
		}
		return false;
	}
}
