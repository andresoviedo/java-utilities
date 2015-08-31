package org.andresoviedo.util.xls;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class HttpXMLTT {

	private Logger logger = Logger.getLogger("");
	private URL _getURL = null;
	private URL[] _postURLs = null;
	private String _xslFileName = null;
	private long _pollingTime = 1000 * 10;
	private long _pollingTimeout = 1000 * 60;

	private HttpPostThread _postTask = null;
	private ThreadHungWatcher _hungWatcher = null;

	private DocumentBuilder _docBuilder = null;
	private Transformer _xformer = null;

	public HttpXMLTT(String getURL, String postURLs, String xslFileName, String pollingTime, String pollingTimeout) {
		try {
			logger.logp(Level.INFO, "HttpXMLTT", "constructor", "Constructing...");
			_getURL = new URL(getURL);

			String[] postURLsArray = postURLs.split("\\|");
			_postURLs = new URL[postURLsArray.length];
			for (int i = 0; i < postURLsArray.length; i++) {
				_postURLs[i] = new URL(postURLsArray[i]);
			}

			_xslFileName = xslFileName;
			_pollingTime = Long.parseLong(pollingTime);
			_pollingTimeout = Long.parseLong(pollingTimeout);
			init();
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, "HttpXMLTT", "constructor", ex.getMessage(), ex);
		}
	}

	public void stop() {
		logger.logp(Level.INFO, "HttpXMLTT", "contextDestroyed", "Closing...");
		_postTask.stop();
		_hungWatcher.stop();
	}

	private void init() throws Exception {
		logger.logp(Level.INFO, "HttpXMLTT", "init", "Initializing...");
		_docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		if (_xslFileName != null && !_xslFileName.equals("")) {
			StreamSource xslSrc = new StreamSource(new FileInputStream(_xslFileName));
			_xformer = TransformerFactory.newInstance().newTransformer(xslSrc);
		} else {
			_xformer = TransformerFactory.newInstance().newTransformer();
		}
		_postTask = new HttpPostThread();
		_postTask.start();
		_hungWatcher = new ThreadHungWatcher();
		_hungWatcher.start();
	}

	class ThreadHungWatcher implements Runnable {
		boolean started = false;
		Thread thread = null;

		void start() {
			if (!started) {
				logger.logp(Level.INFO, "ThreadHungWatcher", "start", "Starting thread...");
				started = true;
				thread = new Thread(this, "ThreadHungWatcher");
				thread.start();
			}
		}

		void stop() {
			if (started) {
				logger.logp(Level.INFO, "ThreadHungWatcher", "stop", "Stopping thread...");
				started = false;
				thread.interrupt();
				try {
					thread.join();
				} catch (InterruptedException ex) {
				}
			}
		}

		public void run() {
			logger.logp(Level.INFO, "ThreadHungWatcher", "run", "Thread running...");
			try {
				while (started) {
					Thread.sleep(_pollingTimeout);
					if (_postTask.nextPost != -1 && // test wheter thread it's
													// started
							System.currentTimeMillis() - _postTask.nextPost >= _pollingTimeout) {
						logger.logp(Level.INFO, "ThreadHungWatcher", "run", "Restarting postTask thread...");
						_postTask.stop();
						_postTask = new HttpPostThread();
						_postTask.start();
					}
				}
			} catch (InterruptedException ex) {
				logger.logp(Level.INFO, "ThreadHungWatcher", "run", ex.getMessage());
			}
			logger.logp(Level.INFO, "ThreadHungWatcher", "run", "Dying...");
		}
	}

	class HttpPostThread implements Runnable {
		Thread _thread = null;
		boolean _started = false;
		long nextPost = -1;
		long lastLog = -1;

		void start() {
			if (!_started) {
				logger.logp(Level.INFO, "HttpPostThread", "start", "Starting thread...");
				_started = true;
				_thread = new Thread(this);
				_thread.start();
			}
		}

		void stop() {
			if (_started) {
				logger.logp(Level.INFO, "HttpPostThread", "stop", "Stopping thread...");
				_started = false;
				_thread.interrupt();
			}
		}

		public void run() {
			logger.logp(Level.INFO, "HttpPostThread", "run", "Thread running...");
			while (_started) {
				try {
					nextPost = System.currentTimeMillis();
					post();
					nextPost = System.currentTimeMillis() + _pollingTime;
				} catch (IOException ex) {
					logger.logp(Level.WARNING, "HttpPostThread", "run", ex.getMessage());
				} catch (Exception ex) {
					logger.logp(Level.SEVERE, "HttpPostThread", "run", ex.getMessage(), ex);
				}
				// Sleep for a while
				try {
					Thread.sleep(_pollingTime);
				} catch (InterruptedException ex) {
					logger.logp(Level.INFO, "HttpPostThread", "run", ex.getMessage());
				}
			}
			logger.logp(Level.INFO, "HttpPostThread", "run", "Thread dying...");
		}

		public void post() throws Exception {
			// log process
			if (lastLog == -1 || (System.currentTimeMillis() - lastLog) > 1000 * 60 * 5) {
				logger.logp(Level.INFO, "HttpPostThread", "post", "Posting msgs...");
				lastLog = System.currentTimeMillis();
			}

			// Get document
			HttpURLConnection urlConn = (HttpURLConnection) _getURL.openConnection();
			int responseCode = urlConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = urlConn.getInputStream();
				Document doc = _docBuilder.parse(in);
				in.close();

				for (URL url : _postURLs) {
					try {
						// Post document
						urlConn = (HttpURLConnection) url.openConnection();
						urlConn.setRequestProperty("User-Agent", "bot");
						urlConn.setRequestProperty("Content-Type", "text/xml");
						urlConn.setRequestMethod("POST");
						urlConn.setDoOutput(true);
						OutputStream out = urlConn.getOutputStream();
						_xformer.transform(new DOMSource(doc), new StreamResult(out));

						// process post response
						responseCode = urlConn.getResponseCode();
						if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_ACCEPTED
								&& responseCode != HttpURLConnection.HTTP_CREATED && responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
							String responseMsg = ((HttpURLConnection) urlConn).getResponseMessage();
							logger.logp(Level.WARNING, "HttpPostThread", "post", "from post url: " + "responseCode[" + responseCode + "]"
									+ "responseMsg[" + responseMsg + "]");
						}
						out.close();
					} catch (IOException ex) {
						logger.logp(Level.WARNING, "HttpPostThread", "post", ex.getMessage());
					} catch (Exception ex) {
						logger.logp(Level.SEVERE, "HttpPostThread", "post", ex.getMessage(), ex);
					}
				}
			} else {
				String responseMsg = ((HttpURLConnection) urlConn).getResponseMessage();
				logger.logp(Level.WARNING, "HttpPostThread", "post", "from get url: " + "responseCode[" + responseCode + "]"
						+ "responseMsg[" + responseMsg + "]");
			}
		}
	}
}
