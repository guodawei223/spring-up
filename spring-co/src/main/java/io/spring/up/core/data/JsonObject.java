/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.spring.up.core.data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * A representation of a <a href="http://json.org/">JSON</a> object in Java.
 * <p>
 * Unlike some other languages Java does not have a native understanding of
 * JSON. To enable JSON to be used easily in Vert.x code we use this class to
 * encapsulate the notion of a JSON object.
 * <p>
 * The implementation adheres to the
 * <a href="http://rfc-editor.org/rfc/rfc7493.txt">RFC-7493</a> to support
 * Temporal data types as well as binary data.
 * <p>
 * Please see the documentation for more information.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class JsonObject implements Iterable<Map.Entry<String, Object>> {

    private Map<String, Object> map;

    /**
     * Create an instance from a string of JSON
     *
     * @param json the string of JSON
     */
    public JsonObject(final String json) {
        this.fromJson(json);
    }

    /**
     * Create a new, empty instance
     */
    public JsonObject() {
        this.map = new LinkedHashMap<>();
    }

    /**
     * Create an instance from a Map. The Map is not copied.
     *
     * @param map the map to create the instance from.
     */
    public JsonObject(final Map<String, Object> map) {
        this.map = map;
    }

    /**
     * Create a JsonObject from the fields of a Java object. Faster than calling
     * `new JsonObject(Json.encode(obj))`.
     *
     * @param obj The object to convert to a JsonObject.
     * @throws IllegalArgumentException if conversion fails due to an incompatible type.
     */
    public static JsonObject mapFrom(final Object obj) {
        return new JsonObject((Map<String, Object>) Json.mapper.convertValue(obj, Map.class));
    }

    /**
     * Instantiate a Java object from a JsonObject. Faster than calling
     * `Json.decodeValue(Json.encode(jsonObject), type)`.
     *
     * @param type The type to instantiate from the JsonObject.
     * @throws IllegalArgumentException if the type cannot be instantiated.
     */
    public <T> T mapTo(final Class<T> type) {
        return Json.mapper.convertValue(this.map, type);
    }

    /**
     * Get the string value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a String
     */
    public String getString(final String key) {
        Objects.requireNonNull(key);
        final CharSequence cs = (CharSequence) this.map.get(key);
        return cs == null ? null : cs.toString();
    }

    /**
     * Get the Integer value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not an Integer
     */
    public Integer getInteger(final String key) {
        Objects.requireNonNull(key);
        final Number number = (Number) this.map.get(key);
        if (number == null) {
            return null;
        } else if (number instanceof Integer) {
            return (Integer) number; // Avoids unnecessary unbox/box
        } else {
            return number.intValue();
        }
    }

    /**
     * Get the Long value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a Long
     */
    public Long getLong(final String key) {
        Objects.requireNonNull(key);
        final Number number = (Number) this.map.get(key);
        if (number == null) {
            return null;
        } else if (number instanceof Long) {
            return (Long) number; // Avoids unnecessary unbox/box
        } else {
            return number.longValue();
        }
    }

    /**
     * Get the Double value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a Double
     */
    public Double getDouble(final String key) {
        Objects.requireNonNull(key);
        final Number number = (Number) this.map.get(key);
        if (number == null) {
            return null;
        } else if (number instanceof Double) {
            return (Double) number; // Avoids unnecessary unbox/box
        } else {
            return number.doubleValue();
        }
    }

    /**
     * Get the Float value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a Float
     */
    public Float getFloat(final String key) {
        Objects.requireNonNull(key);
        final Number number = (Number) this.map.get(key);
        if (number == null) {
            return null;
        } else if (number instanceof Float) {
            return (Float) number; // Avoids unnecessary unbox/box
        } else {
            return number.floatValue();
        }
    }

    /**
     * Get the Boolean value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a Boolean
     */
    public Boolean getBoolean(final String key) {
        Objects.requireNonNull(key);
        return (Boolean) this.map.get(key);
    }

    /**
     * Get the JsonObject value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a JsonObject
     */
    public JsonObject getJsonObject(final String key) {
        Objects.requireNonNull(key);
        Object val = this.map.get(key);
        if (val instanceof Map) {
            val = new JsonObject((Map) val);
        }
        return (JsonObject) val;
    }

    /**
     * Get the JsonArray value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a JsonArray
     */
    public JsonArray getJsonArray(final String key) {
        Objects.requireNonNull(key);
        Object val = this.map.get(key);
        if (val instanceof List) {
            val = new JsonArray((List) val);
        }
        return (JsonArray) val;
    }

    /**
     * Get the binary value with the specified key.
     * <p>
     * JSON itself has no notion of a binary, this extension complies to the
     * RFC-7493, so this method assumes there is a String value with the key and
     * it contains a Base64 encoded binary, which it decodes if found and
     * returns.
     * <p>
     * This method should be used in conjunction with
     * {@link #put(String, byte[])}
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException       if the value is not a String
     * @throws java.lang.IllegalArgumentException if the String value is not a legal Base64 encoded value
     */
    public byte[] getBinary(final String key) {
        Objects.requireNonNull(key);
        final String encoded = (String) this.map.get(key);
        return encoded == null ? null : Base64.getDecoder().decode(encoded);
    }

    /**
     * Get the instant value with the specified key.
     * <p>
     * JSON itself has no notion of a temporal types, this extension complies to
     * the RFC-7493, so this method assumes there is a String value with the key
     * and it contains an ISO 8601 encoded date and time format such as
     * "2017-04-03T10:25:41Z", which it decodes if found and returns.
     * <p>
     * This method should be used in conjunction with
     * {@link #put(String, java.time.Instant)}
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException            if the value is not a String
     * @throws java.time.format.DateTimeParseException if the String value is not a legal ISO 8601 encoded value
     */
    public Instant getInstant(final String key) {
        Objects.requireNonNull(key);
        final String encoded = (String) this.map.get(key);
        return encoded == null ? null : Instant.from(ISO_INSTANT.parse(encoded));
    }

    /**
     * Get the value with the specified key, as an Object
     *
     * @param key the key to lookup
     * @return the value
     */
    public Object getValue(final String key) {
        Objects.requireNonNull(key);
        Object val = this.map.get(key);
        if (val instanceof Map) {
            val = new JsonObject((Map) val);
        } else if (val instanceof List) {
            val = new JsonArray((List) val);
        }
        return val;
    }

    /**
     * Like {@link #getString(String)} but specifying a default value to return
     * if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public String getString(final String key, final String def) {
        Objects.requireNonNull(key);
        final CharSequence cs = (CharSequence) this.map.get(key);
        return cs != null || this.map.containsKey(key) ? cs == null ? null : cs.toString() : def;
    }

    /**
     * Like {@link #getInteger(String)} but specifying a default value to return
     * if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Integer getInteger(final String key, final Integer def) {
        Objects.requireNonNull(key);
        final Number val = (Number) this.map.get(key);
        if (val == null) {
            if (this.map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Integer) {
            return (Integer) val; // Avoids unnecessary unbox/box
        } else {
            return val.intValue();
        }
    }

    /**
     * Like {@link #getLong(String)} but specifying a default value to return if
     * there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Long getLong(final String key, final Long def) {
        Objects.requireNonNull(key);
        final Number val = (Number) this.map.get(key);
        if (val == null) {
            if (this.map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Long) {
            return (Long) val; // Avoids unnecessary unbox/box
        } else {
            return val.longValue();
        }
    }

    /**
     * Like {@link #getDouble(String)} but specifying a default value to return
     * if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Double getDouble(final String key, final Double def) {
        Objects.requireNonNull(key);
        final Number val = (Number) this.map.get(key);
        if (val == null) {
            if (this.map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Double) {
            return (Double) val; // Avoids unnecessary unbox/box
        } else {
            return val.doubleValue();
        }
    }

    /**
     * Like {@link #getFloat(String)} but specifying a default value to return
     * if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Float getFloat(final String key, final Float def) {
        Objects.requireNonNull(key);
        final Number val = (Number) this.map.get(key);
        if (val == null) {
            if (this.map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Float) {
            return (Float) val; // Avoids unnecessary unbox/box
        } else {
            return val.floatValue();
        }
    }

    /**
     * Like {@link #getBoolean(String)} but specifying a default value to return
     * if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Boolean getBoolean(final String key, final Boolean def) {
        Objects.requireNonNull(key);
        final Object val = this.map.get(key);
        return val != null || this.map.containsKey(key) ? (Boolean) val : def;
    }

    /**
     * Like {@link #getJsonObject(String)} but specifying a default value to
     * return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public JsonObject getJsonObject(final String key, final JsonObject def) {
        final JsonObject val = this.getJsonObject(key);
        return val != null || this.map.containsKey(key) ? val : def;
    }

    /**
     * Like {@link #getJsonArray(String)} but specifying a default value to
     * return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public JsonArray getJsonArray(final String key, final JsonArray def) {
        final JsonArray val = this.getJsonArray(key);
        return val != null || this.map.containsKey(key) ? val : def;
    }

    /**
     * Like {@link #getBinary(String)} but specifying a default value to return
     * if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public byte[] getBinary(final String key, final byte[] def) {
        Objects.requireNonNull(key);
        final Object val = this.map.get(key);
        return val != null || this.map.containsKey(key) ? (val == null ? null : Base64.getDecoder().decode((String) val))
                : def;
    }

    /**
     * Like {@link #getInstant(String)} but specifying a default value to return
     * if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Instant getInstant(final String key, final Instant def) {
        Objects.requireNonNull(key);
        final Object val = this.map.get(key);
        return val != null || this.map.containsKey(key)
                ? (val == null ? null : Instant.from(ISO_INSTANT.parse((String) val))) : def;
    }

    /**
     * Like {@link #getValue(String)} but specifying a default value to return
     * if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Object getValue(final String key, final Object def) {
        Objects.requireNonNull(key);
        final Object val = this.getValue(key);
        return val != null || this.map.containsKey(key) ? val : def;
    }

    /**
     * Does the JSON object contain the specified key?
     *
     * @param key the key
     * @return true if it contains the key, false if not.
     */
    public boolean containsKey(final String key) {
        Objects.requireNonNull(key);
        return this.map.containsKey(key);
    }

    /**
     * Return the set of field names in the JSON objects
     *
     * @return the set of field names
     */
    public Set<String> fieldNames() {
        return this.map.keySet();
    }

    /**
     * Put an Enum into the JSON object with the specified key.
     * <p>
     * JSON has no concept of encoding Enums, so the Enum will be converted to a
     * String using the {@link java.lang.Enum#name} method and the value put as
     * a String.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final Enum value) {
        Objects.requireNonNull(key);
        this.map.put(key, value == null ? null : value.name());
        return this;
    }

    /**
     * Put an CharSequence into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final CharSequence value) {
        Objects.requireNonNull(key);
        this.map.put(key, value == null ? null : value.toString());
        return this;
    }

    /**
     * Put a String into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final String value) {
        Objects.requireNonNull(key);
        this.map.put(key, value);
        return this;
    }

    /**
     * Put an Integer into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final Integer value) {
        Objects.requireNonNull(key);
        this.map.put(key, value);
        return this;
    }

    /**
     * Put a Long into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final Long value) {
        Objects.requireNonNull(key);
        this.map.put(key, value);
        return this;
    }

    /**
     * Put a Double into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final Double value) {
        Objects.requireNonNull(key);
        this.map.put(key, value);
        return this;
    }

    /**
     * Put a Float into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final Float value) {
        Objects.requireNonNull(key);
        this.map.put(key, value);
        return this;
    }

    /**
     * Put a Boolean into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final Boolean value) {
        Objects.requireNonNull(key);
        this.map.put(key, value);
        return this;
    }

    /**
     * Put a null value into the JSON object with the specified key.
     *
     * @param key the key
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject putNull(final String key) {
        Objects.requireNonNull(key);
        this.map.put(key, null);
        return this;
    }

    /**
     * Put another JSON object into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final JsonObject value) {
        Objects.requireNonNull(key);
        this.map.put(key, value);
        return this;
    }

    /**
     * Put a JSON array into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final JsonArray value) {
        Objects.requireNonNull(key);
        this.map.put(key, value);
        return this;
    }

    /**
     * Put a byte[] into the JSON object with the specified key.
     * <p>
     * JSON extension RFC7493, binary will first be Base64 encoded before being
     * put as a String.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final byte[] value) {
        Objects.requireNonNull(key);
        this.map.put(key, value == null ? null : Base64.getEncoder().encodeToString(value));
        return this;
    }

    /**
     * Put a Instant into the JSON object with the specified key.
     * <p>
     * JSON extension RFC7493, instant will first be encoded to ISO 8601 date
     * and time String such as "2017-04-03T10:25:41Z".
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, final Instant value) {
        Objects.requireNonNull(key);
        this.map.put(key, value == null ? null : ISO_INSTANT.format(value));
        return this;
    }

    /**
     * Put an Object into the JSON object with the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject put(final String key, Object value) {
        Objects.requireNonNull(key);
        value = Json.checkAndCopy(value, false);
        this.map.put(key, value);
        return this;
    }

    /**
     * Remove an entry from this object.
     *
     * @param key the key
     * @return the value that was removed, or null if none
     */
    public Object remove(final String key) {
        return this.map.remove(key);
    }

    /**
     * Merge in another JSON object.
     * <p>
     * This is the equivalent of putting all the entries of the other JSON
     * object into this object. This is not a deep merge, entries containing
     * (sub) JSON objects will be replaced entirely.
     *
     * @param other the other JSON object
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject mergeIn(final JsonObject other) {
        return this.mergeIn(other, false);
    }

    /**
     * Merge in another JSON object. A deep merge (recursive) matches (sub) JSON
     * objects in the existing tree and replaces all matching entries.
     * JsonArrays are treated like any other entry, i.e. replaced entirely.
     *
     * @param other the other JSON object
     * @param deep  if true, a deep merge is performed
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject mergeIn(final JsonObject other, final boolean deep) {
        return this.mergeIn(other, deep ? Integer.MAX_VALUE : 1);
    }

    /**
     * Merge in another JSON object. The merge is deep (recursive) to the
     * specified level. If depth is 0, no merge is performed, if depth is
     * greater than the depth of one of the objects, a full deep merge is
     * performed.
     *
     * @param other the other JSON object
     * @param depth depth of merge
     * @return a reference to this, so the API can be used fluently
     */
    public JsonObject mergeIn(final JsonObject other, final int depth) {
        if (depth < 1) {
            return this;
        }
        if (depth == 1) {
            this.map.putAll(other.map);
            return this;
        }
        for (final Map.Entry<String, Object> e : other.map.entrySet()) {
            this.map.merge(e.getKey(), e.getValue(), (oldVal, newVal) -> {
                if (oldVal instanceof Map) {
                    oldVal = new JsonObject((Map) oldVal);
                }
                if (newVal instanceof Map) {
                    newVal = new JsonObject((Map) newVal);
                }
                if (oldVal instanceof JsonObject && newVal instanceof JsonObject) {
                    return ((JsonObject) oldVal).mergeIn((JsonObject) newVal, depth - 1);
                }
                return newVal;
            });
        }
        return this;
    }

    /**
     * Encode this JSON object as a string.
     *
     * @return the string encoding.
     */
    public String encode() {
        return Json.encode(this.map);
    }

    /**
     * Encode this JSON object a a string, with whitespace to make the object
     * easier to read by a human, or other sentient organism.
     *
     * @return the pretty string encoding.
     */
    public String encodePrettily() {
        return Json.encodePrettily(this.map);
    }

    /**
     * Copy the JSON object
     *
     * @return a copy of the object
     */
    public JsonObject copy() {
        final Map<String, Object> copiedMap;
        if (this.map instanceof LinkedHashMap) {
            copiedMap = new LinkedHashMap<>(this.map.size());
        } else {
            copiedMap = new HashMap<>(this.map.size());
        }
        for (final Map.Entry<String, Object> entry : this.map.entrySet()) {
            Object val = entry.getValue();
            val = Json.checkAndCopy(val, true);
            copiedMap.put(entry.getKey(), val);
        }
        return new JsonObject(copiedMap);
    }

    /**
     * Get the underlying {@code Map} as is.
     * <p>
     * This map may contain values that are not the types returned by the
     * {@code JsonObject}.
     *
     * @return the underlying Map.
     */
    public Map<String, Object> getMap() {
        return this.map;
    }

    /**
     * Get a stream of the entries in the JSON object.
     *
     * @return a stream of the entries.
     */
    public Stream<Map.Entry<String, Object>> stream() {
        return Json.asStream(this.iterator());
    }

    /**
     * Get an Iterator of the entries in the JSON object.
     *
     * @return an Iterator of the entries
     */
    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return new Iter(this.map.entrySet().iterator());
    }

    /**
     * Get the number of entries in the JSON object
     *
     * @return the number of entries
     */
    public int size() {
        return this.map.size();
    }

    /**
     * Remove all the entries in this JSON object
     */
    public JsonObject clear() {
        this.map.clear();
        return this;
    }

    /**
     * Is this object entry?
     *
     * @return true if it has zero entries, false if not.
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public String toString() {
        return this.encode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        return objectEquals(this.map, o);
    }

    static boolean objectEquals(final Map<?, ?> m1, final Object o2) {
        final Map<?, ?> m2;
        if (o2 instanceof JsonObject) {
            m2 = ((JsonObject) o2).map;
        } else if (o2 instanceof Map<?, ?>) {
            m2 = (Map<?, ?>) o2;
        } else {
            return false;
        }
        if (m1.size() != m2.size())
            return false;
        for (final Map.Entry<?, ?> entry : m1.entrySet()) {
            final Object val = entry.getValue();
            if (val == null) {
                if (m2.get(entry.getKey()) != null) {
                    return false;
                }
            } else {
                if (!equals(entry.getValue(), m2.get(entry.getKey()))) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean equals(final Object o1, final Object o2) {
        if (o1 == o2)
            return true;
        if (o1 instanceof JsonObject) {
            return objectEquals(((JsonObject) o1).map, o2);
        }
        if (o1 instanceof Map<?, ?>) {
            return objectEquals((Map<?, ?>) o1, o2);
        }
        if (o1 instanceof JsonArray) {
            return JsonArray.arrayEquals(((JsonArray) o1).getList(), o2);
        }
        if (o1 instanceof List<?>) {
            return JsonArray.arrayEquals((List<?>) o1, o2);
        }
        if (o1 instanceof Number && o2 instanceof Number && o1.getClass() != o2.getClass()) {
            final Number n1 = (Number) o1;
            final Number n2 = (Number) o2;
            if (o1 instanceof Float || o1 instanceof Double || o2 instanceof Float || o2 instanceof Double) {
                return n1.doubleValue() == n2.doubleValue();
            } else {
                return n1.longValue() == n2.longValue();
            }
        }
        return o1.equals(o2);
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    private void fromJson(final String json) {
        this.map = Json.decodeValue(json, Map.class);
    }

    private class Iter implements Iterator<Map.Entry<String, Object>> {

        final Iterator<Map.Entry<String, Object>> mapIter;

        Iter(final Iterator<Map.Entry<String, Object>> mapIter) {
            this.mapIter = mapIter;
        }

        @Override
        public boolean hasNext() {
            return this.mapIter.hasNext();
        }

        @Override
        public Map.Entry<String, Object> next() {
            final Map.Entry<String, Object> entry = this.mapIter.next();
            if (entry.getValue() instanceof Map) {
                return new Entry(entry.getKey(), new JsonObject((Map) entry.getValue()));
            } else if (entry.getValue() instanceof List) {
                return new Entry(entry.getKey(), new JsonArray((List) entry.getValue()));
            }
            return entry;
        }

        @Override
        public void remove() {
            this.mapIter.remove();
        }
    }

    private static final class Entry implements Map.Entry<String, Object> {
        final String key;
        final Object value;

        public Entry(final String key, final Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public Object setValue(final Object value) {
            throw new UnsupportedOperationException();
        }
    }
}
