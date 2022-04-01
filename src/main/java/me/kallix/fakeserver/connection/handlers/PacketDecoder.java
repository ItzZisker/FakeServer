package me.kallix.fakeserver.connection.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.kallix.fakeserver.network.NetworkManager;
import me.kallix.fakeserver.network.protocol.EnumProtocolDirection;
import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.PacketDataSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger a = LogManager.getLogger();
    private static final Marker b;
    private final EnumProtocolDirection c;

    static {
        b = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.NETWORK_PACKETS_MARKER);
    }

    public PacketDecoder(EnumProtocolDirection var1) {
        this.c = var1;
    }

    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> packets) throws Exception {
        if (buffer.readableBytes() != 0) {

            PacketDataSerializer var4 = new PacketDataSerializer(buffer);
            int var5 = var4.e();
            Packet<?> var6 = context.channel().attr(NetworkManager.protocol_attributeKey).get().getId(this.c, var5);

            if (var6 == null) {
                throw new IOException("Bad packet id " + var5);
            } else {
                var6.a(var4);
                if (var4.readableBytes() > 0) {
                    throw new IOException("Packet " + context.channel().attr(NetworkManager.protocol_attributeKey).get().getId() + "/" + var5 + " (" + var6.getClass().getSimpleName() + ") was larger than I expected, found " + var4.readableBytes() + " bytes extra whilst reading packet " + var5);
                } else {
                    packets.add(var6);
                    if (a.isDebugEnabled()) {
                        a.debug(b, " IN: [{}:{}] {}", context.channel().attr(NetworkManager.protocol_attributeKey).get(), var5, var6.getClass().getName());
                    }

                }
            }
        }
    }
}