package me.kallix.fakeserver.packet.packets;

import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.PacketDataSerializer;
import me.kallix.fakeserver.packet.listeners.PacketStatusInListener;

import java.io.IOException;

public class PacketStatusInStart implements Packet<PacketStatusInListener> {

    public PacketStatusInStart() {
    }

    public void a(PacketDataSerializer var1) throws IOException {
    }

    public void b(PacketDataSerializer var1) throws IOException {
    }

    public void a(PacketStatusInListener var1) {
        var1.a(this);
    }
}
