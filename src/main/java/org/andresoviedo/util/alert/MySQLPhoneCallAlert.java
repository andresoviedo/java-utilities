package org.andresoviedo.util.alert;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andresoviedo.util.bean.CallInfoBean;
import org.andresoviedo.util.bean.MailInfoBean;
import org.andresoviedo.util.log.jul.DefaultFileFormat;
import org.andresoviedo.util.log.jul.MailHandler;
import org.andresoviedo.util.modem.ModemManager;

public class MySQLPhoneCallAlert {

	/**
	 * @param args
	 *            pollingTime_in_millis sql phoneNumbers_separated_by_comma
	 *            comm_port
	 */
	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(new File(
					"queries.properties"));
			prop.load(fis);
			fis.close();
			new MySQLPhoneCallAlert(Long.parseLong(args[0]), args[1],
					prop.getProperty("query", null), args[3].split(";"),
					args[4], Long.parseLong(args[5]),
					Integer.parseInt(args[6]), args[7],
					args.length >= 9 ? args[8] : null, args.length >= 10
							&& args[9].length() > 0 ? args[9] : "url_send_sms",
					prop.getProperty("debug", null), Boolean.valueOf(args[11]));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Alert identifier
	 */
	private String name;
	private long pollingTime;
	private int retryCount;
	private long retryTimeout;
	private String sqlHost;
	private String sql;
	private String debugQuery;
	private String[] phoneNumbers;
	private boolean doCalls;
	private String commPort;

	private String smsPushURL;

	private Timer timer;
	private Logger logger = Logger.getLogger("");
	private String mailInfoFilename;

	public MySQLPhoneCallAlert(long pollingTime, String sqlHost, String sql,
			String[] phoneNumbers, String commPort, long retryTimeout,
			int retryCount, String name, String mailInfoFilename,
			String smsPushURL, String debugQuery, boolean doCalls) {
		this.pollingTime = pollingTime;
		this.sqlHost = sqlHost;
		this.sql = sql;
		this.debugQuery = debugQuery;
		this.phoneNumbers = phoneNumbers;
		this.doCalls = doCalls;
		this.commPort = commPort;
		this.retryTimeout = retryTimeout;
		this.retryCount = retryCount;
		this.name = name;
		this.mailInfoFilename = mailInfoFilename;
		this.smsPushURL = smsPushURL;
		init();
	}

	private void init() {
		try {
			logger.logp(Level.INFO, "AlertApp", "init",
					"Initializing application...");
			initLogger();
			logger.logp(Level.INFO, "AlertApp", "init",
					"Application initialized with host[" + sqlHost + "] sql["
							+ sql + "], debugQuery[" + debugQuery
							+ "], pollingTime[" + pollingTime
							+ "], phoneNumber[" + phoneNumbers + "], doCalls["
							+ doCalls + "], commPort[" + commPort
							+ "] retryTimeout[" + retryTimeout
							+ "] retryCount[" + retryCount + "], name[" + name
							+ "] mailInfoFilename[" + mailInfoFilename
							+ "] smsPushURL[" + smsPushURL + "]");

			timer = new Timer();
			timer.schedule(new MySQLTask(), 0, pollingTime);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void initLogger() throws IOException, UnsupportedEncodingException {
		logger.logp(Level.INFO, "AlertApp", "initLogger",
				"Initializing logger...");

		// configure logger
		logger.setLevel(Level.ALL);

		// configure file handler
		File temp = new File(new File(".\\alert_%g.log").getAbsolutePath());
		if (temp.getParent() != null && !temp.getParentFile().exists()) {
			temp.getParentFile().mkdirs();
		}
		FileHandler fileHandler = new FileHandler(temp.getAbsolutePath(),
				10485760, 2, true);
		fileHandler.setFormatter(new DefaultFileFormat());
		fileHandler.setLevel(Level.ALL);
		fileHandler.setEncoding("UTF-8");
		logger.addHandler(fileHandler);

		// configure mail handler
		if (mailInfoFilename != null) {
			InputStream mailInfoFileIs = null;
			try {
				mailInfoFileIs = new FileInputStream(mailInfoFilename);
				XMLDecoder xmlDecoder = new XMLDecoder(mailInfoFileIs);
				MailInfoBean mailInfoBean = (MailInfoBean) xmlDecoder
						.readObject();
				mailInfoBean.setSubject("Alertas " + name);
				logger.addHandler(new MailHandler(mailInfoBean, 10,
						Level.WARNING, new DefaultFileFormat()));
				logger.logp(Level.INFO, "AlertApp", "initLogger",
						"Found mail info. It's been set for logger");
			} catch (FileNotFoundException ex) {
				// Do nothing
				logger.logp(Level.INFO, "AlertApp", "initLogger",
						"File mailInfo.xml not found. Mails with logs will not be sent");
			} catch (Exception ex) {
				logger.logp(Level.SEVERE, "AlertApp", "initLogger",
						ex.getMessage());
			} finally {
				if (mailInfoFileIs != null) {
					mailInfoFileIs.close();
				}
			}
		}
		logger.logp(Level.INFO, "AlertApp", "initLogger", "Logger initialized");
	}

	private void alert(String why) {
		logger.logp(Level.INFO, "AlertApp", "alert", "Alerting: " + why); // for
																			// mail
																			// alert
																			// in
																			// SEVERE
																			// level
		for (String phoneNumber : phoneNumbers) {
			// alert by SMS

			// alert by call
			if (doCalls) {
				CallInfoBean callInfo = new CallInfoBean();
				callInfo.setPortName(commPort);
				callInfo.setTelephoneNr(phoneNumber);
				callInfo.setWaitSecs(60);
				try {
					logger.logp(Level.INFO, "AlertApp", "alert", "Alerting ["
							+ callInfo.getTelephoneNr() + "]...");
					ModemManager.call(callInfo);
				} catch (Exception ex) {
					logger.logp(Level.INFO, "AlertApp", "alert",
							ex.getMessage());
				}
			}
		}
		logger.logp(Level.INFO, "AlertApp", "alert", "Alerting finalized.");
	}

	class MySQLTask extends TimerTask {

		Logger logger = Logger.getLogger("");

		public MySQLTask() {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void run() {
			run(retryCount);
		}

		public void run(int retryCount) {
			Connection conn = null;

			boolean isReachable = false;
			try {
				// Test whether destination is reachable
				logger.logp(Level.INFO, "MySQLTask", "run", "Pinging host...");
				isReachable = InetAddress.getByName(sqlHost).isReachable(60000);

				logger.logp(Level.INFO, "MySQLTask", "run", "Checking...");
				conn = DriverManager.getConnection("jdbc:mysql://" + sqlHost
						+ "/?user=anonymous&password=12345");

				PreparedStatement statement = conn.prepareStatement(sql);
				ResultSet result = statement.executeQuery();
				boolean doAlert = false;
				if (result.next()) {
					if (result.getInt(1) == 0) { // error=0, ok=1
						doAlert = true;
					} else {
						logger.logp(Level.INFO, "MySQLTask", "run", "-> OK!");
					}
				}
				statement.close();

				if (doAlert) {
					String why = name;
					if (debugQuery != null) {
						try {
							statement = conn.prepareStatement(debugQuery);
							result = statement.executeQuery();
							int cols = result.getMetaData().getColumnCount();
							StringBuffer detail = new StringBuffer();
							while (result.next()) {
								detail.append(result.getString(1) + " ");
								StringBuffer line = new StringBuffer(
										result.getString(1));
								for (int i = 2; i <= cols; i++) {
									line.append("," + result.getString(i));
								}
								logger.logp(Level.INFO, "MySQLTask", "run",
										"debug: " + line.toString());
							}
							statement.close();
							why = MessageFormat.format(why, detail.toString()
									.trim());
						} catch (Exception ex) {
							logger.throwing("MySQLTask", "run", ex);
							logger.logp(Level.SEVERE, "MySQLTask", "run",
									ex.getMessage());
						}
					}
					logger.logp(Level.WARNING, "MySQLTask", "run", "-> " + why);
					alert(why);
				}
			} catch (Exception ex) {
				// Test as many times as retryCount parameter. Anyway, if
				// destination was reachable, then alert immediately
				if (retryCount <= 0) {
					logger.logp(Level.WARNING, "MySQLTask", "run",
							"Destination " + (isReachable ? "was" : "not")
									+ " reachable. " + ex.getMessage());
					alert((!isReachable ? "Server is not reachable. " : "")
							+ "Exception: " + ex.getMessage());
				} else {
					logger.logp(Level.WARNING, "MySQLTask", "run",
							ex.getMessage());
					try {
						Thread.sleep(retryTimeout);
						run(retryCount - 1);
					} catch (InterruptedException ex2) {
						logger.logp(Level.WARNING, "MySQLTask", "run",
								"Thread interrupted");
					}
				}
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (Exception ex) {
					}
				}
			}
		}
	}
}