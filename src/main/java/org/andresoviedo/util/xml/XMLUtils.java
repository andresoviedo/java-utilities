package org.andresoviedo.util.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {

	private static Pattern p = Pattern.compile("\\&#.*?;");
	private static Matcher m = p.matcher("");

	public static void removeXMLEntities(File source, File target) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(source));
			BufferedWriter bw = new BufferedWriter(new FileWriter(target));

			String readed = null;
			while ((readed = br.readLine()) != null) {
				m.reset(readed);
				readed = m.replaceAll("");
				bw.write(readed);
			}
			bw.close();
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void removeXMLEntities(File source, File target, String encoding) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), encoding));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), encoding));

			String readed = null;
			while ((readed = br.readLine()) != null) {
				m.reset(readed);
				readed = m.replaceAll("");
				bw.write(readed);
			}
			bw.close();
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Returns the text value of the specified node. The returned value is the node value of the first child of <code>node</code> which type
	 * is <code>Document.TEXT_NODE</code>.
	 * 
	 * @param node
	 *            the node which text value has to be retrieved.
	 * @return the text value of the node.
	 */
	public static String getTextValue(Node node) {
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Document.TEXT_NODE) {
				return list.item(i).getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Returns the content of the first CDATA section node found under the specified node.
	 * 
	 * @param node
	 *            the node.
	 * @return the content of the first CDATA section node.
	 */
	public static String getCDataSectionContent(Node node) {
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Document.CDATA_SECTION_NODE) {
				return list.item(i).getNodeValue();
			}
		}
		return null;
	}

	public static String encodeReservedHTMLChars(String text) {
		String ret = text;
		ret = ret.replaceAll("&", "&amp;");
		ret = ret.replaceAll(">", "&gt;");
		ret = ret.replaceAll("<", "&lt;");
		return ret;
	}
}
