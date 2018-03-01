package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonParser;
import io.spring.up.core.data.JsonArray;

import java.io.IOException;

public class JsonArrayDeserializer extends JsonDeserializer<JsonArray> {

    @Override
    public JsonArray deserialize(final JsonParser parser,
                                 final DeserializationContext context)
            throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return new JsonArray(node.toString());
    }
}
