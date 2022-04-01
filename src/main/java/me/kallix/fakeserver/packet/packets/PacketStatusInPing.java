package me.kallix.fakeserver.packet.packets;

import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.PacketDataSerializer;
import me.kallix.fakeserver.packet.listeners.PacketStatusInListener;

import java.io.IOException;

public class PacketStatusInPing implements Packet<PacketStatusInListener> {

    private long a;

    public PacketStatusInPing() {
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readLong();
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeLong(this.a);
    }

    public void a(PacketStatusInListener var1) {
        var1.a(this);
    }

    public long a() {
        return this.a;
    }
}
