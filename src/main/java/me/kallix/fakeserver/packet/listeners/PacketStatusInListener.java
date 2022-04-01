package me.kallix.fakeserver.packet.listeners;

import me.kallix.fakeserver.packet.PacketListener;
import me.kallix.fakeserver.packet.packets.PacketStatusInPing;
import me.kallix.fakeserver.packet.packets.PacketStatusInStart;

public interface PacketStatusInListener extends PacketListener {

    void a(PacketStatusInPing var1);

    void a(PacketStatusInStart var1);
}
