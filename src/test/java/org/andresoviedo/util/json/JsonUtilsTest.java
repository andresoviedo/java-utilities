package org.andresoviedo.util.json;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

public class JsonUtilsTest {

	@SuppressWarnings("resource")
	@Test
	public void test_JSonArray_assertEquals() throws JSONException {
		JSONArray input1 = new JSONArray(new Scanner(JsonUtilsTest.class.getResourceAsStream("example_array_1.json")).useDelimiter("\\Z")
				.next());

		JSONArray input2 = new JSONArray(new Scanner(JsonUtilsTest.class.getResourceAsStream("example_array_2.json")).useDelimiter("\\Z")
				.next());

		Assert.assertTrue(JsonUtils.assertEquals(input1, input2));
	}
}
