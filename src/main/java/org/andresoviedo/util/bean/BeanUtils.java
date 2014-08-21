package org.andresoviedo.util.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtils {

	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

	public static String includedClass = "org.andresoviedo";
	/**
	 * Conjunto de clases wrapper de tipos primitivos de JAVA y el BigDecimal (ver clase AbsisCustomNumberEditor)
	 */
	@SuppressWarnings("unchecked")
	private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<Class<?>>(Arrays.asList(Boolean.class, Character.class, Number.class,
			Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class, BigDecimal.class));

	public static String reflectionToString(Object obj) {
		return reflectionToString(obj, true);
	}

	public static String reflectionToString(Object obj, boolean multiline) {
		if (obj == null)
			return "null";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(obj.getClass().getName()).append(" [");
			StringBuilder sb2 = new StringBuilder();
			reflectionToString_impl(sb, obj, getFields(obj, sb2), 0, multiline, sb2);
			sb.append("]");
			return sb.toString();
		}
	}

	public static boolean reflectionEquals(Object obj1, Object obj2) {
		String sb1 = reflectionToString(obj1);
		String sb2 = reflectionToString(obj2);
		return sb1.equals(sb2);
	}

	// Devuelve los campos de una clase de LaCaixa. Si esta clase hereda de una
	// que clase no básica de Jaba, obtiene
	// un String con la impresión de dicha clase
	private static List<Field> getFields(Object obj, StringBuilder classString) {
		List<Field> ret = new ArrayList<Field>();
		Class<?> currentClass = obj.getClass();
		String ObjectClassName = new Object().getClass().getName();
		do {
			// Si la clase deriva de una clase que no es de LaCaixa ni de
			// Object, entonces imprimimos la clase
			// con el método tradicional toString
			// JAC: Fix para WS de tipo castor
			// Si es un tipo enumerado generado por castor (implementa
			// EnumeratedTypeAccess) a partir de un xsd tampoco hay que hacer
			// reflection.

			if (!currentClass.getName().startsWith(includedClass) && !currentClass.getName().equals(ObjectClassName)) {
				classString.append(obj.toString());
				break;
			}
			ret.addAll(0, Arrays.asList(currentClass.getDeclaredFields()));
		} while ((currentClass = currentClass.getSuperclass()) != null);

		return ret;
	}

	private static void reflectionToString_impl(StringBuilder sb, Object obj, List<Field> fields, int level, boolean multiline,
			StringBuilder classString) {
		reflectionToString_impl(sb, new ArrayList<Object>(), null, obj, level, multiline, classString);
	}

	private static void reflectionToString_impl(StringBuilder sb, List<Object> set, String fieldName, Object obj, int level,
			boolean multiline, StringBuilder classString) {
		try {
			if (multiline) {
				sb.append("\n");
				sb.append(StringUtils.repeat("\t", level));
			}
			// Limitamos la recursividad a 21 objetos anidados ('level' aumenta
			// en un factor de 2). Así controlamos que
			// no hayamos entrado en un bucle infinito
			if (level > 40) {
				sb.append("... (continues)");
				return;
			}
			// if (set.contains(obj)) {
			// sb.append("-->").append(obj.toString());
			// return;
			// }
			// set.add(obj);

			if (obj == null) {
				sb.append(fieldName).append("=<null>");
				if (!multiline) {
					sb.append(",");
				}
				return;
			}

			Class<?> objClass = obj.getClass();

			if (objClass.isPrimitive() || (isPrimitiveWrapperClass(objClass) && !(objClass.isArray())) || String.class == objClass
					|| Date.class.isAssignableFrom(objClass) || objClass.isEnum()) {
				sb.append(fieldName).append("=").append(obj);
				if (!multiline) {
					sb.append(",");
				}
				return;
			}

			if (fieldName != null) {
				sb.append(fieldName);
				if (!multiline) {
					sb.append(" [");
				}
			}

			// Si el tipus de la classe es de laCaixa hem d'obtenir els seus
			// camps primer, abans de printar els elements
			// que pugui heredar (collections, arrays, hashmaps, ...)
			if (Iterable.class.isAssignableFrom(objClass)) {
				int i = 0;
				for (Object listElement : ((Iterable<?>) obj)) {
					reflectionToString_impl(sb, set, fieldName + "[" + (i++) + "]", listElement, level + 2, multiline, null);
				}
			} else if (objClass.isArray()) {
				for (int i = 0; i < Array.getLength(obj); i++) {
					Object arrayElementValue = Array.get(obj, i);
					reflectionToString_impl(sb, set, fieldName + "[" + i + "]", arrayElementValue, level + 2, multiline, null);
				}
			} else if (objClass.getName().startsWith("es.lacaixa.")) {
				StringBuilder sb3 = new StringBuilder();
				for (Field objField : getFields(obj, sb3)) {
					if (Modifier.isFinal(objField.getModifiers())) {
						continue;
					}
					objField.setAccessible(true);
					if (sb3.length() > 0) {
						if (multiline) {
							sb.append("\n").append(StringUtils.repeat("\t", level + 2));
							sb.append("<parent>=[").append(sb3).append("]");
						} else {
							sb.append("<parent>=[").append(sb3).append("], ");
						}
					}
					reflectionToString_impl(sb, set, objField.getName(), objField.get(obj), level + 2, multiline, null);
				}
			} else if (Map.class.isAssignableFrom(objClass)) {
				for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
					reflectionToString_impl(sb, set, fieldName + "[" + entry.getKey() + "]", entry.getValue(), level + 2, multiline, null);
				}
			} else {
				sb.append("(").append(objClass.getName()).append(")=").append(obj);
				if (!multiline) {
					sb.append(",");
				}
			}

			if (fieldName != null && !multiline) {
				sb.append("]");
				sb.append(",");
			}

		} catch (Exception ex) {
			logger.error("Exception thrown while processing Class " + (obj != null ? obj.getClass() : null), ex);
		}
	}

	/**
	 * Determina si una clase es un wrapper de un tipo primitivo. <b>NOTA:</b>En commons-lang-2.4 hay un método que poldría servir para
	 * realizar este check. A día de hoy tenemos la versión 2.1.
	 * 
	 * @param clazz
	 *            la clase
	 * @return <code>true</code> si la clase es un wrapper de un tipo primitivo, <code>false</code> de otra manera.
	 */
	public static boolean isPrimitiveWrapperClass(Class<?> clazz) {
		return WRAPPER_TYPES.contains(clazz);
	}

	public static Object getProperty(Object bean, String propertyName) {
		if (bean == null || StringUtils.isBlank(propertyName)) {
			throw new IllegalArgumentException("Bean  or property can't be null");
		}
		// Process possible nested beans
		if (propertyName.contains(".")) {

			Object currentBean = bean;
			String[] propertyPath = propertyName.split("\\.");
			for (int k = 0; k < propertyPath.length - 1; k++) {

				String propertyPart = propertyPath[k];
				PropertyDescriptor pd = getPropertyDescriptorSimple(currentBean.getClass(), propertyPart);
				if (pd == null) {
					throw new IllegalArgumentException("Property path '" + propertyName + "' cant be binded for bean '" + bean + "' ("
							+ bean.getClass() + "). Property '" + propertyPart + "' doesn't exist for bean " + currentBean + " ("
							+ currentBean.getClass() + ")");
				}

				try {
					Object nestedBean = pd.getReadMethod().invoke(currentBean);
					if (nestedBean == null) {
						if (logger.isTraceEnabled()) {
							logger.trace("Property path '" + propertyName + "' couldn't be retrieved for bean '" + bean + "' ("
									+ bean.getClass() + "). Property '" + propertyPart + "' is null for bean " + currentBean + " ("
									+ currentBean.getClass() + ")");
						}
						return null;
					}
					currentBean = nestedBean;
				} catch (Exception ex) {
					throw new RuntimeException("Exception binding property '" + propertyName + "' for bean '" + bean + "' ("
							+ bean.getClass() + ")", ex);
				}
			}

			bean = currentBean;
			propertyName = propertyPath[propertyPath.length - 1];
		}

		PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
		if (pd == null) {
			throw new IllegalArgumentException("Property '" + propertyName + "' not found for bean '" + bean + "'");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Getting property '" + propertyName + "' for bean with class '" + bean.getClass());
		}
		try {
			return pd.getReadMethod().invoke(bean);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Exception getting property '" + pd.getName() + "' for bean '" + bean + " (" + bean.getClass() + ")", ex);
		}

	}

	/**
	 * Establece un nuevo valor para la propiedad del bean indicado.
	 * 
	 * @param bean
	 *            instancia del bean
	 * @param propertyName
	 *            nombre de la property
	 * @param value
	 *            nuevo valor de la property
	 * @return el bean sobre el cual se ha aplicado la property, o si el <code>value</code> si este era un valor no primitivo
	 */
	public static void setProperty(Object bean, String propertyName, Object value) {
		if (bean == null || StringUtils.isBlank(propertyName)) {
			throw new IllegalArgumentException("Bean or property can't be null");
		}

		// Process possible nested beans
		if (propertyName.contains(".")) {

			Object currentBean = bean;
			String[] propertyPath = propertyName.split("\\.");
			for (int k = 0; k < propertyPath.length - 1; k++) {

				String propertyPart = propertyPath[k];
				PropertyDescriptor pd = getPropertyDescriptorSimple(currentBean.getClass(), propertyPart);
				if (pd == null) {
					logger.warn("Property path '" + propertyName + "' cant be binded for bean '" + bean + "' (" + bean.getClass()
							+ "). Property '" + propertyPart + "' doesn't exist for bean " + currentBean + " (" + currentBean.getClass()
							+ ")");
					return;
				}

				try {
					Object nestedBean = pd.getReadMethod().invoke(currentBean);
					Class<?> pt = pd.getPropertyType();
					if (nestedBean == null && !ClassUtils.isPrimitiveOrWrapper(pt)) {
						nestedBean = pt.newInstance();
						pd.getWriteMethod().invoke(currentBean, nestedBean);
					}
					currentBean = nestedBean;
				} catch (Exception ex) {
					String msg = "Exception binding property '" + propertyName + "' for bean '" + bean + "' (" + bean.getClass() + ")";
					throw new RuntimeException(msg, ex);
				}
			}

			bean = currentBean;
			propertyName = propertyPath[propertyPath.length - 1];
		}

		PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
		if (pd == null) {
			throw new IllegalArgumentException("Property '" + propertyName + "' not found for bean '" + bean + "'");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Setting property '" + propertyName + "' for bean with class '" + bean.getClass()
					+ "'. Property descriptor has return type '" + pd.getPropertyType() + "'. Value is '" + value + "' "
					+ (value != null ? "(" + value.getClass() + ")" : ""));
		}
		try {
			Class<?> pt = pd.getPropertyType();
			pd.getWriteMethod().invoke(bean, convert(value, pt));
		} catch (Exception ex) {
			throw new RuntimeException("Exception setting value '" + value + "' for property '" + pd.getName() + "' at bean '" + bean
					+ " (" + bean.getClass() + ")", ex);
		}
	}

	/**
	 * Obtiene el PropertyDescriptor de la propiedad de la clase indicada.
	 * 
	 * @param clazz
	 *            Clase de la cual obtener el descriptor
	 * @param propertyName
	 *            nombre de la propiedad
	 * @return el descriptor de la propiedad o <code>null</code> si la clase no contiene esa propiedad
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {

		// Process possible nested beans
		if (!propertyName.contains(".")) {
			return getPropertyDescriptorSimple(clazz, propertyName);
		}

		Class<?> currentClass = clazz;
		String[] propertyPath = propertyName.split("\\.");
		for (int k = 0; k < propertyPath.length - 1; k++) {
			PropertyDescriptor pd = getPropertyDescriptorSimple(currentClass, propertyPath[k]);
			if (pd == null) {
				return null;
			}
			currentClass = pd.getReadMethod().getReturnType();
		}
		return getPropertyDescriptorSimple(currentClass, propertyPath[propertyPath.length - 1]);
	}

	private static PropertyDescriptor getPropertyDescriptorSimple(Class<?> clazz, String propertyName) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < pds.length; i++) {
				if (pds[i].getName().equals(propertyName) && pds[i].getWriteMethod() != null) {
					// Method writeMethod = pds[i].getWriteMethod();
					// if (writeMethod != null && writeMethod.getName().equals(propertyName))
					return pds[i];
				}
			}
		} catch (IntrospectionException e) {
			logger.error("Exception getting property descriptor '" + propertyName + "' for class '" + clazz + "'");
		}
		return null;
	}

	/**
	 * Convierte el valor al tipo requerido
	 * 
	 * @param value
	 *            valor a convertir
	 * @param requiredType
	 *            Clase a la que convertir el valor
	 * @return valor convertido a la clase requerida
	 */
	public static Object convert(Object value, Class<?> requiredType) {
		Object convertImpl = convertImpl(value, requiredType);
		if (logger.isTraceEnabled()) {
			logger.trace("Value '" + value + "'" + (value != null ? " (" + value.getClass() + ")" : "") + " converted to '" + convertImpl
					+ "'" + (convertImpl != null ? " (" + convertImpl.getClass() + ")" : ""));
		}
		return convertImpl;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object convertImpl(Object value, Class<?> requiredType) {
		if (value == null) {
			return null;
		}
		if (requiredType.isAssignableFrom(value.getClass())) {
			return value;
		}

		if (requiredType == String.class) {
			return String.valueOf(value);
		}

		if (requiredType == Integer.class || requiredType == Integer.TYPE) {
			return Integer.valueOf(String.valueOf(value));
		}

		if (requiredType == Long.class || requiredType == Long.TYPE) {
			return Long.valueOf(String.valueOf(value));
		}

		if (requiredType == Float.class || requiredType == Float.TYPE) {
			return Float.valueOf(String.valueOf(value));
		}

		if (requiredType == Double.class || requiredType == Double.TYPE) {
			return Double.valueOf(String.valueOf(value));
		}

		if (requiredType == Boolean.class || requiredType == Boolean.TYPE) {
			if (value instanceof String) {
				return Boolean.valueOf(String.valueOf(value));
			} else if (value instanceof Integer || requiredType == Integer.TYPE) {
				return (Integer) value == 1;
			} else {
				logger.warn("Value must be of type String or Integer, not '" + value.getClass() + "'");
			}
		}

		if (requiredType.isEnum()) {
			return Enum.valueOf((Class<Enum>) requiredType, StringUtils.trim(String.valueOf(value)));
		}

		return null;
	}

	/**
	 * Convierte un POJO en un Map<String,Object> de properties
	 * 
	 * @param bean
	 *            pojo con metodos de acceso "getX" o "isX"
	 * @return un Map<String,Object> con las properties del POJO
	 * @throws Exception
	 */
	public static Map<String, Object> mapProperties(Object bean) {
		try {
			Map<String, Object> properties = new HashMap<String, Object>();
			for (Method method : bean.getClass().getDeclaredMethods()) {
				if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0
						&& method.getReturnType() != void.class && method.getName().matches("^(get|is).+")) {
					String name = method.getName().replaceAll("^(get|is)", "");
					if (name.length() == 1 || !StringUtils.isAllUpperCase(name)) {
						name = Character.toLowerCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "");
					}
					Object value = method.invoke(bean);
					properties.put(name, value);
				}
			}
			return properties;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void populate(Object bean, Map<String, ? extends Object> properties) {
		if ((bean == null) || (properties == null)) {
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("BeanUtils.populate(" + bean + ", " + properties + ")");
		}

		for (Map.Entry<String, ? extends Object> entry : properties.entrySet()) {
			String name = (String) entry.getKey();
			if (name != null) {
				setProperty(bean, name, entry.getValue());
			}
		}
	}
}
