package org.andresoviedo.util.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ZipHelper {

	public static Log logger = LogFactory.getLog(ZipHelper.class);

	public static void zipFiles(File sourceDir, File targetFile) throws IOException {

		// out put file

		logger.debug("Zipping directory files '" + sourceDir + "' to file '" + targetFile + "'...");
		ZipOutputStream out = null;

		try {
			targetFile.getParentFile().mkdirs();
			out = new ZipOutputStream(new FileOutputStream(targetFile));

			for (File file : sourceDir.listFiles()) {

				logger.debug("Comprimiendo '" + file + "'...");

				// name the file inside the zip file
				out.putNextEntry(new ZipEntry(file.getName()));

				FileInputStream is = new FileInputStream(file);
				IOUtils.copy(is, out);

				out.closeEntry();
				is.close();
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private static List<File> getFilePartsList(File sourceFile) {
		List<File> fileParts = new ArrayList<File>();
		int fileNumber = 1;
		File filePart = null;
		while (true) {
			filePart = new File(sourceFile.getAbsolutePath() + "." + String.format("%03d", fileNumber++));
			if (!filePart.exists()) {
				break;
			}
			fileParts.add(filePart);
		}
		return fileParts;
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
	 * Copia el fichero al filesystem local en un directorio temporal y lo descomprime en el mismo directorio.
	 * 
	 * @param sourceFile
	 *            el fichero a descomprimir
	 * @return el directorio temporal con el fichero descomprimido
	 * @throws Exception
	 */
	public static File getFileThenUnzip(File sourceFile) throws IOException {

		File tempDir = File.createTempFile("arq-sdk-", null);
		logger.debug("Creado directorio temporal '" + tempDir.getAbsolutePath() + "'");
		FileUtils.forceDelete(tempDir);

		getFileThenUnzip(sourceFile, tempDir);

		return tempDir;
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
	 * Copia el fichero al filesystem local y luego lo descomprime en el directorio especificado.
	 * 
	 * @param sourceFile
	 *            el fichero a descomprimir
	 * @param directorio
	 *            de descompresión
	 * @throws Exception
	 */
	public static void getFileThenUnzip(File sourceFile, File destinationDir) throws IOException {
		logger.info("Instalando '" + sourceFile.getName() + "'...");

		logger.debug("Copiando fichero '" + sourceFile.getName() + "' en filesystem local...");
		File sourceFileCopy = new File(destinationDir, sourceFile.getName());
		FileUtils.copyFile(sourceFile, sourceFileCopy);

		logger.debug("Descomprimiendo fichero '" + sourceFileCopy + "'...");
		unzipFile(sourceFileCopy, destinationDir);
		FileUtils.deleteQuietly(sourceFileCopy);

		logger.info("Descompresión de '" + sourceFile.getName() + "' completada");
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

	public static File getMultipartZipThenUnzip(File sourceFile) throws IOException {

		File tempDir = File.createTempFile("arq-sdk-", null);
		logger.debug("Creado directorio temporal '" + tempDir.getAbsolutePath() + "'");
		FileUtils.forceDelete(tempDir);

		getMultipartZipThenUnzip(sourceFile, tempDir);

		return tempDir;
	}

	/**
	 * Copia el fichero al filesystem local y luego lo descomprime en el directorio especificado.
	 * 
	 * @param sourceFile
	 *            el fichero a descomprimir
	 * @param directorio
	 *            de descompresión
	 * @throws Exception
	 */
	public static void getMultipartZipThenUnzip(final File sourceFile, final File destinationDir) throws IOException {

		logger.info("Descargando '" + sourceFile.getName() + ".XXX' ...");

		getMultipartFileMultithread(sourceFile, destinationDir);

		File localCopy = new File(destinationDir, sourceFile.getName());
		logger.debug("Descomprimiendo fichero '" + localCopy + ".XXX'...");
		unzipMultipartZip(localCopy, destinationDir);

		logger.debug("Eliminando cache '" + localCopy + ".XXX'...");
		deleteFileParts(localCopy);

		logger.info("Descompresión de '" + sourceFile.getName() + "' en Filesystem local completada");
	}

	public static File unzipFile(File sourceFile) throws IOException {

		File tempDir = File.createTempFile("arq-sdk-", null);
		logger.debug("Creado directorio temporal '" + tempDir.getAbsolutePath() + "'");
		FileUtils.forceDelete(tempDir);

		unzipFile(sourceFile, tempDir);

		return tempDir;
	}

	public static void unzipFile(File sourceFile, File targetDir) throws IOException {
		ZipFile zipFile = new ZipFile(sourceFile);
		targetDir.mkdirs();
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = new File(targetDir, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					logger.debug("Descomprimiendo '" + entry.getName() + "'...");
					InputStream in = zipFile.getInputStream(entry);
					OutputStream out = new FileOutputStream(entryDestination);
					IOUtils.copy(in, out);
					IOUtils.closeQuietly(in);
					IOUtils.closeQuietly(out);
				}
			}
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
		}
	}

	public static File unzipMultipartZip(File sourceFile) throws IOException {
		File tempDir = File.createTempFile("arq-sdk-", null);
		logger.debug("Creado directorio temporal '" + tempDir.getAbsolutePath() + "'");
		FileUtils.forceDelete(tempDir);

		unzipMultipartZip(sourceFile, tempDir);

		return tempDir;
	}

	public static void unzipMultipartZip(File sourceFile, File targetDir) throws IOException {

		List<File> filePartsList = getFilePartsList(sourceFile);
		if (filePartsList.isEmpty()) {
			throw new IllegalArgumentException("No file parts found for '" + sourceFile.getAbsolutePath() + "'");
		}

		List<FileInputStream> sourceFileParts = getFileInputStreamParts(filePartsList);

		ZipInputStream zipInputStream = new ZipInputStream(new SequenceInputStream(Collections.enumeration(sourceFileParts)));

		targetDir.mkdirs();
		ZipEntry entry = null;
		try {
			while ((entry = zipInputStream.getNextEntry()) != null) {
				File entryDestination = new File(targetDir, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					logger.debug("Descomprimiendo '" + entry.getName() + "'...");
					OutputStream out = new FileOutputStream(entryDestination);
					IOUtils.copy(zipInputStream, out);
					IOUtils.closeQuietly(out);
				}
			}
		} finally {
			if (zipInputStream != null) {
				zipInputStream.close();
			}
		}
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

	private static void getMultipartFileMultithread(final File sourceFile, final File destinationDir) {
		ExecutorService executor = null;
		try {
			List<File> filePartsList = getFilePartsList(sourceFile);
			if (filePartsList.isEmpty()) {
				throw new IllegalArgumentException("No file parts found for '" + sourceFile.getAbsolutePath() + "'");
			}

			executor = Executors.newFixedThreadPool(4);

			for (final File finalFilePart : filePartsList) {
				FutureTask<Integer> getFilePartTask = new FutureTask<Integer>(new Callable<Integer>() {
					@Override
					public Integer call() {
						try {
							logger.debug("Descargando fichero '" + finalFilePart + "' en filesystem local...");
							File filePartCopy = new File(destinationDir, finalFilePart.getName());
							FileUtils.copyFile(finalFilePart, filePartCopy);
							return filePartCopy.exists() ? 0 : -1;
						} catch (IOException ex) {
							return -1;
						}
					}
				});
				executor.submit(getFilePartTask);
			}
			executor.shutdown();
			executor.awaitTermination(24 * 60L, TimeUnit.MINUTES);

		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static List<FileInputStream> getFileInputStreamParts(List<File> sourceFiles) throws FileNotFoundException {
		List<FileInputStream> sourceFileParts = new ArrayList<FileInputStream>();
		for (File file : sourceFiles) {
			sourceFileParts.add(new FileInputStream(file));
		}
		return sourceFileParts;
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

	private static void deleteFileParts(File sourceFile) throws IOException {
		for (File file : getFilePartsList(sourceFile)) {
			FileUtils.forceDelete(file);
		}
	}

}
