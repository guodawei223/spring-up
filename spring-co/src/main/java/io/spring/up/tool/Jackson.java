package io.spring.up.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.spring.up.cv.Strings;
import io.spring.up.tool.fn.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@SuppressWarnings("all")
class Jackson {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jackson.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // TimeZone Issue
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Jackson.MAPPER.setDateFormat(dateFormat);
        // Ignore null value
        Jackson.MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Non-standard JSON but we allow C style comments in our JSON
        Jackson.MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        Jackson.MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Jackson.MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Jackson.MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

        final SimpleModule module = new SimpleModule();
        // custom types
        module.addSerializer(JsonObject.class, new JsonObjectSerializer());
        module.addSerializer(JsonArray.class, new JsonArraySerializer());
        // he have 2 extensions: RFC-7493
        module.addSerializer(Instant.class, new InstantSerializer());
        module.addSerializer(byte[].class, new ByteArraySerializer());
        // Zero Extension
        module.addDeserializer(JsonObject.class, new JsonObjectDeserializer());
        module.addDeserializer(JsonArray.class, new JsonArrayDeserializer());

        Jackson.MAPPER.registerModule(module);
        Jackson.MAPPER.findAndRegisterModules();
    }

    static ObjectMapper getMapper() {
        return MAPPER;
    }


    static <T, R extends Iterable> R serializeJson(final T t) {
        final String content = Jackson.serialize(t);
        return Fn.getJvm(null,
                () -> {
                    if (content.trim().startsWith(Strings.LEFT_BRACE)) {
                        return (R) new JsonObject(content);
                    } else {
                        return (R) new JsonObject(content);
                    }
                }, content);
    }

    static <T> String serialize(final T t) {
        return Fn.getJvm(null, () -> Fn.getJvm(() -> Jackson.MAPPER.writeValueAsString(t), t), t);
    }


    static <T> T deserialize(final JsonObject value, final Class<T> type) {
        return Fn.getJvm(null,
                () -> Jackson.deserialize(value.encode(), type), value);
    }

    static <T> T deserialize(final JsonArray value, final Class<T> type) {
        return Fn.getJvm(null,
                () -> Jackson.deserialize(value.encode(), type), value);
    }

    static <T> List<T> deserialize(final JsonArray value, final TypeReference<List<T>> type) {
        return Fn.getJvm(new ArrayList<>(),
                () -> Jackson.deserialize(value.encode(), type), value);
    }

    static <T> T deserialize(final String value, final Class<T> type) {
        return Fn.getJvm(null,
                () -> Fn.getJvm(() -> Jackson.MAPPER.readValue(value, type)), value);
    }

    static <T> T deserialize(final String value, final TypeReference<T> type) {
        return Fn.getJvm(null,
                () -> Fn.getJvm(() -> Jackson.MAPPER.readValue(value, type)), value);
    }

    static Object readJson(final Object value, final JsonObject data, final String key) {
        return Fn.getNull(value, () -> {
            final Object result = data.getValue(key);
            return Fn.getNull(value, () -> result, result);
        }, data, key);
    }

    static Integer readInt(final Integer value, final JsonObject data, final String key) {
        return Fn.getNull(value, () -> {
            final Object result = data.getValue(key);
            return Types.isInteger(result) ? To.toInteger(result) : value;
        }, data, key);
    }
}
