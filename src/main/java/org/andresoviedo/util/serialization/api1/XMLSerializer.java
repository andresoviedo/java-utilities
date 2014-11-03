package org.andresoviedo.util.serialization.api1;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.andresoviedo.util.string.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * XMLSerializer factory wich creates XMLSerializer objects with configurable options.
 * 
 * Default options are: - Mapping between java-objects and XML elements: no mapping; it means java objects will be mapped to XML elements
 * with the tag name equals to object class name. - Serialize unknown elements: false; it means, ununderstood XML elements wont be
 * serialized. If true, it will be serialized through an UnmappeableXMLElement java object.
 * 
 * Also, XMLSerializer has 2 static methods which corresponds to calling a XMLSerializer object with default options.
 * 
 * The mapping between java object classes and XML element tag names can be set by this 2 methods:
 * 
 * #addElementClass(String elementName, Class theClass) #removeElementClass(String elementName)
 * 
 * Wich associates an element name to a java Class.
 */

public class XMLSerializer<T extends XMLSerializable<?>> {

	private Map<String, Class<? extends XMLSerializable<?>>> _classForElement = new Hashtable<String, Class<? extends XMLSerializable<?>>>();

	private Map<Class<? extends XMLSerializable<?>>, String> _elementForClass = new Hashtable<Class<? extends XMLSerializable<?>>, String>();

	private boolean _serializeUnknownElements = false;

	private static Logger logger = Logger.getLogger("org.andresoviedo.util");

	private static XMLSerializer<XMLSerializable<?>> _defaultXMLSerializer = new XMLSerializer<XMLSerializable<?>>();
	private static DocumentBuilder builder;
	private static Transformer xformer;
	static {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xformer = TransformerFactory.newInstance().newTransformer();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (TransformerConfigurationException ex) {
			ex.printStackTrace();
		} catch (TransformerFactoryConfigurationError ex) {
			ex.printStackTrace();
		}
	}

	public XMLSerializer() {

	}

	public static XMLSerializer<?> getDefaultXMLSerializer() {
		return _defaultXMLSerializer;
	}

	// -------------------- XML CONFIGURATION --------------------------------- //

	public void serializeUnknownElements(boolean serialize) {
		_serializeUnknownElements = serialize;
	}

	public void addElementClass(String elementName, Class<? extends XMLSerializable<?>> theClass) {
		_classForElement.put(elementName, theClass);
		_elementForClass.put(theClass, elementName);
	}

	public void removeElementClass(String elementName) {
		Class<? extends XMLSerializable<?>> theClass = _classForElement.remove(elementName);
		if (theClass != null) {
			_elementForClass.remove(theClass);
		}
	}

	// ---------------------- IMPLEMENTATION ---------------------------------- //

	public Element toXML(Document document, T node) {
		return toXML_impl(document, node);
	}

	private Element toXML_impl(Document document, XMLSerializable<?> node) {
		// Buscar el nombre del elemento para esa clase. Si no existe,
		// el nombre del elemento sera el nombre de la clase
		String elementName = _elementForClass.get(node.getClass());
		if (elementName == null) {
			if (node instanceof UnmappeableXMLElement) {
				elementName = ((UnmappeableXMLElement) node).getClassName();
			} else {
				elementName = node.getClass().getCanonicalName();
			}
		}

		// crear un elemento identificandolo con el nombre de clase
		Element ret = document.createElement(elementName);

		// añadir los atributos
		Map<String, String> attributes = node.getAttributes();
		if (attributes != null) {
			for (Iterator<String> e = attributes.keySet().iterator(); e.hasNext();) {
				String key = e.next();
				String value = attributes.get(key);
				ret.setAttribute(key, StringUtils.removeUnicodeControlChars(value));
			}
		}

		// Añadir los hijos
		List<? extends XMLSerializable<?>> childs = node.getChilds();
		if (childs != null) {
			for (int i = 0; i < childs.size(); i++) {
				Object childNode = childs.get(i);
				if (!(childNode instanceof Node)) {
					ret.appendChild(toXML_impl(document, childs.get(i)));
				} else {
					ret.appendChild((Node) childNode);
				}
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public T fromXML(Document document) {
		return (T) fromXML_impl(null, document.getFirstChild());
	}

	@SuppressWarnings("unchecked")
	private XMLSerializable<?> fromXML_impl(XMLSerializable<?> parentNode, Node node) {
		XMLSerializable<XMLSerializable<?>> ret = null;
		String parameterClassName = null;
		try {
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				// obtener argumentos por defecto
				parameterClassName = node.getNodeName();
				Class<? extends XMLSerializable<?>> parameterClass = _classForElement.get(parameterClassName);

				// Si la clase del elemento no ha sido sobreescrita, buscar clase con
				// el mismo nombre del elemento
				if (parameterClass == null) {
					parameterClass = (Class<? extends XMLSerializable<?>>) Class.forName(parameterClassName);
				}

				// obtener constructor por defecto
				Constructor<? extends XMLSerializable<?>> parameterConstructor = parameterClass.getConstructor(new Class[] {});

				// crear una nueva instancia (sin argumentos)
				ret = (XMLSerializable<XMLSerializable<?>>) parameterConstructor.newInstance(new Object[] {});

				// inicializar objeto instanciado
				ret.setParentNode(parentNode);

				// invocar metodos si se ha especificado
				NamedNodeMap parameterAttributes = node.getAttributes();
				for (int i = 0; i < parameterAttributes.getLength(); i++) {
					Node n = parameterAttributes.item(i);
					String key = n.getNodeName();
					String value = n.getNodeValue();
					try {
						// poner en mayúscula la primera letra de la variable del metodo
						String setterName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
						Method method = parameterClass.getMethod(setterName, new Class[] { String.class });
						method.invoke(ret, new Object[] { value });
					} catch (NoSuchMethodException ex1) {
						/*
						 * warning("fromXMLSerialized", "NoSuchMethodException: " + ex1.getMessage());
						 */
					} catch (Exception ex2) {
						warning("fromXML_impl", "Exception: " + ex2.getMessage());
					}
				}

				// Agregar hijos
				NodeList childNodes = node.getChildNodes();
				if (childNodes != null) {
					for (int i = 0; i < childNodes.getLength(); i++) {
						XMLSerializable<?> parameter = fromXML_impl(ret, childNodes.item(i));
						if (parameter != null) {
							ret.addChildNode(parameter);
						}
					}
				}
			}
		} catch (ClassNotFoundException cnfex) {
			if (_serializeUnknownElements) {
				// Crear objeto
				UnmappeableXMLElement uel = new UnmappeableXMLElement(parameterClassName);
				ret = uel;
				ret.setParentNode(parentNode);

				// Agregar attributos
				NamedNodeMap parameterAttributes = node.getAttributes();
				for (int i = 0; i < parameterAttributes.getLength(); i++) {
					Node n = parameterAttributes.item(i);
					String key = n.getNodeName();
					String value = n.getNodeValue();
					uel.addAttribute(key, value);
				}
				// Agregar hijos
				NodeList childNodes = node.getChildNodes();
				if (childNodes != null) {
					for (int i = 0; i < childNodes.getLength(); i++) {
						XMLSerializable<?> parameter = fromXML_impl(ret, childNodes.item(i));
						if (parameter != null) {
							ret.addChildNode(parameter);
						}
					}
				}
			} else {
				warning("fromXML_impl", "ClassNotFoundException: " + cnfex.getMessage());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logException(ex);
		}
		return ret;
	}

	// ------------------ SERIALIZING WITH DEFAULT SERIALIZER ----------------- //

	public static XMLSerializable<?> fromXMLSerialized(Document document) {
		return _defaultXMLSerializer.fromXML(document);
	}

	public static Element toXMLSerialized(Document document, XMLSerializable<?> node) {
		return _defaultXMLSerializer.toXML(document, node);
	}

	// ------------------------------------------------------------------------ //

	public String toXMLString(T node) {
		Writer writer = new StringWriter();
		try {
			Document document;
			synchronized (builder) {
				document = builder.newDocument();
			}
			document.appendChild(toXML(document, node));
			Source domSource = new DOMSource(document);
			Result result = new StreamResult(writer);
			synchronized (xformer) {
				xformer.transform(domSource, result);
			}
		} catch (Exception ex) {
			logException(ex);
		}
		return writer.toString();
	}

	public static String toXMLSerializedString(XMLSerializable<?> node) {
		Writer writer = new StringWriter();
		try {
			Document document;
			synchronized (builder) {
				document = builder.newDocument();
			}
			document.appendChild(_defaultXMLSerializer.toXML(document, node));
			Source domSource = new DOMSource(document);
			Result result = new StreamResult(writer);
			synchronized (xformer) {
				xformer.transform(domSource, result);
			}
		} catch (Exception ex) {
			logException(ex);
		}
		return writer.toString();
	}

	public T fromXMLString(String xmlString) {
		T ret = null;
		Reader reader = new StringReader(xmlString);
		try {
			Document doc;
			synchronized (builder) {
				doc = builder.parse(new InputSource(reader));
			}
			ret = fromXML(doc);
		} catch (Exception ex) {
			logException(ex);
		}
		return ret;
	}

	public static XMLSerializable<?> fromXMLSerializedString(String xmlString) {
		XMLSerializable<?> ret = null;
		Reader reader = new StringReader(xmlString);
		try {
			Document doc;
			synchronized (builder) {
				doc = builder.parse(new InputSource(reader));
			}
			ret = _defaultXMLSerializer.fromXML(doc);
		} catch (Exception ex) {
			logException(ex);
		}
		return ret;
	}

	public static byte[] toXMLSerializedBytes(XMLSerializable<?> node, boolean compress) {
		try {
			Document doc;
			synchronized (builder) {
				doc = builder.newDocument();
			}
			return toXMLSerializedBytes(doc, node, compress);
		} catch (Exception ex) {
			logException(ex);
		}
		return null;
	}

	public static byte[] toXMLSerializedBytes(Document document, XMLSerializable<?> node, boolean compress) {
		byte[] ret = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			OutputStream out = bos;
			ZipOutputStream zipOut = null;

			if (compress) {
				zipOut = new ZipOutputStream(out);
				zipOut.setLevel(9); // max compression
				ZipEntry zipEntry = new ZipEntry("XMLSerializable<?>.xml");
				zipOut.putNextEntry(zipEntry);
				out = zipOut;
			}

			document.appendChild(_defaultXMLSerializer.toXML(document, node));

			Source domSource = new DOMSource(document);
			Result result = new StreamResult(out);

			synchronized (xformer) {
				xformer.transform(domSource, result);
			}

			if (compress) {
				zipOut.closeEntry();
			}

			out.flush();
			out.close();

			ret = bos.toByteArray();

		} catch (Exception ex) {
			logException(ex);
		}
		return ret;
	}

	public static XMLSerializable<?> fromXMLSerializedBytes(byte[] xmlBytes, boolean uncompress) {
		XMLSerializable<?> ret = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(xmlBytes);
			InputStream in = bis;

			if (uncompress) {
				ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(in));
				if (zipIn.getNextEntry() != null) {
					in = zipIn;
				}
			}

			Document doc;
			synchronized (builder) {
				doc = builder.parse(in);
			}
			ret = _defaultXMLSerializer.fromXML(doc);

			in.close();
		} catch (Exception ex) {
			logException(ex);
		}
		return ret;
	}

	// ------------------------------------------------------------------------ //

	private static void warning(String method, String message) {
		logger.logp(Level.WARNING, "XMLSerializer", method, message);
	}

	private static void logException(Throwable ex) {
		logger.logp(Level.SEVERE, "XMLSerializer", "logException", ex.getMessage(), ex);
	}
}
