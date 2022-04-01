package me.kallix.fakeserver.connection.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.kallix.fakeserver.packet.PacketDataSerializer;

public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    protected void encode(ChannelHandlerContext context, ByteBuf bufferIn, ByteBuf bufferOut)  {

        int var4 = bufferIn.readableBytes();
        int var5 = PacketDataSerializer.a(var4);

        if (var5 > 3) {
            throw new IllegalArgumentException("unable to fit " + var4 + " into " + 3);
        } else {
            PacketDataSerializer var6 = new PacketDataSerializer(bufferOut);
            var6.ensureWritable(var5 + var4);
            var6.b(var4);
            var6.writeBytes(bufferIn, bufferIn.readerIndex(), var4);
        }
    }
}
