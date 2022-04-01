package me.kallix.fakeserver.connection.handlers;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.kallix.fakeserver.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class LegacyPingHandler extends ChannelInboundHandlerAdapter {

    private static final Logger a = LogManager.getLogger();

    public void channelRead(ChannelHandlerContext context, Object packet) {

        ByteBuf var3 = (ByteBuf) packet;
        var3.markReaderIndex();
        boolean var4 = true;

        try {
            try {
                if (var3.readUnsignedByte() != 254) {
                    return;
                }

                InetSocketAddress socketAddress = (InetSocketAddress) context.channel().remoteAddress();
                int var7 = var3.readableBytes();
                String var8;

                switch (var7) {
                    case 0:
                        a.debug("Ping: (<1.3.x) from {}:{}", socketAddress.getAddress(), socketAddress.getPort());
                        var8 = String.format("%s§%d§%d", Config.MOTD_TEXT, Config.MAX_PLAYERS_COUNT, Config.PLAYERS_COUNT);
                        this.a(context, this.a(var8));
                        break;
                    case 1:
                        if (var3.readUnsignedByte() != 1) {
                            return;
                        }

                        a.debug("Ping: (1.4-1.5.x) from {}:{}", socketAddress.getAddress(), socketAddress.getPort());
                        var8 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, Config.VERSION, Config.MOTD_TEXT, Config.MAX_PLAYERS_COUNT, Config.PLAYERS_COUNT);
                        this.a(context, this.a(var8));
                        break;
                    default:
                        boolean var23 = var3.readUnsignedByte() == 1;
                        var23 &= var3.readUnsignedByte() == 250;
                        var23 &= "MC|PingHost".equals(new String(var3.readBytes(var3.readShort() * 2).array(), Charsets.UTF_16BE));
                        int var9 = var3.readUnsignedShort();
                        var23 &= var3.readUnsignedByte() >= 73;
                        var23 &= 3 + var3.readBytes(var3.readShort() * 2).array().length + 4 == var9;
                        var23 &= var3.readInt() <= 65535;
                        var23 &= var3.readableBytes() == 0;
                        if (!var23) {
                            return;
                        }

                        a.debug("Ping: (1.6) from {}:{}", socketAddress.getAddress(), socketAddress.getPort());
                        String var10 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, Config.VERSION, Config.MOTD_TEXT, Config.MAX_PLAYERS_COUNT, Config.PLAYERS_COUNT);
                        ByteBuf var11 = this.a(var10);

                        try {
                            this.a(context, var11);
                        } finally {
                            var11.release();
                        }
                }

                var3.release();
                var4 = false;
            } catch (RuntimeException ignored) {
            }

        } finally {
            if (var4) {
                var3.resetReaderIndex();
                context.channel().pipeline().remove("legacy_query");
                context.fireChannelRead(packet);
            }
        }
    }

    private void a(ChannelHandlerContext var1, ByteBuf var2) {
        var1.pipeline().firstContext().writeAndFlush(var2).addListener(ChannelFutureListener.CLOSE);
    }

    private ByteBuf a(String var1) {

        ByteBuf var2 = Unpooled.buffer();
        var2.writeByte(255);
        char[] var3 = var1.toCharArray();
        var2.writeShort(var3.length);

        for (char var7 : var3) {
            var2.writeChar(var7);
        }

        return var2;
    }
}
