package org.andresoviedo.util.data;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de utilidades para el que no disponga de un ORM.
 * 
 * @author andresoviedo
 */
public final class ORMUtils {

	private static Logger logger = LoggerFactory.getLogger(ORMUtils.class);

	private ORMUtils() {
	}

	/**
	 * Devuelve las columnas que estan anotadas con {@link Column}
	 * 
	 * @param clazz
	 *            la clase a introspeccionar
	 * @return las columnas que estan anotadas con {@link Column}
	 */
	public static Map<String, Field> getColumnsFields(Class<?> clazz) {
		Map<String, Field> ret = new HashMap<String, Field>();
		try {
			for (Field field : clazz.getDeclaredFields()) {
				final Column annotation = field.getAnnotation(Column.class);
				if (annotation != null && StringUtils.isNotBlank(annotation.name())) {
					field.setAccessible(true);
					ret.put(annotation.name(), field);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * Devuelve los valores del bean mapeados con un prefijo. Requiere de haber llamado antes a {@link #getColumnsFields(Class)}
	 * 
	 * @param prefix
	 *            el prefijo de las claves
	 * @param columnsFields
	 *            el mapeo devuelto por {@link #getColumnsFields(Class)}
	 * @param bean
	 *            el bean de donde recuperar los valores
	 * @return los valores del bean mapeados con un prefijo
	 */
	public static Map<String, Object> getColumnsValues(String prefix, Map<String, Field> columnsFields, Object bean) {
		if (bean == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			for (Map.Entry<String, Field> entry : columnsFields.entrySet()) {
				Object value = entry.getValue().get(bean);
				if (value != null) {
					if (value.getClass().isEnum()) {
						value = ((Enum<?>) value).name();
					}
					ret.put(prefix + entry.getKey(), value);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}

	public static Object[] generateQuery(Class<?> entity) {
		return generateQuery(getEntityMetadata(entity));
	}

	public static Object[] generateQuery(EntityMetadata entityMetadata) {
		final StringBuilder selectString = new StringBuilder("SELECT ");
		final StringBuilder tableAndJoinString = new StringBuilder(" FROM ");
		final Map<String, String> fieldAlias = new HashMap<String, String>();
		Object[] ret = new Object[2];
		generateQuery_impl(null, null, 0, selectString, tableAndJoinString, entityMetadata, fieldAlias);
		ret[0] = selectString.replace(7, 8, "") + tableAndJoinString.toString();
		ret[1] = fieldAlias;
		return ret;
	}

	public static void generateQuery_impl(String parentTableAlias, String parentForeignKey, int tableIdx, StringBuilder selectString,
			StringBuilder tableAndJoinString, EntityMetadata meta, Map<String, String> allFieldAlias) {

		String tableAlias = "T" + tableIdx;

		// construcción de la query
		if (meta.parent == null) {
			tableAndJoinString.append(" ").append(meta.schema).append(".").append(meta.tableName);
			tableAndJoinString.append(" ").append(tableAlias);
		} else {
			tableAndJoinString.append(" INNER JOIN ").append(meta.schema).append(".").append(meta.tableName);
			tableAndJoinString.append(" ").append(tableAlias);
			tableAndJoinString.append(" ON ").append(parentTableAlias).append(".").append(parentForeignKey);
			tableAndJoinString.append("=").append(tableAlias).append(".").append(meta.primaryKey);
		}

		int i = 0;
		for (Map.Entry<String, Field> entry : meta.fields.entrySet()) {
			String fieldAlias = tableAlias + "C" + i++;
			selectString.append(",").append(tableAlias).append(".").append(entry.getKey());
			selectString.append(" AS \"").append(fieldAlias);
			selectString.append("\"");

			String propertyName = null;
			if (StringUtils.isNotBlank(meta.fieldPath)) {
				propertyName = meta.fieldPath + "." + entry.getValue().getName();
			} else {
				propertyName = entry.getValue().getName();
			}
			allFieldAlias.put(fieldAlias, propertyName);
		}

		for (Map.Entry<String, EntityMetadata> join : meta.joins.entrySet()) {
			generateQuery_impl(tableAlias, join.getKey(), ++tableIdx, selectString, tableAndJoinString, join.getValue(), allFieldAlias);
		}
	}

	public static ORMUtils.EntityMetadata getEntityMetadata(Class<?> entity) {
		return getEntityMetadata(entity, null, null);
	}

	public static ORMUtils.EntityMetadata getEntityMetadata(Class<?> entity, String parentFieldName, EntityMetadata parent) {

		// validaciones
		if (entity == null) {
			throw new IllegalArgumentException("Argument can't be null");
		}
		Entity entityAnnotation = entity.getAnnotation(Entity.class);
		if (entityAnnotation == null) {
			throw new IllegalArgumentException("La clase '" + entity + "' no esta anotada con @Entity");
		}
		Table tableAnnotation = entity.getAnnotation(Table.class);
		if (tableAnnotation == null || StringUtils.isBlank(tableAnnotation.schema()) || StringUtils.isBlank(tableAnnotation.name())) {
			throw new IllegalArgumentException("La clase '" + entity
					+ "' no esta anotada con @Table o le falta informar los atributos 'schema' o 'name'.");
		}

		String schema = tableAnnotation.schema();
		String tableName = tableAnnotation.name();

		Map<String, Field> fieldsFound = new HashMap<String, Field>();
		Map<String, EntityMetadata> joinsFound = new HashMap<String, EntityMetadata>();

		String primaryKey = null;
		Id idAnnotation = null;
		EntityMetadata thisMetadata = new EntityMetadata();
		thisMetadata.fieldPath = parent == null ? "" : StringUtils.isBlank(parent.fieldPath) ? parentFieldName : parent.fieldPath + "."
				+ parentFieldName;
		for (Field field : entity.getDeclaredFields()) {
			// TODO: avisar al usuario cuando no ha informado el atributo "name"
			final Column columnAnnotation = field.getAnnotation(Column.class);
			if (columnAnnotation != null && StringUtils.isNotBlank(columnAnnotation.name())) {
				field.setAccessible(true);
				fieldsFound.put(columnAnnotation.name(), field);
			}
			final JoinColumn joinAnnotation = field.getAnnotation(JoinColumn.class);
			if (joinAnnotation != null && StringUtils.isNotBlank(joinAnnotation.name())) {
				field.setAccessible(true);
				final EntityMetadata joinMetadata = getEntityMetadata(field.getType(), field.getName(), thisMetadata);
				joinsFound.put(joinAnnotation.name(), joinMetadata);
			}
			if (idAnnotation == null && columnAnnotation != null) {
				idAnnotation = field.getAnnotation(Id.class);
				if (idAnnotation != null) {
					primaryKey = columnAnnotation.name();
				}
			}
		}
		if (primaryKey == null) {
			throw new IllegalArgumentException("La clase '" + entity + "' no tiene ningún campo anotado con @Id");
		}

		thisMetadata.parent = parent;
		thisMetadata.schema = schema;
		thisMetadata.tableName = tableName;
		thisMetadata.primaryKey = primaryKey;
		thisMetadata.fields = fieldsFound;
		thisMetadata.joins = joinsFound;
		return thisMetadata;
	}

	public static class EntityMetadata {
		String schema;
		String tableName;
		String primaryKey;

		EntityMetadata parent;
		Map<String, Field> fields;
		Map<String, EntityMetadata> joins;

		String sql;
		String fieldPath;

		public EntityMetadata() {
			this(null, null, null);
		}

		public EntityMetadata(String schema, String tableName, String primaryKey) {
			this(null, schema, tableName, primaryKey, new HashMap<String, Field>(), new HashMap<String, EntityMetadata>());
		}

		public EntityMetadata(EntityMetadata parent, String schema, String tableName, String primaryKey, Map<String, Field> fields,
				Map<String, EntityMetadata> joins) {
			super();
			this.fields = fields;
			this.schema = schema;
			this.tableName = tableName;
			this.primaryKey = primaryKey;
			this.joins = joins;
		}

	}
}
