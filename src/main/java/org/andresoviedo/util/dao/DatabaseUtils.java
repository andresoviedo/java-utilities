package org.andresoviedo.util.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public final class DatabaseUtils {

	public static Set<String> listTables(Connection jdbcConnection) throws SQLException {
		DatabaseMetaData m = jdbcConnection.getMetaData();
		ResultSet tables = m.getTables(null, null, "%", null);
		Set<String> ret = new HashSet<String>();
		while (tables.next()) {
			ret.add(tables.getString(3));
		}
		return ret;
	}
}
