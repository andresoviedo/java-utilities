package org.andresoviedo.util.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple HTTP proxy
 * 
 * @author andresoviedo
 * 
 */
public final class HttpProxy {

	private final int port;
	private ProxyServerThread proxyThread = null;
	private boolean started = false;

	public HttpProxy(int port) {
		super();
		this.port = port;
	}

	public synchronized boolean start() {
		if (started) {
			return false;
		}
		proxyThread = new ProxyServerThread(port);
		proxyThread.start();
		started = true;
		return true;
	}

	public synchronized boolean stop() {
		if (!started) {
			return false;
		}
		proxyThread.stop();
		started = false;
		return true;
	}

	static class ProxyServerThread implements Runnable {

		private static final Logger LOG = Logger.getLogger(HttpProxy.class.getName());

		private final ServerSocket serverSocket;
		private final List<Thread> requests = new ArrayList<Thread>();

		private boolean started = false;
		private Thread thread = null;

		public ProxyServerThread(int port) {
			try {
				LOG.fine("Opening server socket at port '" + port + "'...");
				serverSocket = new ServerSocket(port);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, "Could not open port '" + port + "'", ex);
				throw new RuntimeException(ex);
			}
		}

		public void run() {
			while (started) {
				LOG.info("I'm starting....");
				try {
					Thread requestThread = new Thread(new ProxyRequestThread(serverSocket.accept()));
					synchronized (requests) {
						requests.add(requestThread);
					}
					requestThread.start();
				} catch (Exception ex) {
					LOG.log(Level.SEVERE, "Oops! There was a problem processing request", ex);
				}
			}
			LOG.info("I have been stopped. Good bye!");
		}

		synchronized boolean start() {
			if (started) {
				return false;
			}

			LOG.info("Starting proxy server...");
			thread = new Thread(this);
			started = true;
			thread.start();

			return true;
		}

		synchronized boolean stop() {
			if (!started) {
				return false;
			}
			try {
				started = false;
				serverSocket.close();
				thread.interrupt();
				thread.join(5000);

				synchronized (requests) {
					for (Thread request : requests) {
						try {
							request.interrupt();
							request.join(5000);
						} catch (Exception ex) {
							LOG.log(Level.SEVERE, "Oops! There was a problem waiting for request to finish", ex);
						}
					}
				}

				return true;
			} catch (Exception ex) {
				LOG.log(Level.SEVERE, "Oops! There was a problem stopping the proxy", ex);
				return false;
			}
		}

		class ProxyRequestThread implements Runnable {

			private Socket socket = null;
			private static final int BUFFER_SIZE = 32768;

			public ProxyRequestThread(Socket socket) {
				this.socket = socket;
			}

			public void run() {
				try {
					OutputStream out = new BufferedOutputStream(socket.getOutputStream());
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

					String inputLine;
					int cnt = 0;
					String urlToCall = "";
					// /////////////////////////////////
					// begin get request from client
					while ((inputLine = in.readLine()) != null) {
						try {
							StringTokenizer tok = new StringTokenizer(inputLine);
							tok.nextToken();
						} catch (Exception e) {
							break;
						}
						// parse the first line of the request to find the url
						if (cnt == 0) {
							String[] tokens = inputLine.split(" ");
							urlToCall = tokens[1];
							// can redirect this to output log
							LOG.fine("Request for : " + urlToCall);
						}

						cnt++;
					}
					// end get request from client
					// /////////////////////////////////

					BufferedReader rd = null;
					try {
						// System.out.println("sending request
						// to real server for url: "
						// + urlToCall);
						// /////////////////////////////////
						// begin send request to server, get response from server
						URL url = new URL(urlToCall);
						URLConnection conn = url.openConnection();
						conn.setDoInput(true);
						conn.setDoOutput(false);

						// Get the response
						InputStream is = null;
						HttpURLConnection huc = (HttpURLConnection) conn;
						int responseCode = huc.getResponseCode();
						LOG.fine("Response code '" + responseCode + "'");

						if (responseCode == HttpURLConnection.HTTP_OK) {
							String connectResponse = "HTTP/1.0 200 Connection established\n" + "Proxy-agent: HttpProxy/1.0\n" + "\r\n";
							out.write(connectResponse.getBytes("UTF-8"));
						}

						try {
							is = conn.getInputStream();
							rd = new BufferedReader(new InputStreamReader(is));

							byte by[] = new byte[BUFFER_SIZE];
							int index = is.read(by, 0, BUFFER_SIZE);
							while (index != -1) {
								out.write(by, 0, index);
								index = is.read(by, 0, BUFFER_SIZE);
							}
							out.close();
						} catch (IOException ioe) {
							LOG.log(Level.SEVERE, "Oops! There was a problem reading streams", ioe);
						}

						// end send request to server, get response from server
						// /////////////////////////////////

						// /////////////////////////////////
						// begin send response to client

						// end send response to client
						// /////////////////////////////////
					} catch (Exception e) {
						// can redirect this to error log
						LOG.log(Level.SEVERE, "Oops! There was an unexpected problem", e);
						// encountered error - just send nothing back, so
						// processing can continue
						// out.writeBytes("");
					}

					// close out all resources
					if (rd != null) {
						rd.close();
					}
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
					if (socket != null) {
						socket.close();
					}

				} catch (Exception ex) {
					LOG.log(Level.SEVERE, "Oops! There was a problem processing request", ex);
				} finally {
					synchronized (requests) {
						requests.remove(this);
					}
				}
			}
		}
	}
}
