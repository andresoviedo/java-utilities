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

	public static String encodeReservedHTMLChars(String text) {
		String ret = text;
		ret = ret.replaceAll("&", "&amp;");
		ret = ret.replaceAll(">", "&gt;");
		ret = ret.replaceAll("<", "&lt;");
		return ret;
	}
}
