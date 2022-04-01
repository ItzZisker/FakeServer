package me.kallix.fakeserver.utils.serializer.chat;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

public final class ChatDeserializer {

    public static boolean getType(JsonObject var0, String var1) {
        if (!g(var0, var1)) {
            return false;
        } else {
            return var0.get(var1).isJsonArray();
        }
    }

    public static boolean g(JsonObject var0, String var1) {
        if (var0 == null) {
            return false;
        } else {
            return var0.get(var1) != null;
        }
    }

    public static String a(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive()) {
            return var0.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be a string, was " + getType(var0));
        }
    }

    public static String h(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return a(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find a string");
        }
    }

    public static boolean b(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive()) {
            return var0.getAsBoolean();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be a Boolean, was " + getType(var0));
        }
    }

    public static boolean a(JsonObject var0, String var1, boolean var2) {
        return var0.has(var1) ? b(var0.get(var1), var1) : var2;
    }

    public static float getType(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
            return var0.getAsFloat();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be a Float, was " + getType(var0));
        }
    }

    public static float a(JsonObject var0, String var1, float var2) {
        return var0.has(var1) ? getType(var0.get(var1), var1) : var2;
    }

    public static int f(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
            return var0.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be a Int, was " + getType(var0));
        }
    }

    public static int m(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return f(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Int");
        }
    }

    public static int a(JsonObject var0, String var1, int var2) {
        return var0.has(var1) ? f(var0.get(var1), var1) : var2;
    }

    public static JsonObject asJsonObject(JsonElement jsonElement, String object) {
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + object + " to be a JsonObject, was " + getType(jsonElement));
        }
    }

    public static JsonArray m(JsonElement var0, String var1) {
        if (var0.isJsonArray()) {
            return var0.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be a JsonArray, was " + getType(var0));
        }
    }

    public static JsonArray t(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return m(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find a JsonArray");
        }
    }

    public static String getType(JsonElement jsonElement) {

        String generic = StringUtils.abbreviateMiddle(String.valueOf(jsonElement), "...", 10);

        if (jsonElement == null) {
            return "null (missing)";
        } else if (jsonElement.isJsonNull()) {
            return "null (json)";
        } else if (jsonElement.isJsonArray()) {
            return "an array (" + generic + ")";
        } else if (jsonElement.isJsonObject()) {
            return "an object (" + generic + ")";
        } else {
            if (jsonElement.isJsonPrimitive()) {

                JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();

                if (primitive.isNumber()) {
                    return "a number (" + generic + ")";
                }
                if (primitive.isBoolean()) {
                    return "a boolean (" + generic + ")";
                }
            }
            return generic;
        }
    }
}
