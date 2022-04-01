package me.kallix.fakeserver.packet.listeners;

import me.kallix.fakeserver.packet.PacketListener;
import me.kallix.fakeserver.packet.packets.PacketStatusOutPong;
import me.kallix.fakeserver.packet.packets.PacketStatusOutServerInfo;

public interface PacketStatusOutListener extends PacketListener {

    void a(PacketStatusOutServerInfo var1);

    void a(PacketStatusOutPong var1);
}
