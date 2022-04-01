package me.kallix.fakeserver.utils.serializer.chat;

import com.google.gson.*;
import me.kallix.fakeserver.utils.chat.*;

import java.lang.reflect.Type;
import java.util.Map;

public class ChatSerializer implements JsonDeserializer<IChatBaseComponent>, JsonSerializer<IChatBaseComponent> {

    private static final Gson a;

    static {
        GsonBuilder var0 = new GsonBuilder();
        var0.registerTypeHierarchyAdapter(IChatBaseComponent.class, new ChatSerializer());
        var0.registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer());
        var0.registerTypeAdapterFactory(new ChatTypeAdapterFactory());
        a = var0.create();
    }

    public IChatBaseComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
        if (var1.isJsonPrimitive()) {
            return new ChatComponentText(var1.getAsString());
        } else if (!var1.isJsonObject()) {
            if (var1.isJsonArray()) {

                JsonArray var11 = var1.getAsJsonArray();
                IChatBaseComponent var15 = null;

                for (JsonElement var17 : var11) {
                    IChatBaseComponent var18 = this.deserialize(var17, var17.getClass(), var3);
                    if (var15 == null) {
                        var15 = var18;
                    } else {
                        var15.addSibling(var18);
                    }
                }

                return var15;
            } else {
                throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
            }
        } else {
            JsonObject var4 = var1.getAsJsonObject();
            IChatBaseComponent var5;
            if (var4.has("text")) {
                var5 = new ChatComponentText(var4.get("text").getAsString());
            } else if (var4.has("translate")) {
                String var12 = var4.get("translate").getAsString();
                if (var4.has("with")) {

                    JsonArray var7 = var4.getAsJsonArray("with");
                    Object[] var8 = new Object[var7.size()];

                    for(int var9 = 0; var9 < var8.length; ++var9) {
                        var8[var9] = this.deserialize(var7.get(var9), var2, var3);
                        if (var8[var9] instanceof ChatComponentText) {
                            ChatComponentText var10 = (ChatComponentText)var8[var9];
                            if (var10.getChatModifier().g() && var10.a().isEmpty()) {
                                var8[var9] = var10.g();
                            }
                        }
                    }
                    var5 = new ChatMessage(var12, var8);
                } else {
                    var5 = new ChatMessage(var12);
                }
            } else if (var4.has("score")) {

                JsonObject var6 = var4.getAsJsonObject("score");

                if (!var6.has("name") || !var6.has("objective")) {
                    throw new JsonParseException("A score component needs a least a name and an objective");
                }

                var5 = new ChatComponentScore(ChatDeserializer.h(var6, "name"), ChatDeserializer.h(var6, "objective"));

                if (var6.has("value")) {
                    ((ChatComponentScore)var5).b(ChatDeserializer.h(var6, "value"));
                }
            } else {
                if (!var4.has("selector")) {
                    throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
                }

                var5 = new ChatComponentSelector(ChatDeserializer.h(var4, "selector"));
            }

            if (var4.has("extra")) {
                JsonArray var13 = var4.getAsJsonArray("extra");
                if (var13.size() <= 0) {
                    throw new JsonParseException("Unexpected empty array of components");
                }

                for(int var16 = 0; var16 < var13.size(); ++var16) {
                    var5.addSibling(this.deserialize(var13.get(var16), var2, var3));
                }
            }
            var5.setChatModifier(var3.deserialize(var1, ChatModifier.class));
            return var5;
        }
    }

    private void a(ChatModifier var1, JsonObject var2, JsonSerializationContext var3) {

        JsonElement var4 = var3.serialize(var1);

        if (var4.isJsonObject()) {

            JsonObject var5 = (JsonObject)var4;

            for (Map.Entry<String, JsonElement> stringJsonElementEntry : var5.entrySet()) {
                var2.add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
            }
        }
    }

    public JsonElement serialize(IChatBaseComponent var1, Type var2, JsonSerializationContext var3) {
        if (var1 instanceof ChatComponentText && var1.getChatModifier().g() && var1.a().isEmpty()) {
            return new JsonPrimitive(((ChatComponentText)var1).g());
        } else {
            JsonObject var4 = new JsonObject();
            if (!var1.getChatModifier().g()) {
                this.a(var1.getChatModifier(), var4, var3);
            }

            if (!var1.a().isEmpty()) {
                JsonArray var5 = new JsonArray();

                for (IChatBaseComponent var7 : var1.a()) {
                    var5.add(this.serialize(var7, var7.getClass(), var3));
                }
                var4.add("extra", var5);
            }

            if (var1 instanceof ChatComponentText) {
                var4.addProperty("text", ((ChatComponentText)var1).g());
            } else if (var1 instanceof ChatMessage) {
                ChatMessage var11 = (ChatMessage)var1;
                var4.addProperty("translate", var11.i());
                if (var11.j() != null && var11.j().length > 0) {

                    JsonArray var14 = new JsonArray();
                    Object[] var16 = var11.j();

                    for (Object var10 : var16) {
                        if (var10 instanceof IChatBaseComponent) {
                            var14.add(this.serialize((IChatBaseComponent) var10, var10.getClass(), var3));
                        } else {
                            var14.add(new JsonPrimitive(String.valueOf(var10)));
                        }
                    }
                    var4.add("with", var14);
                }
            } else if (var1 instanceof ChatComponentScore) {
                ChatComponentScore var12 = (ChatComponentScore)var1;
                JsonObject var15 = new JsonObject();
                var15.addProperty("name", var12.g());
                var15.addProperty("objective", var12.h());
                var15.addProperty("value", var12.getText());
                var4.add("score", var15);
            } else {
                if (!(var1 instanceof ChatComponentSelector)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + var1 + " as a Component");
                }

                ChatComponentSelector var13 = (ChatComponentSelector)var1;
                var4.addProperty("selector", var13.g());
            }
            return var4;
        }
    }

    public static String a(IChatBaseComponent var0) {
        return a.toJson(var0);
    }

    public static IChatBaseComponent a(String var0) {
        return a.fromJson(var0, IChatBaseComponent.class);
    }
}
