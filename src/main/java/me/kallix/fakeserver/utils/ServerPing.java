package me.kallix.fakeserver.utils;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import me.kallix.fakeserver.utils.chat.IChatBaseComponent;
import me.kallix.fakeserver.utils.serializer.chat.ChatDeserializer;

import java.lang.reflect.Type;
import java.util.UUID;

public class ServerPing {

    private IChatBaseComponent motd;
    private ServerPing.ServerPingPlayerSample playerSample;
    private ServerPing.ServerData serverData;
    private String iconBase64;

    public IChatBaseComponent getMotd() {
        return motd;
    }

    public void setMOTD(IChatBaseComponent motd) {
        this.motd = motd;
    }

    public ServerPing.ServerPingPlayerSample getPlayerSample() {
        return playerSample;
    }

    public void setPlayerSample(ServerPing.ServerPingPlayerSample playerSample) {
        this.playerSample = playerSample;
    }

    public ServerPing.ServerData getServerData() {
        return serverData;
    }

    public void setServerInfo(ServerPing.ServerData serverData) {
        this.serverData = serverData;
    }

    public void setFavicon(String base64) {
        this.iconBase64 = base64;
    }

    public String getServerIcon() {
        return iconBase64;
    }

    public static class Serializer implements JsonDeserializer<ServerPing>, JsonSerializer<ServerPing> {

        public ServerPing deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

            JsonObject motdJson = ChatDeserializer.asJsonObject(jsonElement, "status");
            ServerPing serverPing = new ServerPing();

            if (motdJson.has("description")) {
                serverPing.setMOTD(context.deserialize(motdJson.get("description"), IChatBaseComponent.class));
            }

            if (motdJson.has("players")) {
                serverPing.setPlayerSample(context.deserialize(motdJson.get("players"), ServerPingPlayerSample.class));
            }

            if (motdJson.has("version")) {
                serverPing.setServerInfo(context.deserialize(motdJson.get("version"), ServerData.class));
            }

            if (motdJson.has("favicon")) {
                serverPing.setFavicon(ChatDeserializer.asString(motdJson, "favicon"));
            }
            return serverPing;
        }

        public JsonElement serialize(ServerPing serverPing, Type type, JsonSerializationContext context) {

            JsonObject jsonObject = new JsonObject();

            if (serverPing.getMotd() != null) {
                jsonObject.add("description", context.serialize(serverPing.getMotd()));
            }

            if (serverPing.getPlayerSample() != null) {
                jsonObject.add("players", context.serialize(serverPing.getPlayerSample()));
            }

            if (serverPing.getServerData() != null) {
                jsonObject.add("version", context.serialize(serverPing.getServerData()));
            }

            if (serverPing.getServerIcon() != null) {
                jsonObject.addProperty("favicon", serverPing.getServerIcon());
            }
            return jsonObject;
        }
    }

    public static class ServerData {

        private final String data;
        private final int protocolVersion;

        public ServerData(String data, int protocolVersion) {
            this.data = data;
            this.protocolVersion = protocolVersion;
        }

        public String getData() {
            return data;
        }

        public int getProtocol() {
            return protocolVersion;
        }

        public static class Serializer implements JsonDeserializer<ServerPing.ServerData>, JsonSerializer<ServerPing.ServerData> {

            public ServerPing.ServerData deserialize_N(JsonElement jsonelement) throws JsonParseException {

                JsonObject jsonobject = ChatDeserializer.asJsonObject(jsonelement, "version");

                return new ServerPing.ServerData(ChatDeserializer.asString(jsonobject, "name"), ChatDeserializer.asInteger(jsonobject, "protocol"));
            }

            public JsonElement serialize_N(ServerPing.ServerData serverData) {

                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty("name", serverData.getData());
                jsonobject.addProperty("protocol", serverData.getProtocol());

                return jsonobject;
            }

            public JsonElement serialize(ServerPing.ServerData data, Type type, JsonSerializationContext context) {
                return serialize_N(data);
            }

            public ServerPing.ServerData deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext context) throws JsonParseException {
                return deserialize_N(jsonelement);
            }
        }
    }

    public static class ServerPingPlayerSample {

        private final int playerCount;
        private final int playerCount_Max;
        private GameProfile[] gameProfiles;

        public ServerPingPlayerSample(int playerCount, int playerCount_Max) {
            this.playerCount = playerCount;
            this.playerCount_Max = playerCount_Max;
        }

        public int getPlayerCount() {
            return playerCount;
        }

        public int getPlayerCount_Max() {
            return playerCount_Max;
        }

        public GameProfile[] getProfiles() {
            return gameProfiles;
        }

        public void setProfiles(GameProfile[] gameProfiles) {
            this.gameProfiles = gameProfiles;
        }

        public static class Serializer implements JsonDeserializer<ServerPing.ServerPingPlayerSample>, JsonSerializer<ServerPing.ServerPingPlayerSample> {

            public ServerPing.ServerPingPlayerSample deserialize_N(JsonElement jsonelement) throws JsonParseException {

                JsonObject jsonobject = ChatDeserializer.asJsonObject(jsonelement, "players");
                ServerPing.ServerPingPlayerSample playerSample = new ServerPing.ServerPingPlayerSample(ChatDeserializer.asInteger(jsonobject, "max"), ChatDeserializer.asInteger(jsonobject, "online"));

                if (ChatDeserializer.isJsonArray(jsonobject, "sample")) {

                    JsonArray jsonarray = ChatDeserializer.asJsonArray_fromObj(jsonobject, "sample");

                    if (jsonarray.size() > 0) {

                        GameProfile[] profiles = new GameProfile[jsonarray.size()];

                        for (int k = 0; k < profiles.length; ++k) {

                            JsonObject profileObject = ChatDeserializer.asJsonObject(jsonarray.get(k), "player[" + k + "]");

                            profiles[k] = new GameProfile(
                                    UUID.fromString(ChatDeserializer.asString(profileObject, "id")),
                                    ChatDeserializer.asString(profileObject, "name"));
                        }
                        playerSample.setProfiles(profiles);
                    }
                }
                return playerSample;
            }

            public JsonElement serialize_N(ServerPing.ServerPingPlayerSample playerSample) {

                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty("max", playerSample.getPlayerCount());
                jsonobject.addProperty("online", playerSample.getPlayerCount_Max());

                if (playerSample.getProfiles() != null && playerSample.getProfiles().length > 0) {

                    JsonArray jsonarray = new JsonArray();

                    for (GameProfile profile : playerSample.getProfiles()) {

                        JsonObject profileObject = new JsonObject();
                        UUID uuid = profile.getId();

                        profileObject.addProperty("id", uuid == null ? "" : uuid.toString());
                        profileObject.addProperty("name", profile.getName());

                        jsonarray.add(profileObject);
                    }
                    jsonobject.add("sample", jsonarray);
                }
                return jsonobject;
            }

            public JsonElement serialize(ServerPing.ServerPingPlayerSample playerSample, Type type, JsonSerializationContext context) {
                return this.serialize_N(playerSample);
            }

            public ServerPing.ServerPingPlayerSample deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext context) throws JsonParseException {
                return this.deserialize_N(jsonelement);
            }
        }
    }
}
