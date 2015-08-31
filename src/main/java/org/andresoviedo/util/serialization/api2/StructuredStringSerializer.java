package org.andresoviedo.util.serialization.api2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.log4j.Logger;

public final class StructuredStringSerializer<T> {

	private static final Logger LOG = Logger.getLogger(StructuredStringSerializer.class);

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Pattern to extend actual java Pattern <code>%[argument_index$][flags][width][.precision]conversion</code> with a flag that is the
	 * padding char.
	 */
	private static final Pattern formatPattern = Pattern.compile("%(\\d+\\$)?('.')?(?:[-#+ ,(])?(\\d+)[sSd]");

	private final Class<T> clazz;

	private final Collection<SerializableField> serializableFields;

	public StructuredStringSerializer(Class<T> clazz) {
		this.clazz = clazz;
		LOG.debug("Building info for class '" + clazz + "'...");
		serializableFields = SerializableField.build(clazz);
		LOG.info("Serializable fields: " + serializableFields);
	}

	public String serialize(T object) {
		StringBuilder ret = new StringBuilder("");
		serializeImpl(ret, object, serializableFields);

		// TODO: move this from here. remove last character '\n'
		ret.deleteCharAt(ret.length() - 1);

		return ret.toString();
	}

	private void serializeImpl(StringBuilder ret, Object obj, Collection<SerializableField> sfs) {
		LOG.debug("Serializing...");
		for (SerializableField f : sfs) {
			if (f.isRecord) {
				if (!f.isList) {
					serializeImpl(ret, f.getValue(obj), f.childs);
					ret.append(LINE_SEPARATOR);
				} else {
					for (Object item : (List<?>) f.getValue(obj)) {
						serializeImpl(ret, item, f.childs);
						ret.append(LINE_SEPARATOR);
					}
				}
			} else {
				ret.append(f.getValue(obj));
			}
		}

	}

	public T deserialize(File f) {
		LOG.debug("Deserializing file '" + f.getAbsolutePath() + "'...");
		BOMInputStream is = null;
		try {
			is = new BOMInputStream(new FileInputStream(f));
			return deserialize(new Scanner(is));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					LOG.error("Couldn't close input file stream: " + ex.getMessage(), ex);
				}
			}
		}
	}

	public T deserialize(Scanner inputStream) {
		return deserialize(inputStream.useDelimiter("\\Z").next());
	}

	public T deserialize(String objectSerialized) {
		LOG.debug("Deserializing... '" + objectSerialized + "'");
		T ret = null;

		try {
			ret = clazz.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Exception instantiating class '" + clazz + "'", ex);
		}

		// unordered fields
		Map<String, SerializableField> unorderedRecords = new HashMap<String, StructuredStringSerializer.SerializableField>();
		for (SerializableField sf : serializableFields) {
			LOG.info("Analyzing '" + sf + "'...");
			if (sf.isUnordered()) {
				LOG.info("Analyzed OK '" + sf + "'...");
				unorderedRecords.put(sf.listItemId, sf);
			}
		}

		deserializeImpl(ret, serializableFields, unorderedRecords, new StringBuilder(objectSerialized), 0);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public void deserializeImpl(Object currentObj, Collection<SerializableField> fields, Map<String, SerializableField> unorderedRecords,
			StringBuilder sb, int pos) {

		while (sb.indexOf("\n") == 0 || sb.indexOf("\r") == 0) {
			sb.deleteCharAt(0);
			pos++;
		}

		LOG.info("Deserializing class '" + currentObj.getClass().getSimpleName() + "'...");

		for (SerializableField f : fields) {
			try {
				// try to deserialize unordered records
				if (unorderedRecords != null && !unorderedRecords.isEmpty()) {
					boolean tryToDeserialize = true;
					while (tryToDeserialize) {
						LOG.info("Trying to deserialize with '" + unorderedRecords.keySet() + "'...");
						tryToDeserialize = false;
						for (Map.Entry<String, SerializableField> ue : unorderedRecords.entrySet()) {
							Object actualObject = ue.getValue().getValue(currentObj);
							if (ue.getValue().recordRegex.matcher(sb.toString()).find()) {
								tryToDeserialize = true;
								LOG.info("Instantiating new (unordered) item  with type '" + ue.getValue().listType + "' for field '"
										+ ue.getValue().field.getName() + "'...");
								if (ue.getValue().isList) {
									Object newItem = ue.getValue().newListItemInstance();
									((List<Object>) actualObject).add(newItem);
									actualObject = newItem;
								}
								deserializeImpl(actualObject, ue.getValue().childs, null, sb, pos);
							}
						}

					}
				}

				if (f.isRecord) {
					Object actualObject = f.getValue(currentObj);
					List<Object> actualList = null;
					if (f.isList) {
						actualList = (List<Object>) actualObject;
					}

					boolean first = true;

					while ((!f.isList && first) || (f.isList && f.recordRegex.matcher(sb.toString()).find())) {
						if (f.isList) {
							LOG.info("Instantiating new item  with type '" + f.listType + "' for field '" + f.field.getName() + "'...");
							Object newItem = f.newListItemInstance();
							actualList.add(newItem);
							actualObject = newItem;
						}

						deserializeImpl(actualObject, f.childs, null, sb, pos);

						first = false;
					}

					continue;
				}

				int fs = f.getFormatSize();
				int end = fs;
				String value = sb.substring(0, end);
				if (!f.isConstant) {
					f.setValue(currentObj, value);
					LOG.info("-" + currentObj.getClass().getSimpleName() + "#" + f.field.getName() + " [" + fs + "]='" + value + "'");
				} else {
					LOG.info("-" + currentObj.getClass().getSimpleName() + "#" + f.field.getName() + " [" + fs + "]='" + value + "'");
				}
				sb.delete(0, end);
				pos += end;
			} catch (Exception ex) {
				String errorMsg = "Exception deserializing field '" + currentObj.getClass().getSimpleName() + "#" + f.field.getName()
						+ "' at pos '" + pos + "' in '" + sb + "'";
				LOG.fatal(errorMsg, ex);
				throw new RuntimeException(errorMsg, ex);
			}
		}
	}

	static class SerializableField {

		final Class<?> enclosingClass;
		final StringField classAnnotation;
		final Field field;
		final StringField annotation;
		final boolean isRecord;
		final boolean isList;
		final Class<?> listType;
		final String listItemId;
		final Pattern recordRegex;
		final boolean isField;
		final boolean isConstant;
		final Object value;
		final SimpleDateFormat dateFormat;
		final Collection<SerializableField> childs;
		final Map<String, SerializableField> childsWithoutOrder;

		private static Collection<SerializableField> build(Class<?> clazz) {
			LOG.debug("Building serialization info for class '" + clazz + "'...");
			// Set<SerializableField> recordFields = new TreeSet<SerializableField>(recordComparator);
			List<SerializableField> recordFields = new ArrayList<SerializableField>();
			for (Field field : clazz.getDeclaredFields()) {
				if (field.getAnnotation(StringField.class) != null) {
					recordFields.add(new SerializableField(clazz, field));
				}
			}
			// return Collections.unmodifiableSet(recordFields);
			return Collections.unmodifiableList(recordFields);
		}

		public SerializableField(Class<?> clazz, Field field) {
			super();
			this.enclosingClass = clazz;
			this.field = field;
			this.annotation = field.getAnnotation(StringField.class);
			this.isList = List.class.isAssignableFrom(field.getType());
			this.isRecord = annotation.order() != -1 || (annotation.order() == -1 && annotation.offset() == -1);
			this.isField = !isRecord;

			LOG.debug("-Building field '" + field.getName() + "'" + (isRecord ? " (Record)" : "") + "...");

			if (isList) {
				listType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
				this.classAnnotation = listType.getAnnotation(StringField.class);
				if (classAnnotation == null || "".equals(classAnnotation.id())) {
					LOG.warn("The class is an item list '" + listType + "' but the @'" + StringField.class.getSimpleName()
							+ "' annotation is missing or the attribute #id was not set. "
							+ "You won't be able to deserialize this type of variable length records");
					listItemId = null;
					recordRegex = null;
				} else {
					listItemId = classAnnotation.id();
					recordRegex = Pattern.compile("(?s)^[\\r\\n]*" + listItemId + ".*");
					LOG.info("Found List for field '" + field.getName() + "' with type '" + listType + "' and item id '" + listItemId
							+ "'...");
				}
			} else {
				this.classAnnotation = clazz.getAnnotation(StringField.class);
				listType = null;
				listItemId = null;
				recordRegex = null;
			}

			Collection<SerializableField> tempChilds = null;
			if (isRecord) {
				if (!isList) {
					tempChilds = build(field.getType());
				} else {
					Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					LOG.info("Found List for field '" + field.getName() + "' with type '" + genericType + "'...");
					tempChilds = build(genericType);
				}
			}

			if (tempChilds != null) {
				// LOG.info("Looking in '" + clazz + "' for indexed record '" + listItemId + "'...");
				childsWithoutOrder = null;
				this.childs = Collections.unmodifiableCollection(tempChilds);
			} else {
				this.childs = null;
				this.childsWithoutOrder = null;
			}

			this.isConstant = Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers());
			field.setAccessible(true);
			if (isConstant) {
				if (String.class.isAssignableFrom(field.getType()) || Integer.class.isAssignableFrom(field.getType())
						|| Integer.TYPE == field.getType()) {
					try {
						value = formatValue(field.get(null));
						LOG.debug("Found constant value for '" + field.getName() + "'='" + value + "'");
					} catch (Exception ex) {
						throw new IllegalStateException("Couldn't get constant value from '" + field.getName() + "'", ex);
					}
				} else {
					throw new UnsupportedOperationException("Found constant value for '" + field.getName() + "', but type ("
							+ field.getType() + ") is not supported yet");
				}
			} else {
				value = null;
			}

			if (Date.class.isAssignableFrom(field.getType())) {
				dateFormat = new SimpleDateFormat(annotation.format());
			} else {
				dateFormat = null;
			}

		}

		boolean isUnordered() {
			return (isRecord || isList) && listItemId != null && annotation.order() == -1;
		}

		Object newListItemInstance() {
			if (!isList) {
				throw new IllegalStateException("Tried to instantiate the non list item '" + field.getName() + "'");
			}
			try {
				return listType.newInstance();
			} catch (Exception ex) {
				throw new RuntimeException("Exception instantiating list item class '" + listType + "'");
			}
			// TODO: this was in the main for... i should put this somewhere but i'll think about it later...
			// if (f.listItemId == null) {
			// String errorMsg = "A list was found to deserialize, but the item type '"
			// + genericType.getName() + "' is missing the @" + StringField.class.getSimpleName()
			// + " or #id attribute was not set.";
			// LOG.fatal(errorMsg);
			// throw new RuntimeException(errorMsg);
			// }
		}

		Object getValue(Object runtimeObj) {
			if (isConstant) {
				LOG.debug("Returning constant value '" + value + "'...");
				return value;
			}

			try {
				Object rawValue = field.get(runtimeObj);
				if (isRecord) {
					return rawValue;
				}

				LOG.debug("Formatting field '" + field.getName() + "'...");
				return formatValue(field.get(runtimeObj));

			} catch (Exception ex) {
				throw new IllegalArgumentException("Exception getting field '" + field.getName() + "' from '" + runtimeObj + "'", ex);
			}
		}

		private Object formatValue(Object runtimeValue) {
			try {

				if (runtimeValue == null) {
					LOG.debug("Returning <null>...");
					return null;
				}

				if ("".equals(annotation.format())) {
					LOG.debug("Returning unformatted value '" + runtimeValue + "'");
					return runtimeValue;
				}

				if (runtimeValue instanceof String) {
					return formatString((String) runtimeValue, annotation.format());
				}

				if (runtimeValue instanceof Integer || Integer.TYPE.isInstance(runtimeValue)) {
					String ret = String.format(annotation.format(), runtimeValue);
					LOG.debug("Formated integer value '" + runtimeValue + "' with '" + annotation.format() + "'='" + ret + "'");
					return ret;
				}

				if (runtimeValue instanceof Date) {
					String ret = dateFormat.format(runtimeValue);
					LOG.debug("Formated value '" + runtimeValue + "' with '" + annotation.format() + "'='" + ret + "'");
					return ret;
				}

				return String.valueOf(runtimeValue);

			} catch (Exception ex) {
				throw new IllegalArgumentException("Exception formatting field '" + field.getName() + "' with format '"
						+ annotation.format() + "'", ex);
			}
		}

		public void setValue(Object currentObject, String value) {
			try {
				if (Date.class.isAssignableFrom(field.getType())) {
					field.set(currentObject, dateFormat.parse(value));
				} else if (Integer.class.isAssignableFrom(field.getType()) || Integer.TYPE == field.getType()) {
					field.set(currentObject, Integer.parseInt(value));
				} else if (String.class.isAssignableFrom(field.getType())) {
					field.set(currentObject, value);
				} else {
					throw new UnsupportedOperationException("Unsupported type '" + field.getType() + "'");
				}
			} catch (Exception ex) {
				throw new RuntimeException("Exception populating field '" + field.getName() + "' with value '" + value + "'", ex);
			}
		}

		public int getFormatSize() {
			if (isConstant && "".equals(annotation.format()) && value != null && value instanceof String) {
				return value.toString().length();
			}

			if (Date.class.isAssignableFrom(field.getType())) {
				return annotation.format().length();
			}

			Matcher m = formatPattern.matcher(annotation.format());
			if (!m.find()) {
				throw new IllegalArgumentException("Couldn't guess field size for '" + field.getName() + "' with format '"
						+ annotation.format() + "'");
			}
			return Integer.parseInt(m.group(3));
		}

		/**
		 * This is an extension to the {@link String#format(String, Object...)} method to support padding with any character (not only
		 * space).
		 * 
		 * @param runtimeValue
		 * @param format
		 * @return
		 */
		public static String formatString(String runtimeValue, String format) {
			String ret = runtimeValue;
			Matcher m = formatPattern.matcher(format);
			if (m.find() && m.group(2) != null) {
				char padChar = m.group(2).charAt(1);
				StringBuilder newFormat = new StringBuilder(format);
				newFormat.delete(m.start(2), m.end(2));
				LOG.debug("Formatting '" + runtimeValue + "' with new format '" + newFormat + "' with pad char '" + padChar + "'...");
				String newValue = String.format(newFormat.toString(), runtimeValue);
				StringBuilder tempRet = new StringBuilder(newValue);
				LOG.debug("Replacing pad char for '" + tempRet + "'");
				int idx = tempRet.indexOf(runtimeValue);
				for (int i = 0; i < idx; i++) {
					tempRet.replace(i, i + 1, String.valueOf(padChar));
				}
				for (int i = idx + runtimeValue.length(); i < tempRet.length(); i++) {
					tempRet.replace(i, i + 1, String.valueOf(padChar));
				}
				ret = tempRet.toString();
			} else {
				LOG.debug("Formatting '" + runtimeValue + "' with format '" + format + "'...");
				ret = String.format(format, runtimeValue);
			}

			LOG.debug("Formated string value '" + runtimeValue + "' with '" + format + "'='" + ret + "'");
			return ret;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((enclosingClass == null) ? 0 : enclosingClass.hashCode());
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SerializableField other = (SerializableField) obj;
			if (enclosingClass == null) {
				if (other.enclosingClass != null)
					return false;
			} else if (!enclosingClass.equals(other.enclosingClass))
				return false;
			if (field == null) {
				if (other.field != null)
					return false;
			} else if (!field.equals(other.field))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "SerializableField [class=" + enclosingClass + ", id=" + (classAnnotation != null ? classAnnotation.id() : "<null>")
					+ ",field=" + field.getName() + ", childs=" + childs + "]";
		}

	}

	public static String toHex(String arg) {
		return String.format("%040x", new BigInteger(1, arg.getBytes(/* YOUR_CHARSET? */)));
	}

}
