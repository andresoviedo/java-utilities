package org.andresoviedo.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class ResourceUtils {

	public static final String getResourceAsString(String path) throws IOException {
		InputStream resourceAsStream = ResourceUtils.class.getResourceAsStream(path);
		if (resourceAsStream == null) {
			return null;
		}
		List<String> lines = IOUtils.readLines(resourceAsStream, "UTF-8");
		StringBuilder sb = new StringBuilder(lines.remove(0));
		for (String line : lines) {
			sb.append('\n').append(line);
		}
		return sb.toString();
	}
}
