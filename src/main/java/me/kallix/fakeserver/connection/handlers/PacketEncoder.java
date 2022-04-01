package me.kallix.fakeserver.connection.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.kallix.fakeserver.network.NetworkManager;
import me.kallix.fakeserver.network.protocol.EnumProtocolDirection;
import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.PacketDataSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {

    private static final Logger a = LogManager.getLogger();
    private static final Marker b;
    private final EnumProtocolDirection c;

    static {
        b = MarkerManager.getMarker("PACKET_SENT", NetworkManager.NETWORK_PACKETS_MARKER);
    }

    public PacketEncoder(EnumProtocolDirection var1) {
        this.c = var1;
    }

    protected void encode(ChannelHandlerContext context, Packet packet, ByteBuf buffer) throws Exception {

        Integer var4 = context.channel().attr(NetworkManager.protocol_attributeKey).get().getId(this.c, packet);

        if (a.isDebugEnabled()) {
            a.debug(b, "OUT: [{}:{}] {}", context.channel().attr(NetworkManager.protocol_attributeKey).get(), var4, packet.getClass().getName());
        }

        if (var4 == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {

            PacketDataSerializer var5 = new PacketDataSerializer(buffer);
            var5.b(var4);

            try {
               /* if (packet instanceof PacketPlayOutNamedEntitySpawn) {
                    packet = packet;
                }*/

                packet.b(var5);
            } catch (Throwable var7) {
                a.error(var7);
            }

        }
    }
}
