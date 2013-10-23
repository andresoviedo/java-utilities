package org.andresoviedo.tests.regexp;

import java.util.regex.Matcher;

public class RegExpTests {

	public static void main(String[] args) {
		String string = "c:\\archivos de programa\\blah\\a.exe";
		System.out.println(string);
		System.out.println(string.replaceAll("\\\\",
				Matcher.quoteReplacement("\\")));
	}
}
