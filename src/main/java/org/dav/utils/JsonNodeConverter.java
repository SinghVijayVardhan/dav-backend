package org.dav.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        try {
            return jsonNode != null ? objectMapper.writeValueAsString(jsonNode) : "{}";
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to String", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String json) {
        try {
            return json != null ? objectMapper.readTree(json) : objectMapper.createObjectNode();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert String to JSON", e);
        }
    }
}
