package org.andresoviedo.util.serialization.api1;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class basica abstracta para objetos XMLSerializables.
 */
public abstract class XMLElement<T extends XMLSerializable<?>> implements XMLSerializable<T> {

	protected XMLSerializable<?> _parentNode = null;

	protected Map<String, String> _attributes = new Hashtable<String, String>();

	protected List<T> _childs = new Vector<T>();

	public XMLElement() {
	}

	@Override
	public Map<String, String> getAttributes() {
		return _attributes;
	}

	@Override
	public void setParentNode(XMLSerializable<?> parentNode) {
		_parentNode = parentNode;
	}

	protected XMLSerializable<?> getParentNode() {
		return _parentNode;
	}

	@Override
	public void addChildNode(T child) {
		synchronized (_childs) {
			_childs.add(child);
		}
	}

	/*
	 * Permite agregar un atributo a este elemento.
	 */
	protected void addAttribute(String name, String value) {
		synchronized (_attributes) {
			_attributes.put(name, value);
		}
	}

	/*
	 * Permite obtener un atributo de este elemento.
	 */
	protected String getAttribute(String name) {
		synchronized (_attributes) {
			return _attributes.get(name);
		}
	}

	public List<T> getChilds() {
		return _childs;
	}

	/**
	 * Sobreescribe el metodo Objec.clone() para que se puedan clonar objectos XMLSerializables.
	 * 
	 * @return el objecto XML clonado.
	 */
	@Override
	public Object clone() {
		Object ret = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element e = XMLSerializer.toXMLSerialized(document, this);
			document.appendChild(e);
			ret = XMLSerializer.fromXMLSerialized(document);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
}
