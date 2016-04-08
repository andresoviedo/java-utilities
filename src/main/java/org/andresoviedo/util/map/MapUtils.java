package org.andresoviedo.util.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public final class MapUtils {

	public static <K, V> String mapToAbbreviatedString(Map<K, V> map, int maxKeyOrValueLength) {
		if (map == null) {
			return null;
		}
		Iterator<Entry<K, V>> i = map.entrySet().iterator();
		if (!i.hasNext())
			return "{}";

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Entry<K, V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key == map ? "(this Map)"
					: key != null ? StringUtils.abbreviate(key.toString(), maxKeyOrValueLength) : null);
			sb.append('=');
			sb.append(value == map ? "(this Map)"
					: value != null ? StringUtils.abbreviate(value.toString(), maxKeyOrValueLength) : null);
			if (!i.hasNext())
				return sb.append('}').toString();
			sb.append(", ");
		}
	}
}
