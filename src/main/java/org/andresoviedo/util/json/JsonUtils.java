package org.andresoviedo.util.json;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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
}
