package me.kallix.fakeserver.packet;

import java.io.IOException;

public interface Packet<E extends PacketListener> {

    void a(PacketDataSerializer var1) throws IOException;

    void b(PacketDataSerializer var1) throws IOException;

    void a(E var1);
}
