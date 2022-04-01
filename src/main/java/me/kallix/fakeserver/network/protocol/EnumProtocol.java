package me.kallix.fakeserver.network.protocol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.kallix.fakeserver.network.PacketHandshakingInSetProtocol;
import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.packets.PacketStatusInPing;
import me.kallix.fakeserver.packet.packets.PacketStatusInStart;
import me.kallix.fakeserver.packet.packets.PacketStatusOutPong;
import me.kallix.fakeserver.packet.packets.PacketStatusOutServerInfo;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum EnumProtocol {

    HANDSHAKING(-1) {
        {
            getId(EnumProtocolDirection.SERVERBOUND, PacketHandshakingInSetProtocol.class);
        }
    },
    STATUS(1) {
        {
            getId(EnumProtocolDirection.SERVERBOUND, PacketStatusInStart.class);
            getId(EnumProtocolDirection.CLIENTBOUND, PacketStatusOutServerInfo.class);
            getId(EnumProtocolDirection.SERVERBOUND, PacketStatusInPing.class);
            getId(EnumProtocolDirection.CLIENTBOUND, PacketStatusOutPong.class);
        }
    };

    private static final int U1 = -1;
    private static final int U2 = 2;
    private static final EnumProtocol[] PROTOCOLS = new EnumProtocol[U2 - U1 + 1];
    private static final Map<Class<? extends Packet<?>>, EnumProtocol> PACKET_PROTOCOLS = new HashMap<>();

    private final Map<EnumProtocolDirection, BiMap<Integer, Class<? extends Packet<?>>>> packetDirections;
    private final int id;

    static {

        for (EnumProtocol eachProtocol : values()) {

            int id = eachProtocol.getId();

            if (id < U1 || id > U2) {
                throw new Error("Invalid protocol ID " + id);
            }

            PROTOCOLS[id - U1] = eachProtocol;

            for (EnumProtocolDirection eachDirection : eachProtocol.packetDirections.keySet()) {

                Class<? extends Packet<?>> packetClazz;

                for (Iterator<Class<? extends Packet<?>>> eachPacketClazz = eachProtocol.packetDirections.get(eachDirection).values().iterator();
                     eachPacketClazz.hasNext();
                     PACKET_PROTOCOLS.put(packetClazz, eachProtocol)) {

                    packetClazz = eachPacketClazz.next();

                    if (PACKET_PROTOCOLS.containsKey(packetClazz) && PACKET_PROTOCOLS.get(packetClazz) != eachProtocol) {
                        throw new Error("Packet " + packetClazz + " is already assigned to protocol " + PACKET_PROTOCOLS.get(packetClazz) + " - can't reassign to " + eachProtocol);
                    }

                    try {
                        packetClazz.newInstance();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        throw new Error("Packet " + packetClazz + " fails instantiation checks! " + packetClazz);
                    }
                }
            }
        }
    }

    public static EnumProtocol getId(int id) {
        return id >= U1 && id <= U2 ? PROTOCOLS[id - U1] : null;
    }

    public static EnumProtocol getId(Packet<?> packet) {
        return PACKET_PROTOCOLS.get(packet.getClass());
    }

    EnumProtocol(int id) {
        this.packetDirections = new EnumMap<>(EnumProtocolDirection.class);
        this.id = id;
    }

    protected EnumProtocol getId(EnumProtocolDirection direction, Class<? extends Packet<?>> packetClazz) {

        BiMap<Integer, Class<? extends Packet<?>>> packetIds = packetDirections.computeIfAbsent(direction, k -> HashBiMap.create());

        if (packetIds.containsValue(packetClazz)) {
            throw new IllegalArgumentException(direction + " packet " + packetClazz + " is already known to ID " + packetIds.inverse().get(packetClazz));
        } else {
            packetIds.put(packetIds.size(), packetClazz);
            return this;
        }
    }

    public Integer getId(EnumProtocolDirection direction, Packet<?> packet) {
        return packetDirections.get(direction).inverse().get(packet.getClass());
    }

    public Packet<?> getId(EnumProtocolDirection direction, int id) throws IllegalAccessException, InstantiationException {

        Class<? extends Packet<?>> packetClazz = packetDirections.get(direction).get(id);

        return packetClazz == null ? null : packetClazz.newInstance();
    }

    public int getId() {
        return id;
    }
}
