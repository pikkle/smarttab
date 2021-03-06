package ch.epfl.sweng.smartTabs.test;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import ch.epfl.sweng.smartTabs.music.Tab;

/**
 * @author rphkhr Raphael El-Khoury 212765
 */
public class TabTest extends AndroidTestCase {
	public String goodTabString = "{ \"name\": \"foo\", \"artist\": fooArtist, \"tempo\": 120, \"partition\": [ { \"string_1\": \"0\", \"string_2\": \"1\", \"string_3\": \"2\", \"string_4\": \"2\", \"string_5\": \"0\", \"string_6\": \"0\", \"step\": 1, \"length\" : \"Noire\"}]}";
	public String badTabString = "{ \"name\": \"foo\", \"\": false, \"tempo\": 120, \"partition\": [  \"string_1\": \"0\", \"string_2\": \"1\", \"string_3\": \"2\", \"string_4\": \"2\", \"string_5\": \"0\", \"string_6\": \"0\", \"step\": 1, \"length\" : \"Noire\"}]}";

	private List<String> jsonFields;

	protected void setUp() throws Exception {
		super.setUp();

		jsonFields = new ArrayList<String>();
		jsonFields.add("name");
		jsonFields.add("complex");
		jsonFields.add("tempo");
		jsonFields.add("partition");
	}

	public void testJSONParsing() throws JSONException {
		JSONObject jObj = new JSONObject(goodTabString);
		Tab parsedTab = Tab.parseTabFromJSON(jObj);
		assertNotNull(parsedTab);
	}

	public void testJSONName() throws JSONException {
		JSONObject jObj = new JSONObject(goodTabString);
		Tab parsedTab = Tab.parseTabFromJSON(jObj);
		assertEquals(parsedTab.getTabName(), jObj.getString("name"));
		assertEquals("foo", parsedTab.getTabName());
		assertEquals("foo", jObj.getString("name"));
	}

	public void testJSONArtist()throws JSONException  {
		JSONObject jObj = new JSONObject(goodTabString);
		Tab parsedTab = Tab.parseTabFromJSON(jObj);
		assertEquals(parsedTab.getTabArtist(), jObj.getString("artist"));
		assertEquals("fooArtist", parsedTab.getTabArtist());
		assertEquals("fooArtist", jObj.getString("artist"));

	}

	public void testJSONTempo() throws JSONException {
		JSONObject jObj = new JSONObject(goodTabString);
		Tab parsedTab = Tab.parseTabFromJSON(jObj);
		assertEquals(120, parsedTab.getTempo());
		int falseTempo = 121;
		assertFalse(falseTempo == parsedTab.getTempo());
	}

	public void testJSONLength() throws JSONException {
		JSONObject jObj = new JSONObject(goodTabString);
		Tab parsedTab = Tab.parseTabFromJSON(jObj);
		assertEquals("Noire", parsedTab.getTime(0).getDuration());
		assertFalse("" == parsedTab.getTime(0).getDuration());
	}

	public void testEmptyJSONParsing() throws JSONException {
		try {
			Tab.parseTabFromJSON(new JSONObject());
			fail("Parsed empty JSON");
		} catch (JSONException e) {
			// success
		}
	}

	public void testBadJSONParsing() throws JSONException {
		try {
			JSONObject jObj = new JSONObject(badTabString);
			Tab parsedTab = Tab.parseTabFromJSON(jObj);
			assertTrue(parsedTab == null);
		} catch (JSONException e) {
			// success
		}
	}

}