package me.kallix.fakeserver.utils.serializer.chat;

import com.google.gson.*;
import me.kallix.fakeserver.utils.chat.*;

import java.lang.reflect.Type;
import java.util.Map;

public class ChatSerializer implements JsonDeserializer<IChatBaseComponent>, JsonSerializer<IChatBaseComponent> {

    private static final Gson chat = new GsonBuilder()
            .registerTypeHierarchyAdapter(IChatBaseComponent.class, new ChatSerializer())
            .registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer())
            .registerTypeAdapterFactory(new ChatTypeAdapterFactory())
            .create();

    public IChatBaseComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        if (jsonElement.isJsonPrimitive()) {
            return new ChatComponentText(jsonElement.getAsString());
        } else if (!jsonElement.isJsonObject()) {

            if (jsonElement.isJsonArray()) {

                JsonArray jsonArray = jsonElement.getAsJsonArray();
                IChatBaseComponent chat = null;

                for (JsonElement eachElement : jsonArray) {

                    IChatBaseComponent deserialized = deserialize(eachElement, eachElement.getClass(), context);

                    if (chat == null) {
                        chat = deserialized;
                    } else {
                        chat.addSibling(deserialized);
                    }
                }
                return chat;
            } else {
                throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
            }
        } else {

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            IChatBaseComponent text;

            if (jsonObject.has("text")) {
                text = new ChatComponentText(jsonObject.get("text").getAsString());
            } else if (jsonObject.has("translate")) {

                String translate = jsonObject.get("translate").getAsString();

                if (jsonObject.has("with")) {

                    JsonArray nextToArray = jsonObject.getAsJsonArray("with");
                    Object[] objectsAsArray = new Object[nextToArray.size()];

                    for (int var9 = 0; var9 < objectsAsArray.length; ++var9) {

                        objectsAsArray[var9] = deserialize(nextToArray.get(var9), type, context);

                        if (objectsAsArray[var9] instanceof ChatComponentText) {

                            ChatComponentText text_ = (ChatComponentText) objectsAsArray[var9];

                            if (text_.getChatModifier().g() && text_.a().isEmpty()) {
                                objectsAsArray[var9] = text_.g();
                            }
                        }
                    }
                    text = new ChatMessage(translate, objectsAsArray);
                } else {
                    text = new ChatMessage(translate);
                }
            } else if (jsonObject.has("score")) {

                JsonObject scoreObject = jsonObject.getAsJsonObject("score");

                if (!scoreObject.has("name") || !scoreObject.has("objective")) {
                    throw new JsonParseException("A score component needs a least a name and an objective");
                }

                text = new ChatComponentScore(
                        ChatDeserializer.asString(scoreObject, "name"),
                        ChatDeserializer.asString(scoreObject, "objective")
                );

                if (scoreObject.has("value")) {
                    ((ChatComponentScore) text).b(ChatDeserializer.asString(scoreObject, "value"));
                }
            } else {

                if (!jsonObject.has("selector")) {
                    throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                }

                text = new ChatComponentSelector(ChatDeserializer.asString(jsonObject, "selector"));
            }

            if (jsonObject.has("extra")) {

                JsonArray extraArray = jsonObject.getAsJsonArray("extra");

                if (extraArray.size() <= 0) {
                    throw new JsonParseException("Unexpected empty array of components");
                }

                for (int var16 = 0; var16 < extraArray.size(); ++var16) {
                    text.addSibling(this.deserialize(extraArray.get(var16), type, context));
                }
            }
            text.setChatModifier(context.deserialize(jsonElement, ChatModifier.class));
            return text;
        }
    }

    private void a(ChatModifier modifier, JsonObject jsonObject, JsonSerializationContext context) {

        JsonElement jsonElement = context.serialize(modifier);

        if (jsonElement.isJsonObject()) {

            JsonObject jsonObject_ = (JsonObject) jsonElement;

            for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObject_.entrySet()) {
                jsonObject.add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
            }
        }
    }

    public JsonElement serialize(IChatBaseComponent text, Type type, JsonSerializationContext context) {

        if (text instanceof ChatComponentText && text.getChatModifier().g() && text.a().isEmpty()) {
            return new JsonPrimitive(((ChatComponentText) text).g());
        } else {

            JsonObject jsonObject = new JsonObject();

            if (!text.getChatModifier().g()) {
                this.a(text.getChatModifier(), jsonObject, context);
            }

            if (!text.a().isEmpty()) {

                JsonArray jsonArray = new JsonArray();

                for (IChatBaseComponent eachChat : text.a()) {
                    jsonArray.add(this.serialize(eachChat, eachChat.getClass(), context));
                }
                jsonObject.add("extra", jsonArray);
            }

            if (text instanceof ChatComponentText) {
                jsonObject.addProperty("text", ((ChatComponentText) text).g());
            } else if (text instanceof ChatMessage) {

                ChatMessage var11 = (ChatMessage) text;
                jsonObject.addProperty("translate", var11.i());

                if (var11.j() != null && var11.j().length > 0) {

                    JsonArray var14 = new JsonArray();
                    Object[] var16 = var11.j();

                    for (Object var10 : var16) {
                        if (var10 instanceof IChatBaseComponent) {
                            var14.add(this.serialize((IChatBaseComponent) var10, var10.getClass(), context));
                        } else {
                            var14.add(new JsonPrimitive(String.valueOf(var10)));
                        }
                    }
                    jsonObject.add("with", var14);
                }
            } else if (text instanceof ChatComponentScore) {

                ChatComponentScore var12 = (ChatComponentScore) text;
                JsonObject var15 = new JsonObject();

                var15.addProperty("name", var12.g());
                var15.addProperty("objective", var12.h());
                var15.addProperty("value", var12.getText());
                jsonObject.add("score", var15);
            } else {

                if (!(text instanceof ChatComponentSelector)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + text + " as a ComponentSelector");
                }

                ChatComponentSelector var13 = (ChatComponentSelector) text;
                jsonObject.addProperty("selector", var13.g());
            }
            return jsonObject;
        }
    }

    public static String toJson(IChatBaseComponent text) {
        return ChatSerializer.chat.toJson(text);
    }

    public static IChatBaseComponent fromJson(String textJson) {
        return chat.fromJson(textJson, IChatBaseComponent.class);
    }
}
