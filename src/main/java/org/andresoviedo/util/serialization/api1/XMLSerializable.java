package org.andresoviedo.util.serialization.api1;

import java.util.List;
import java.util.Map;

/**
 * Interface that allows to serialize / deserialize XMLSerializable objects.
 * 
 * XMLSerializable objects should have: - defaultConstructor - setter methods like: setAttributeXXX(String value) for all attributes defined
 * for the element.
 */

public interface XMLSerializable<T extends XMLSerializable<?>> {
	/**
	 * Should return the absolute name of the class for this object. Ej:
	 * 
	 * public String getClassName(){ return MyClass.class.getName(); }
	 * 
	 * @return the absolute class namespace.
	 */
	// public String getClassName();
	/**
	 * Should return all the XML element attributes for this element. Ej: <MyClass attr1_name="value_attribute_1"
	 * attr2_name="attribute_2_value" />
	 * 
	 * public Hashtable getAttributes(){ Hashtable h = new Hashtable(); h.put("attr1_name","attribute_1_value");
	 * h.put("attr2_name","attribute_2_value"); return h; }
	 * 
	 * @return the key-value pairs corresponding to the XML element attributes.
	 */
	public Map<String, String> getAttributes();

	/**
	 * This method is called when an XML subordinated (or child) element is found for this XMLSerializable element.
	 * 
	 * @param the
	 *            subordinate element.
	 */
	public void addChildNode(T child);

	/**
	 * @return a list of childs
	 */
	public List<? extends T> getChilds();

	/**
	 * Let the XMLElement know wich is it's XML parent element.
	 * 
	 * @param parent
	 *            XMLSerializable
	 */
	public void setParentNode(XMLSerializable<?> parent);

}
