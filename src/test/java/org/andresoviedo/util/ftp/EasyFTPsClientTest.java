package org.andresoviedo.util.ftp;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.andresoviedo.util.serialization.api2.model.EntityConstants;
import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// TODO: fix this test. why is failing?
@Ignore
public class EasyFTPsClientTest {

	String CONSTANT_DATE_STRING = "2012/06/19 09:52";

	Date constantDate;

	String CONSTANT_DATE2_STRING = "2012/06/19 09:53";

	Date constantDate2;

	FtpServer server;

	EasyFTPsClient sut;

	@Before
	public void setUp() throws ParseException, IOException, FtpException {
		constantDate = new SimpleDateFormat("yyyy/MM/dd HH:mm")
				.parse(CONSTANT_DATE_STRING);
		constantDate2 = new SimpleDateFormat("yyyy/MM/dd HH:mm")
				.parse(CONSTANT_DATE2_STRING);

		// get free port
		int freePort = freePort();

		// get temporary home directory
		File homeDirectory = new File("target" + File.separator
				+ "generated-test-sources/sftp");
		homeDirectory.mkdirs();
		File bankFolder = new File(homeDirectory, EntityConstants.BANK_FOLDER);
		bankFolder.mkdir();
		new File(bankFolder, EntityConstants.REQUEST_FOLDER).mkdir();
		File confirmationDir = new File(bankFolder,
				EntityConstants.CONFIRMATION_FOLDER);
		confirmationDir.mkdirs();
		File responseDir = new File(bankFolder, EntityConstants.RESPONSE_FOLDER);
		responseDir.mkdirs();

		File confirmation1 = new File(confirmationDir, generateFilename(
				EntityConstants.FileType.CONFIRMATION, constantDate));
		FileUtils
				.copyFile(
						new File(
								"src/test/resources/org/andresoviedo/util/serialization/api2/C_EEEEAAAAMMDDHHMM.txt"),
						confirmation1);
		File confirmation2 = new File(confirmationDir, generateFilename(
				EntityConstants.FileType.CONFIRMATION, constantDate2));
		FileUtils
				.copyFile(
						new File(
								"src/test/resources/org/andresoviedo/util/serialization/api2/C_EEEEAAAAMMDDHHMM.txt"),
						confirmation2);
		File response1 = new File(responseDir, generateFilename(
				EntityConstants.FileType.RESPONSE, constantDate));
		FileUtils
				.copyFile(
						new File(
								"src/test/resources/org/andresoviedo/util/serialization/api2/R_EEEEAAAAMMDDHHMM.txt"),
						response1);
		File response2 = new File(responseDir, generateFilename(
				EntityConstants.FileType.RESPONSE, constantDate2));
		FileUtils
				.copyFile(
						new File(
								"src/test/resources/org/andresoviedo/util/serialization/api2/R_EEEEAAAAMMDDHHMM.txt"),
						response2);

		// configure the server
		FtpServerFactory serverFactory = new FtpServerFactory();
		server = serverFactory.createServer();
		ListenerFactory factory = new ListenerFactory();
		// set the port of the listener
		factory.setPort(freePort);
		// replace the default listener
		serverFactory.addListener("default", factory.createListener());
		// define SSL configuration
		SslConfigurationFactory ssl = new SslConfigurationFactory();
		ssl.setKeystoreFile(new File(
				"src/test/resources/org/andresoviedo/util/ftp/sftp_keystore.jks"));
		ssl.setKeystorePassword("123456");
		// set the SSL configuration for the listener
		factory.setSslConfiguration(ssl.createSslConfiguration());
		factory.setImplicitSsl(false);
		// replace the default listener
		serverFactory.addListener("default", factory.createListener());
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory
				.setFile(new File(
						"src/test/resources/org/andresoviedo/util/ftp/sftp_users.properties"));
		UserManager um = userManagerFactory.createUserManager();
		// BaseUser user = new BaseUser();
		// user.setName("dummy");
		// user.setPassword("ymmud");
		// user.setHomeDirectory(homeDirectory.getPath());
		// user.setAuthorities(Collections.singletonList((Authority) new
		// WritePermission()));
		// um.save(user);
		serverFactory.setUserManager(um);

		// start the ftpServer
		server = serverFactory.createServer();
		// start the ftpServer
		server.start();

		sut = new EasyFTPsClient("dummy", "ymmud", "localhost", freePort);
	}

	@After
	public void destroy() {
		constantDate = null;
		server.stop();
	}

	private static int freePort() throws IOException {
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}

	@Test
	public void testPut() {
		sut.put(generateFilename(EntityConstants.FileType.REQUEST, constantDate),
				getClass()
						.getResourceAsStream(
								"/org/andresoviedo/util/serialization/api2/R_EEEEAAAAMMDDHHMM.txt"));
	}

	@Test
	public void testGet() {
		File outputFolder = new File("target" + File.separator
				+ "generated-test-sources" + File.separator + "output");
		outputFolder.mkdirs();

		File[] f = sut.get(outputFolder,
				EntityConstants.ABSOLUTE_CONFIRMATION_FILES_PATTERN.pattern());
		Assert.assertEquals("Expected to read 2 files", 2, f.length);
		Assert.assertEquals(
				new File(outputFolder, generateFilename(
						EntityConstants.FileType.CONFIRMATION, constantDate)),
				f[0]);
		Assert.assertEquals(
				new File(outputFolder, generateFilename(
						EntityConstants.FileType.CONFIRMATION, constantDate2)),
				f[1]);

		f = sut.get(outputFolder,
				EntityConstants.ABSOLUTE_RESPONSE_FILES_PATTERN.pattern());
		Assert.assertEquals("Expected to read 2 files", 2, f.length);
		Assert.assertEquals(
				new File(outputFolder, generateFilename(
						EntityConstants.FileType.RESPONSE, constantDate)), f[0]);
		Assert.assertEquals(
				new File(outputFolder, generateFilename(
						EntityConstants.FileType.RESPONSE, constantDate2)),
				f[1]);
	}

	public static final String generateFilename(
			EntityConstants.FileType fileType, Date date) {
		return String.format(EntityConstants.FILE_FORMAT, fileType.code, date);
	}
}
