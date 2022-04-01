package me.kallix.fakeserver.packet.packets;

import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.PacketDataSerializer;
import me.kallix.fakeserver.packet.listeners.PacketStatusOutListener;

import java.io.IOException;

public class PacketStatusOutPong implements Packet<PacketStatusOutListener> {

    private long a;

    public PacketStatusOutPong() {
    }

    public PacketStatusOutPong(long var1) {
        this.a = var1;
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readLong();
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeLong(this.a);
    }

    public void a(PacketStatusOutListener var1) {
        var1.a(this);
    }
}
