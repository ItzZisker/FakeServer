package me.kallix.fakeserver.network;

import me.kallix.fakeserver.network.protocol.EnumProtocol;
import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.PacketDataSerializer;

import java.io.IOException;

public class PacketHandshakingInSetProtocol implements Packet<PacketHandshakingInListener> {

    private int a;
    public String hostname;
    public int port;
    private EnumProtocol d;

    public PacketHandshakingInSetProtocol() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.hostname = packetdataserializer.c(Short.MAX_VALUE); // Spigot
        this.port = packetdataserializer.readUnsignedShort();
        this.d = EnumProtocol.getId(packetdataserializer.e());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.a(this.hostname);
        packetdataserializer.writeShort(this.port);
        packetdataserializer.b(this.d.getId());
    }

    public void a(PacketHandshakingInListener packethandshakinginlistener) {
        packethandshakinginlistener.a(this);
    }

    public EnumProtocol a() {
        return this.d;
    }

    public int b() {
        return this.a;
    }
}



