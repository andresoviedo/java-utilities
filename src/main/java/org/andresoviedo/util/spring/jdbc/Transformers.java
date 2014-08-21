package org.andresoviedo.util.spring.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;

import org.andresoviedo.util.bean.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

final public class Transformers {

	private static Logger logger = LoggerFactory.getLogger(Transformers.class);

	public static <T> RowMapper<T> createRowMapper(final Class<T> target) {
		return new RowMapper<T>() {

			private final Map<String, String> columnAliases = new HashMap<String, String>();
			{
				ReflectionUtils.doWithFields(target, new FieldCallback() {
					public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
						Column annotation = field.getAnnotation(Column.class);
						if (annotation != null && StringUtils.isNotBlank(annotation.name())) {
							columnAliases.put(annotation.name().toUpperCase(), field.getName());
						} else {
							columnAliases.put(field.getName().toUpperCase(), field.getName());
						}
					}
				});
			}

			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				try {
					T ret = target.newInstance();
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						String columnName = rs.getMetaData().getColumnLabel(i);
						if (columnAliases.containsKey(columnName)) {
							columnName = columnAliases.get(columnName);
						} else if (columnAliases.containsKey(columnName.toUpperCase())) {
							columnName = columnAliases.get(columnName.toUpperCase());
						}
						try {
							BeanUtils.setProperty(ret, columnName, rs.getObject(i));
						} catch (IllegalArgumentException ex) {
							logger.warn(ex.getMessage());
						}
					}
					return ret;
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e);
				}
			}
		};
	}

	public static <T> ResultSetExtractor<T> createResultSetExtractor(final Class<T> target) {
		return new ResultSetExtractor<T>() {

			private final Map<String, String> columnAliases = new HashMap<String, String>();
			{
				ReflectionUtils.doWithFields(target, new FieldCallback() {
					public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
						Column annotation = field.getAnnotation(Column.class);
						if (annotation != null && StringUtils.isNotBlank(annotation.name())) {
							columnAliases.put(annotation.name().toUpperCase(), field.getName());
						} else {
							columnAliases.put(field.getName().toUpperCase(), field.getName());
						}
					}
				});
			}

			@Override
			public T extractData(ResultSet rs) throws SQLException, DataAccessException {
				try {
					T ret = null;
					if (rs.next()) {
						ret = target.newInstance();
						for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
							String columnName = rs.getMetaData().getColumnLabel(i);
							if (columnAliases.containsKey(columnName)) {
								columnName = columnAliases.get(columnName);
							} else if (columnAliases.containsKey(columnName.toUpperCase())) {
								columnName = columnAliases.get(columnName.toUpperCase());
							}
							try {
								BeanUtils.setProperty(ret, columnName, rs.getObject(i));
							} catch (IllegalArgumentException ex) {
								logger.warn(ex.getMessage());
							}
						}
					}
					return ret;
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
