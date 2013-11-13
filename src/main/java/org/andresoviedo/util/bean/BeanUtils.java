package org.andresoviedo.util.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanUtils {

	protected static final Log logger = LogFactory.getLog(BeanUtils.class);

	public static String includedClass = "org.andresoviedo";
	/**
	 * Conjunto de clases wrapper de tipos primitivos de JAVA y el BigDecimal
	 * (ver clase AbsisCustomNumberEditor)
	 */
	@SuppressWarnings("unchecked")
	private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<Class<?>>(
			Arrays.asList(Boolean.class, Character.class, Number.class,
					Byte.class, Short.class, Integer.class, Long.class,
					Float.class, Double.class, Void.class, BigDecimal.class));

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
			reflectionToString_impl(sb, obj, getFields(obj, sb2), 0, multiline,
					sb2);
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

			if (!currentClass.getName().startsWith(includedClass)
					&& !currentClass.getName().equals(ObjectClassName)) {
				classString.append(obj.toString());
				break;
			}
			ret.addAll(0, Arrays.asList(currentClass.getDeclaredFields()));
		} while ((currentClass = currentClass.getSuperclass()) != null);

		return ret;
	}

	private static void reflectionToString_impl(StringBuilder sb, Object obj,
			List<Field> fields, int level, boolean multiline,
			StringBuilder classString) {
		reflectionToString_impl(sb, new ArrayList<Object>(), null, obj, level,
				multiline, classString);
	}

	private static void reflectionToString_impl(StringBuilder sb,
			List<Object> set, String fieldName, Object obj, int level,
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

			if (objClass.isPrimitive()
					|| (isPrimitiveWrapperClass(objClass) && !(objClass
							.isArray())) || String.class == objClass
					|| Date.class.isAssignableFrom(objClass)
					|| objClass.isEnum()) {
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
					reflectionToString_impl(sb, set, fieldName + "[" + (i++)
							+ "]", listElement, level + 2, multiline, null);
				}
			} else if (objClass.isArray()) {
				for (int i = 0; i < Array.getLength(obj); i++) {
					Object arrayElementValue = Array.get(obj, i);
					reflectionToString_impl(sb, set, fieldName + "[" + i + "]",
							arrayElementValue, level + 2, multiline, null);
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
							sb.append("\n").append(
									StringUtils.repeat("\t", level + 2));
							sb.append("<parent>=[").append(sb3).append("]");
						} else {
							sb.append("<parent>=[").append(sb3).append("], ");
						}
					}
					reflectionToString_impl(sb, set, objField.getName(),
							objField.get(obj), level + 2, multiline, null);
				}
			} else if (Map.class.isAssignableFrom(objClass)) {
				for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
					reflectionToString_impl(sb, set,
							fieldName + "[" + entry.getKey() + "]",
							entry.getValue(), level + 2, multiline, null);
				}
			} else {
				sb.append("(").append(objClass.getName()).append(")=")
						.append(obj);
				if (!multiline) {
					sb.append(",");
				}
			}

			if (fieldName != null && !multiline) {
				sb.append("]");
				sb.append(",");
			}

		} catch (Exception ex) {
			logger.error("Exception thrown while processing Class "
					+ (obj != null ? obj.getClass() : null), ex);
		}
	}

	/**
	 * Determina si una clase es un wrapper de un tipo primitivo. <b>NOTA:</b>En
	 * commons-lang-2.4 hay un método que poldría servir para realizar este
	 * check. A día de hoy tenemos la versión 2.1.
	 * 
	 * @param clazz
	 *            la clase
	 * @return <code>true</code> si la clase es un wrapper de un tipo primitivo,
	 *         <code>false</code> de otra manera.
	 */
	public static boolean isPrimitiveWrapperClass(Class<?> clazz) {
		return WRAPPER_TYPES.contains(clazz);
	}
}
