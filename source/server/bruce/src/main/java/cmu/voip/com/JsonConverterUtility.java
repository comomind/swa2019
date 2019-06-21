package cmu.voip.com;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverterUtility {
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static Object jsonString2Object(String input, TypeReference ref) {
		Object result = null;
		try {
			result = mapper.readValue(input,ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result;
	}
	
	
}
