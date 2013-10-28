package org.andresoviedo.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;

import org.andresoviedo.util.zip.ZipHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class IOHelper {

	public static Log logger = LogFactory.getLog(IOHelper.class);

	@SuppressWarnings("unchecked")
	public static void copyAndFilter(File sourceFile, File targetFile,
			String pattern, String replacement) throws IOException {
		@SuppressWarnings("rawtypes")
		Dictionary dictionary = new Hashtable<String, String>();
		dictionary.put(pattern, replacement);
		copyAndFilter(sourceFile, targetFile, dictionary);
	}

	public static void copyAndFilter(File sourceFile, File targetFile,
			Dictionary<String, String> dictionary) throws IOException {

		if (sourceFile.isDirectory()) {
			targetFile.mkdirs();
			for (File file : sourceFile.listFiles()) {

				copyAndFilter(file, new File(targetFile, file.getName()),
						dictionary);
			}
		} else if (sourceFile.getName().toLowerCase().endsWith(".zip")) {
			String tempDir = System.getProperty("java.io.tmpdir");
			String tempName = "arq-sdk-" + System.currentTimeMillis();

			File unfilteredZipDir = new File(tempDir, tempName + ".unfiltered");
			File filteredZipDir = new File(tempDir, tempName + ".filtered");
			ZipHelper.unzipFile(sourceFile, unfilteredZipDir);
			copyAndFilter(unfilteredZipDir, filteredZipDir, dictionary);
			ZipHelper.zipFiles(filteredZipDir, targetFile);

			FileUtils.deleteQuietly(unfilteredZipDir);
			FileUtils.deleteQuietly(filteredZipDir);

		} else {
			String content = FileUtils.readFileToString(sourceFile);
			for (Enumeration<String> e = dictionary.keys(); e.hasMoreElements();) {
				String key = e.nextElement();
				String value = dictionary.get(key);
				content = content.replaceAll(key,
						Matcher.quoteReplacement(value));
			}
			if (targetFile.getParent() != null
					&& targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}
			FileUtils.writeStringToFile(targetFile, content);
		}
	}

	public static File filterFile(File sourceFile, String pattern,
			String replace) throws IOException {
		String content = FileUtils.readFileToString(sourceFile);
		File tempFile = File.createTempFile(
				"arq-sdk-" + System.currentTimeMillis(), ".tmp");
		FileUtils.writeStringToFile(tempFile,
				content.replaceAll(pattern, Matcher.quoteReplacement(replace)));
		return tempFile;
	}

	public static File filterFile(File sourceFile,
			Dictionary<String, String> dictionary) throws IOException {
		String content = FileUtils.readFileToString(sourceFile);
		File tempFile = File.createTempFile(
				"arq-sdk-" + System.currentTimeMillis(), ".tmp");

		for (Enumeration<String> e = dictionary.keys(); e.hasMoreElements();) {
			String key = e.nextElement();
			String value = dictionary.get(key);
			content = content.replaceAll(key, Matcher.quoteReplacement(value));
		}

		FileUtils.writeStringToFile(tempFile, content);
		return tempFile;
	}

	// IMPLEMENTACIÓN X FILTROS
	//
	// private static List<File> getFileParts2(final File sourceFile)
	// throws FileNotFoundException {
	// List<File> fileParts = Arrays.asList(sourceFile.getParentFile()
	// .listFiles(new FilenameFilter() {
	// @Override
	// public boolean accept(File file, String name) {
	// return file.isFile()
	// && name.startsWith(sourceFile.getName());
	// }
	// }));
	// Collections.sort(fileParts, NameFileComparator.NAME_COMPARATOR);
	// return fileParts;
	// }

	/**
	 * Thread de lectura del inputstream
	 * 
	 * @author generali
	 * @see http://www.javaworld.com/jw-12-2000/jw-1229-traps.html?page=4
	 */
	public static class StreamGobbler extends Thread {

		static final Log logger = LogFactory.getLog(StreamGobbler.class);

		InputStream is;
		String type;
		Appendable output;

		public StreamGobbler(InputStream is, String type) {
			this(is, type, null);
		}

		public StreamGobbler(InputStream is, String type, Appendable output) {
			this.is = is;
			this.type = type;
			this.output = output;
		}

		// public void run() {
		// try {
		// InputStreamReader isr = new InputStreamReader(is);
		// BufferedReader br = new BufferedReader(isr);
		// String line = null;
		// while ((line = br.readLine()) != null)
		// logger.debug("<" + type + "> " + line);
		// } catch (IOException ex) {
		// logger.fatal("Exception while reading stream.", ex);
		// }
		// }

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				// String line = null;
				// while ((line = br.readLine()) != null)
				// logger.debug("<" + type + "> " + line);
				char[] charBuffer = new char[1];
				while (br.read(charBuffer, 0, 1) != -1) {
					// logger.debug("<" + type + "> " + line);
					if (output != null) {
						output.append(new String(charBuffer));
					}
					logger.debug(new String(charBuffer));
				}
			} catch (IOException ex) {
				logger.fatal("Exception while reading stream.", ex);
			}
		}
	}

	public static File copyResourceToTempFile(String resourcePath)
			throws IOException {
		int extension = resourcePath.lastIndexOf(".");
		return copyResourceToTempFile(resourcePath,
				extension != -1 ? resourcePath.substring(extension) : null);
	}

	public static File copyResourceToTempFile(String resourcePath, String suffix)
			throws IOException {
		int resourceName = resourcePath.lastIndexOf("/");
		File tempFile = File.createTempFile(
				"org.andresoviedo.util."
						+ (resourceName != -1 ? resourcePath
								.substring(resourceName + 1) : "") + ".",
				suffix);
		FileUtils.copyInputStreamToFile(
				IOHelper.class.getResourceAsStream(resourcePath), tempFile);
		return tempFile;
	}
}
