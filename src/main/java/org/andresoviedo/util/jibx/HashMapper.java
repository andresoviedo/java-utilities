package org.andresoviedo.util.jibx;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLReader;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.Utility;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

/**
 * Custom handler de JIBX para serializar mapas. Las entradas del mapa se espera que sean de tipo String y los valores puedes ser
 * IMarshallable o Strings. Se soporta el atributo xsi:nil.
 * 
 * @author andresoviedo
 */
public class HashMapper implements IMarshaller, IUnmarshaller, IAliasable {

	/**
	 * Namespace
	 */
	private String m_uri;
	/**
	 * Index
	 */
	private int m_index;
	/**
	 * Tag name
	 */
	private String m_name;
	/**
	 * Lista de tipos java soportados y su respectivo serializador.
	 */
	private static final Map<Class<?>, Method> javaToXsdTypeSerializationMapping = new HashMap<Class<?>, Method>();
	/**
	 * Lista de nombres de clases a ignorar por este marshaller TODO: Esto resulta una ñapa, pero actualmente no hay otra manera de inyectar
	 * estas clases.
	 */
	private static final Set<String> ignoredClasses = new HashSet<String>();
	static {
		try {
			// Serializers (standard types)
			javaToXsdTypeSerializationMapping.put(Boolean.class, Utility.class.getMethod("serializeBoolean", boolean.class));
			javaToXsdTypeSerializationMapping.put(Double.class, Utility.class.getMethod("serializeDouble", double.class));
			javaToXsdTypeSerializationMapping.put(Float.class, Utility.class.getMethod("serializeFloat", float.class));
			javaToXsdTypeSerializationMapping.put(Integer.class, Utility.class.getMethod("serializeInt", int.class));
			javaToXsdTypeSerializationMapping.put(Long.class, Utility.class.getMethod("serializeLong", long.class));
			javaToXsdTypeSerializationMapping.put(Short.class, Utility.class.getMethod("serializeShort", short.class));
			javaToXsdTypeSerializationMapping.put(Date.class, Utility.class.getMethod("serializeDate", Date.class));
			javaToXsdTypeSerializationMapping.put(BigDecimal.class, Object.class.getMethod("toString"));
			javaToXsdTypeSerializationMapping.put(BigInteger.class, Object.class.getMethod("toString"));
			javaToXsdTypeSerializationMapping.put(String.class, Object.class.getMethod("toString"));
			// Serializers (extra types)
			javaToXsdTypeSerializationMapping.put(Locale.class, Locale.class.getMethod("toString"));

			ignoredClasses.add("org.andresoviedo.IgnoredClass");
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}

	}

	/**
	 * Logger
	 */
	private final static Log logger = LogFactory.getLog(HashMapper.class);

	public HashMapper() {
		this.m_uri = null;
		this.m_index = 0;
		this.m_name = "hashmap";
	}

	public HashMapper(String uri, int index, String name) {
		this.m_uri = uri;
		this.m_index = index;
		this.m_name = name;
	}

	protected String getSizeAttributeName() {
		return "size";
	}

	protected String getEntryElementName() {
		return "entry";
	}

	protected String getKeyAttributeName() {
		return "key";
	}

	public boolean isExtension(int index) {
		return false;
	}

	public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
		if (!(obj instanceof Map))
			throw new JiBXException("Invalid object type for marshaller");
		if (!(ictx instanceof MarshallingContext)) {
			throw new JiBXException("Invalid object type for marshaller");
		}

		MarshallingContext ctx = (MarshallingContext) ictx;
		ctx.startTag(m_index, m_name);

		for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
			Object value = entry.getValue();
			Class<?> valueClass = value != null ? value.getClass() : null;
			try {
				if (value == null) {
					ctx.startTag(m_index, String.valueOf(entry.getKey()));
					ctx.attribute(0, "xsi:nil", "true");
					ctx.endTag(m_index, String.valueOf(entry.getKey()));
				} else if (value instanceof IMarshallable && !ignoredClasses.contains(value.getClass().getName())) {
					ctx.startTag(m_index, String.valueOf(entry.getKey()));
					try {
						IBindingFactory bfact = BindingDirectory.getFactory(value.getClass());
						IMarshallingContext mctx = bfact.createMarshallingContext();
						mctx.setXmlWriter(ctx.getXmlWriter());
						((IMarshallable) value).marshal(mctx);
					} catch (Exception ex) {
						logger.error("Exception while marshaling map '" + m_name + "'. Affected entry has key '" + entry.getKey()
								+ "' and value '" + entry.getValue() + "'" + (valueClass != null ? " (" + valueClass.getName() + ")" : "")
								+ "", ex);
					} finally {
						ctx.endTag(m_index, String.valueOf(entry.getKey()));
					}
				} else if (javaToXsdTypeSerializationMapping.containsKey(valueClass)) {
					ctx.startTag(m_index, String.valueOf(entry.getKey()));
					if (value instanceof String || value instanceof BigDecimal || value instanceof Locale
							|| int.class.isAssignableFrom(valueClass)) {
						ctx.writeContent((String) ((Method) javaToXsdTypeSerializationMapping.get(valueClass)).invoke(value));
					} else {
						ctx.writeContent((String) ((Method) javaToXsdTypeSerializationMapping.get(valueClass)).invoke(value, value));
					}
					ctx.endTag(m_index, String.valueOf(entry.getKey()));
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Ignoring entry for map '" + m_name + "' with key '" + entry.getKey() + "' and value '"
								+ entry.getValue() + " (" + valueClass.getName() + ")");
					}
				}
			} catch (Exception ex) {
				logger.error("Exception while marshaling map '" + m_name + "'. Affected entry has key '" + entry.getKey() + "' and value '"
						+ entry.getValue() + "'" + (valueClass != null ? " (" + valueClass.getName() + ")" : "") + "", ex);
			}

		}

		ctx.endTag(m_index, m_name);
	}

	public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException {
		return ctx.isAt(this.m_uri, this.m_name);
	}

	public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
		UnmarshallingContext ctx = (UnmarshallingContext) ictx;
		if (!ctx.isAt(this.m_uri, this.m_name)) {
			ctx.throwStartTagNameError(this.m_uri, this.m_name);
		}

		Map<String, Object> map = (Map<String, Object>) obj;
		if (map == null) {
			if (m_name.equals("requestData")) {
				map = new HashMap<String, Object>();
			} else if (m_name.equals("sessionData")) {
				map = new HashMap<String, Object>();
			} else {
				throw new UnsupportedOperationException("Tipo de dato '" + m_name + "' no soportado.");
			}
		}

		// Avanzar al siguiente hijo
		while (ctx.next() == IXMLReader.START_TAG) {
			String key = ctx.getElementName();
			Object value = null;
			String xsType = null;
			try {
				boolean isNull = ctx.attributeBoolean("http://www.w3.org/2001/XMLSchema-instance", "nil", false);
				// xsType = ctx.attributeText("http://www.w3.org/2001/XMLSchema-instance", "type", null);
				if (isNull) {
					value = null; // redundante pero aclara que el valor es nulo
					ctx.next();
				} else {
					int next = ctx.next();
					if (next == IXMLReader.START_TAG) {
						value = ctx.unmarshalElement();
					} else {
						value = ctx.parseContentText();
					}
				}
				map.put(key, value);
			} catch (Exception ex) {
				logger.error("Exception while unmarshaling map '" + m_name + "'. Affected entry has key '" + key + "', value '" + value
						+ "' and type '" + xsType + "'", ex);
				throw new JiBXException("Exception while unmarshaling map '" + m_name + "'. Affected entry has key '" + key + "', value '"
						+ value + "' and type '" + xsType + "'", ex);
			}
		}
		ctx.next();

		return map;
	}

}