package io.boodskap.iot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ThreadContext {

	public static final ObjectMapper mapper = new ObjectMapper();
	
	private static final TypeReference<Map<String, Object>> TR = new TypeReference<Map<String, Object>>() {
	};
	
	public static String toJSON(Object object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}

	public static Map<String, Object> jsonToMap(String dataJson) throws JsonParseException, JsonMappingException, IOException {

		dataJson = dataJson.trim();
		if (dataJson.startsWith("\"")) {
			dataJson = dataJson.replaceAll("\\\\", "");
			dataJson = dataJson.substring(1, dataJson.length() - 1);
		}

		Map<String, Object> props = ThreadContext.mapper.readValue(dataJson, TR);

		return props;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
	    Map<String, Object> map = new HashMap<String, Object>();
	 
	    Iterator<String> keysItr = object.keys();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);
	 
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }
	 
	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        map.put(key, value);
	    }
	    return map;
	}
	 
	public static List<Object> toList(JSONArray array) throws JSONException {
		
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.length(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }
	 
	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}
	
	public static long longHash(String string) {
		long h = 98764321261L;
		int l = string.length();
		char[] chars = string.toCharArray();

		for (int i = 0; i < l; i++) {
			h = 31 * h + chars[i];
		}
		return h;
	}
	
	public static String uniqueHash(String string) {
		return String.valueOf(longHash(string));
	}
}
