package me.kallix.fakeserver.packet.packets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.kallix.fakeserver.utils.chat.ChatModifier;
import me.kallix.fakeserver.utils.chat.ChatTypeAdapterFactory;
import me.kallix.fakeserver.utils.chat.IChatBaseComponent;
import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.PacketDataSerializer;
import me.kallix.fakeserver.packet.listeners.PacketStatusOutListener;
import me.kallix.fakeserver.utils.ServerPing;
import me.kallix.fakeserver.utils.serializer.chat.ChatSerializer;

import java.io.IOException;

public class PacketStatusOutServerInfo implements Packet<PacketStatusOutListener> {

    private static final Gson a = (new GsonBuilder())
            .registerTypeAdapter(ServerPing.ServerData.class, new ServerPing.ServerData.Serializer())
            .registerTypeAdapter(ServerPing.ServerPingPlayerSample.class, new ServerPing.ServerPingPlayerSample.Serializer())
            .registerTypeAdapter(ServerPing.class, new ServerPing.Serializer())
            .registerTypeHierarchyAdapter(IChatBaseComponent.class, new ChatSerializer())
            .registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer())
            .registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();

    private ServerPing b;

    public PacketStatusOutServerInfo() {
    }

    public PacketStatusOutServerInfo(ServerPing var1) {
        this.b = var1;
        System.out.println("called packet");
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.b = a.fromJson(var1.c(32767), ServerPing.class);
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.a(a.toJson(this.b));
    }

    public void a(PacketStatusOutListener var1) {
        var1.a(this);
    }
}
