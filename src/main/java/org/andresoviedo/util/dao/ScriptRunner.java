package org.andresoviedo.util.dao;

/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class
 * from the iBATIS Apache project. Only removed dependency on Resource class
 * and a constructor 
 */
/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tool to run database scripts
 */
public class ScriptRunner {

	private static final String DEFAULT_DELIMITER = ";";

	private Connection connection;

	private boolean stopOnError;
	private boolean autoCommit;

	private Logger logWriter = Logger.getLogger(ScriptRunner.class.getName());
	private Logger errorLogWriter = Logger.getLogger(ScriptRunner.class.getName());

	private String delimiter = DEFAULT_DELIMITER;
	private boolean fullLineDelimiter = false;

	/**
	 * Default constructor
	 */
	public ScriptRunner(Connection connection, boolean autoCommit, boolean stopOnError) {
		this.connection = connection;
		this.autoCommit = autoCommit;
		this.stopOnError = stopOnError;
	}

	public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
		this.delimiter = delimiter;
		this.fullLineDelimiter = fullLineDelimiter;
	}

	/**
	 * Runs an SQL script (read in using the Reader parameter)
	 *
	 * @param reader
	 *            - the source of the script
	 */
	public void runScript(Reader reader) throws IOException, SQLException {
		try {
			boolean originalAutoCommit = connection.getAutoCommit();
			try {
				if (originalAutoCommit != this.autoCommit) {
					connection.setAutoCommit(this.autoCommit);
				}
				runScript(connection, reader);
			} finally {
				connection.setAutoCommit(originalAutoCommit);
			}
		} catch (IOException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Error running script.  Cause: " + e, e);
		}
	}

	/**
	 * Runs an SQL script (read in using the Reader parameter) using the connection passed in
	 *
	 * @param conn
	 *            - the connection to use for the script
	 * @param reader
	 *            - the source of the script
	 * @throws SQLException
	 *             if any SQL errors occur
	 * @throws IOException
	 *             if there is an error reading from the Reader
	 */
	private void runScript(Connection conn, Reader reader) throws IOException, SQLException {
		int commandsExecuted = 0;
		StringBuffer command = null;
		try {
			LineNumberReader lineReader = new LineNumberReader(reader);
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				if (command == null) {
					command = new StringBuffer();
				}
				String trimmedLine = line.trim();
				if (trimmedLine.startsWith("--")) {
					println(trimmedLine);
				} else if (trimmedLine.length() < 1 || trimmedLine.startsWith("//")) {
					// Do nothing
				} else if (trimmedLine.length() < 1 || trimmedLine.startsWith("--")) {
					// Do nothing
				} else if (trimmedLine.toLowerCase().startsWith("delimiter ")) {
					int delimiterPos = line.toLowerCase().indexOf("delimiter ") + "delimiter ".length();
					setDelimiter(line.substring(delimiterPos).trim(), false);
				} else if (!fullLineDelimiter && trimmedLine.endsWith(getDelimiter()) || fullLineDelimiter
						&& trimmedLine.equals(getDelimiter())) {
					command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
					command.append(" ");
					Statement statement = conn.createStatement();

					println(command.toString());

					boolean hasResults = false;
					if (stopOnError) {
						hasResults = statement.execute(command.toString());
					} else {
						try {
							statement.execute(command.toString());
						} catch (SQLException e) {
							e.fillInStackTrace();
							printlnError("Error executing: " + command);
							printlnError(e);
						}
					}
					commandsExecuted++;

					if (autoCommit && !conn.getAutoCommit()) {
						conn.commit();
					}

					ResultSet rs = statement.getResultSet();
					if (hasResults && rs != null) {
						ResultSetMetaData md = rs.getMetaData();
						int cols = md.getColumnCount();
						for (int i = 0; i < cols; i++) {
							String name = md.getColumnLabel(i);
							print(name + "\t");
						}
						println("");
						while (rs.next()) {
							for (int i = 0; i < cols; i++) {
								String value = rs.getString(i);
								print(value + "\t");
							}
							println("");
						}
					}

					command = null;
					try {
						statement.close();
					} catch (Exception e) {
						// Ignore to workaround a bug in Jakarta DBCP
					}
					Thread.yield();
				} else {
					command.append(line);
					command.append("\n");
				}
			}
			if (!autoCommit) {
				conn.commit();
			}
			if (command != null && command.length() > 0 && commandsExecuted == 0){
				printlnError("Found commands but nothing was executed. Did you add the delimiter '" + delimiter
						+ "' to the end of the command?");
			}
		} catch (SQLException e) {
			e.fillInStackTrace();
			printlnError("Error executing: " + command);
			printlnError(e);
			throw e;
		} catch (IOException e) {
			e.fillInStackTrace();
			printlnError("Error executing: " + command);
			printlnError(e);
			throw e;
		} finally {
			conn.rollback();

		}
	}

	private String getDelimiter() {
		return delimiter;
	}

	private void print(String o) {
		if (logWriter != null) {
			logWriter.fine(o);
		}
	}

	private void println(String o) {
		if (logWriter != null) {
			logWriter.finest(o);
		}
	}
	
	private void printlnError(String ex) {
		if (errorLogWriter != null) {
			errorLogWriter.log(Level.SEVERE, ex);
		}
	}

	private void printlnError(Exception ex) {
		if (errorLogWriter != null) {
			errorLogWriter.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
