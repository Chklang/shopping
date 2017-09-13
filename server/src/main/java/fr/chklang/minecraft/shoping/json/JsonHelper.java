package fr.chklang.minecraft.shoping.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonHelper {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	public static <T> T fromJson(String pString, Class<T> pClass)  {
		try {
			return objectMapper.readValue(pString, pClass);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJson(Object pObject)  {
		try {
			return objectMapper.writeValueAsString(pObject);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
