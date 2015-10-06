package org.andresoviedo.util.dao;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public final class DataSourceDefinition {

	private static Logger LOG = Logger.getLogger(DataSourceDefinition.class);

	private final String databaseDriver;

	private final String url;

	private final String databaseScripts;

	private final BasicDataSource datasource;

	public DataSourceDefinition(Properties prop, String propertyPrefix) {
		this.databaseDriver = prop.getProperty(propertyPrefix + "database.driver");
		this.url = prop.getProperty(propertyPrefix + "database.url");

		this.datasource = setupDataSource(url);
		this.databaseScripts = prop.getProperty(propertyPrefix + "database.scripts");

		init();
	}

	private void init() {
		testConnection();
		initializeDatabase();
	}

	private void testConnection() {
		LOG.debug("Testing datasource connection '" + url + "'...");
		Connection conn = null;
		try {
			conn = getDataSource().getConnection();
			// INFO: this works with H2,MySQL so far
			final Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery("SELECT 1;");
			if (!result.next() || result.getInt(1) != 1) {
				throw new RuntimeException("SELECT 1 didn't work!");
			}
			result.close();
			statement.close();
			LOG.debug("Datasource connection OK!");
		} catch (SQLException ex) {
			LOG.fatal(ex);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex2) {
				LOG.fatal(ex2);
			}
		}
	}

	private void initializeDatabase() {
		if (StringUtils.isBlank(databaseScripts)) {
			return;
		}

		String[] scripts = databaseScripts.split(",");

		LOG.info("Initializing database with '" + Arrays.toString(scripts) + "'...");
		Connection conn = null;
		try {
			conn = getDataSource().getConnection();
			ScriptRunner runner = new ScriptRunner(conn, false, true);
			for (String script : scripts) {
				LOG.debug("Executing script '" + script + "'...");
				InputStream scriptAsStream = this.getClass().getResourceAsStream(script);
				runner.runScript(new InputStreamReader(scriptAsStream, "UTF-8"));
			}
			LOG.info("Database initialized!");
		} catch (Exception ex) {
			LOG.fatal(ex);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex2) {
				LOG.fatal(ex2);
			}
		}
	}

	public DataSource getDataSource() {
		return datasource;
	}

	private BasicDataSource setupDataSource(String connectURI) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(databaseDriver);
		ds.setUrl(connectURI);
		ds.setTestOnBorrow(true);
		ds.setValidationQuery("SELECT 1");
		return ds;
	}

	public void close() {
		try {
			datasource.close();
			// connection = null;
		} catch (SQLException ex) {
			String msg = "Exception closing connection";
			LOG.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
	}

	// // Inititialization when using MySQL with JDBC
	// static {
	// try {
	// // The newInstance() call is a work around for some
	// // broken Java implementations
	// Class.forName("com.mysql.jdbc.Driver").newInstance();
	// } catch (Exception ex) {
	// String msg = "Couldn't instantiate MySQL DAO";
	// LOG.fatal(msg, ex);
	// throw new RuntimeException(msg, ex);
	// }
	// }

	// try {
	// connection = DriverManager.getConnection(url);
	// connection.setAutoCommit(false);
	// } catch (SQLException ex) {
	// String msg = "Couldn't get connection to database";
	// LOG.fatal(msg, ex);
	// throw new RuntimeException(msg, ex);
	// }
}
