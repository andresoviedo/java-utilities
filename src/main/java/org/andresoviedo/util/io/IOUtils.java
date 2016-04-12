package org.andresoviedo.util.io;

import java.io.IOException;
import java.net.ServerSocket;

public class IOUtils {

	/**
	 * @return a free socks port in the local machine
	 * @throws IOException
	 *             if an I/O error occurs when opening the socket
	 */
	public static int freePort() {
		try {
			ServerSocket socket = new ServerSocket(0);
			int port = socket.getLocalPort();
			socket.close();
			return port;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
