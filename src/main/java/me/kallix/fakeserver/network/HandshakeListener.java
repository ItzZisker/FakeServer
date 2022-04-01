package me.kallix.fakeserver.network;

import me.kallix.fakeserver.packet.listeners.PacketStatusListener;
import me.kallix.fakeserver.utils.chat.IChatBaseComponent;
import me.kallix.fakeserver.network.protocol.EnumProtocol;

import java.net.InetAddress;
import java.util.HashMap;

public class HandshakeListener implements PacketHandshakingInListener {

    private static final com.google.gson.Gson gson = new com.google.gson.Gson();
    private static final HashMap<InetAddress, Long> throttleTracker = new HashMap<>();

    private final NetworkManager networkManager;

    public HandshakeListener(NetworkManager networkmanager) {
        this.networkManager = networkmanager;
    }

    public void a(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
        this.networkManager.autoRead(EnumProtocol.STATUS);
        this.networkManager.autoRead(new PacketStatusListener(this.networkManager));
    }

    @Override
    public void terminate(IChatBaseComponent message) {
    }
}
