package me.kallix.fakeserver.packet;

import me.kallix.fakeserver.utils.chat.IChatBaseComponent;

public interface PacketListener {
    void terminate(IChatBaseComponent message);
}
