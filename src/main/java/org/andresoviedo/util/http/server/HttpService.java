package org.andresoviedo.util.http.server;

import java.io.IOException;

import javax.net.ssl.SSLServerSocketFactory;

public interface HttpService {

	void addSupportHandler(RequestHandler handler);

	void addHandler(RequestHandler handler);

	void start() throws IOException;

	void stop();

	void makeSecure(SSLServerSocketFactory makeSSLSocketFactory);

}