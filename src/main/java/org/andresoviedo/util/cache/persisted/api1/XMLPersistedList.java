package org.andresoviedo.util.cache.persisted.api1;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.andresoviedo.util.serialization.api1.XMLSerializable;
import org.andresoviedo.util.serialization.api1.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLPersistedList<T extends XMLSerializable<?>> extends PersistedList<T> {

	DocumentBuilderFactory docBuilderFactory;
	DocumentBuilder docBuilder;
	Transformer xformer;

	public XMLPersistedList(File targetFile) {
		super(targetFile);
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			xformer = TransformerFactory.newInstance().newTransformer();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T readObject(File targetFile) throws IOException {
		try {
			Document doc = docBuilder.parse(targetFile);
			return (T) XMLSerializer.fromXMLSerialized(doc);
		} catch (Exception ex) {
			throw new IOException("Exception while trying to read object: " + ex.getMessage());
		}
	}

	@Override
	public void writeObject(File targetFile, T targetObject) throws IOException {
		try {
			Document doc = docBuilder.newDocument();

			Element el = XMLSerializer.toXMLSerialized(doc, targetObject);
			doc.appendChild(el);

			// Prepare the DOM document for writing
			Source source = new DOMSource(doc);

			// Prepare the output file
			Result result = new StreamResult(targetFile);

			// Write the DOM document to the file
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			xformer.transform(source, result);
		} catch (Exception ex) {
			throw new IOException("Exception while trying to write object: " + ex.getMessage());
		}
	}
}
