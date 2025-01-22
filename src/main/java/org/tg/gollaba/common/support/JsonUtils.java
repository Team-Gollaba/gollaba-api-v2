package org.tg.gollaba.common.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    private JsonUtils() {
    }

    public static String stringify(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(String jsonString,
                              Class<T> clazz) {
        try {
            return objectMapper.readValue(
                    jsonString,
                    clazz
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readTree(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseArray(String jsonString,
                                         Class<T> clazz) {
        try {
            return objectMapper.readValue(
                    jsonString,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(
                                    ArrayList.class,
                                    clazz
                            )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
