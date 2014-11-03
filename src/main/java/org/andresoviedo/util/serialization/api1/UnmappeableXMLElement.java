package org.andresoviedo.util.serialization.api1;

public class UnmappeableXMLElement extends XMLElement<XMLSerializable<?>> {

	public static final String ATTR_CLASSNAME = "className";

	private String _className;

	public UnmappeableXMLElement(String className) {
		_className = className;
	}

	public String getClassName() {
		return _className;
	}

	@Override
	public String toString() {
		return "UnmappeableXMLElement: className[" + _className + "]";
	}
}
