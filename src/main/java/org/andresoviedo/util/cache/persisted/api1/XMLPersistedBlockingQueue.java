package org.andresoviedo.util.cache.persisted.api1;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XMLPersistedBlockingQueue {

	private File dir;
	private String _dir;
	private DocumentBuilder builder;
	private Transformer xformer;

	private Vector<File> _files = new Vector<File>();

	private Logger _logger = Logger.getLogger("");

	public XMLPersistedBlockingQueue() {
		this(".");
	}

	public XMLPersistedBlockingQueue(String storageDir) {
		_dir = storageDir;
		try {
			dir = new File(_dir);
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xformer = TransformerFactory.newInstance().newTransformer();
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void init() {
		initializeDirectory();
		readDirectory();
	}

	private void initializeDirectory() {
		_logger.logp(Level.INFO, "XMLPersistedBlockingQueue", "initializeDirectory", "Initializing directory [" + dir + "]");
		if (!dir.exists()) {
			boolean ret = dir.mkdirs();
			_logger.logp(Level.INFO, "XMLPersistedBlockingQueue", "initializeDirectory", "Directory created [" + ret + "]");
		}
	}

	private void readDirectory() {
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			Arrays.sort(files);
			for (int i = 0; i < files.length; i++) {
				_files.add(files[i]);
			}
		}
	}

	// ------------------------------------------------------------------------ //

	public void add(Document doc) {
		try {
			synchronized (_files) {
				File f = getFreeFile();
				xformer.transform(new DOMSource(doc), new StreamResult(f));
				_files.add(f);
				_files.notify();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Document get() throws InterruptedException {
		Document[] ret = get(1);
		return (ret != null) ? ret[0] : null;
	}

	public Document[] get(int max) throws InterruptedException {
		if (max <= 0) {
			throw new IllegalArgumentException("max <= 0");
		}
		Document[] ret = null;
		Vector<Document> docs = new Vector<Document>();
		synchronized (_files) {
			// First checking
			while (_files.isEmpty()) {
				_files.wait();
			}
			for (Iterator<File> i = _files.iterator(); i.hasNext() && max > 0; max--) {
				try {
					// parse file
					File f = (File) i.next();
					Document doc = builder.parse(f);
					docs.add(doc);

					// Delete reference and file
					i.remove();
					f.delete();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		if (!docs.isEmpty()) {
			ret = new Document[docs.size()];
			for (int i = 0; i < docs.size(); i++) {
				ret[i] = (Document) docs.elementAt(i);
			}
		}
		return ret;
	}

	// ------------------------------------------------------------------------ //

	private File getFreeFile() {
		File ret = null;
		File baseDir = new File(_dir + File.separator);

		// Create dirs if they doesn't exists
		if (!baseDir.exists()) {
			baseDir.mkdirs();
		}

		// Look for free file
		int i = 0;
		do {
			String freeFileName = baseDir.getAbsolutePath() + File.separator + String.valueOf(System.currentTimeMillis()) + i + ".xml";
			ret = new File(freeFileName);
			i++;
		} while (ret.exists());
		return ret;
	}

	// ------------------------------------------------------------------------ //
	// Sorting by name
	/*
	 * public static void main(String[] args){ String[] array = new String[2]; array[0] = "hola_1"; array[1] = "hola"; Arrays.sort(array);
	 * System.out.println("array[0]:"+array[0]); System.out.println("array[1]:"+array[1]); }
	 */
}
