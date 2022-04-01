package me.kallix.fakeserver.utils.serializer.chat;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

public final class ChatDeserializer {

    public static boolean isJsonArray(JsonObject jsonObject, String key) {
        if (!hasKey(jsonObject, key)) {
            return false;
        } else {
            return jsonObject.get(key).isJsonArray();
        }
    }

    public static boolean hasKey(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return false;
        } else {
            return jsonObject.get(key) != null;
        }
    }

    public static String asString(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a string, was " + getType(jsonElement));
        }
    }

    public static String asString(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return asString(jsonObject.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a string");
        }
    }

    public static boolean asBoolean(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsBoolean();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Boolean, was " + getType(jsonElement));
        }
    }

    public static boolean asBoolean(JsonObject jsonObject, String key, boolean bDefault) {
        return jsonObject.has(key) ? asBoolean(jsonObject.get(key), key) : bDefault;
    }

    public static float getType(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsFloat();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Float, was " + getType(jsonElement));
        }
    }

    public static float asFloat(JsonObject jsonObject, String key, float fDefault) {
        return jsonObject.has(key) ? getType(jsonObject.get(key), key) : fDefault;
    }

    public static int asInteger(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Int, was " + getType(jsonElement));
        }
    }

    public static int asInteger(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return asInteger(jsonObject.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a Int");
        }
    }

    public static int asInteger(JsonObject jsonObject, String key, int iDefault) {
        return jsonObject.has(key) ? asInteger(jsonObject.get(key), key) : iDefault;
    }

    public static JsonObject asJsonObject(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a JsonObject, was " + getType(jsonElement));
        }
    }

    public static JsonArray asJsonArray(JsonElement jsonelement, String key) {
        if (jsonelement.isJsonArray()) {
            return jsonelement.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a JsonArray, was " + getType(jsonelement));
        }
    }

    public static JsonArray asJsonArray_fromObj(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return asJsonArray(jsonObject.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonArray");
        }
    }

    public static String getType(JsonElement jsonElement) {

        String values = StringUtils.abbreviateMiddle(String.valueOf(jsonElement), "...", 10);

        if (jsonElement == null) {
            return "null (missing)";
        } else if (jsonElement.isJsonNull()) {
            return "null (json)";
        } else if (jsonElement.isJsonArray()) {
            return "an array (" + values + ")";
        } else if (jsonElement.isJsonObject()) {
            return "an object (" + values + ")";
        } else {
            if (jsonElement.isJsonPrimitive()) {

                JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();

                if (primitive.isNumber()) {
                    return "a number (" + values + ")";
                }
                if (primitive.isBoolean()) {
                    return "a boolean (" + values + ")";
                }
            }
            return values;
        }
    }
}
