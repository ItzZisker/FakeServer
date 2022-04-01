package me.kallix.fakeserver.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.kallix.fakeserver.exceptions.CancelledPacketHandleException;
import me.kallix.fakeserver.network.protocol.EnumProtocol;
import me.kallix.fakeserver.network.protocol.EnumProtocolDirection;
import me.kallix.fakeserver.packet.Packet;
import me.kallix.fakeserver.packet.PacketListener;
import me.kallix.fakeserver.packet.compress.PacketCompressor;
import me.kallix.fakeserver.packet.compress.PacketDecompressor;
import me.kallix.fakeserver.utils.LazyInitVar;
import me.kallix.fakeserver.utils.chat.ChatComponentText;
import me.kallix.fakeserver.utils.chat.ChatMessage;
import me.kallix.fakeserver.utils.chat.IChatBaseComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings({"unused"})
public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final Marker NETWORK_MARKER = MarkerManager.getMarker("NETWORK");
    public static final Marker NETWORK_PACKETS_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.NETWORK_MARKER);
    public static final AttributeKey<EnumProtocol> protocol_attributeKey = AttributeKey.valueOf("protocol");
    public static final LazyInitVar<NioEventLoopGroup> NIO_EVENT_LOOP_GROUP;
    public static final LazyInitVar<EpollEventLoopGroup> EPOLL_EVENT_LOOP_GROUP;
    public static final LazyInitVar<LocalEventLoopGroup> LOCAL_EVENT_LOOP_GROUP;

    private final Queue<QueuedPacket> packetQueue = Queues.newConcurrentLinkedQueue();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public Channel channel;
    public SocketAddress socketAddress;
    public java.util.UUID spoofedUUID;
    public com.mojang.authlib.properties.Property[] spoofedProfile;
    public boolean isPreparing = true;

    @SuppressWarnings("FieldCanBeLocal")
    private final EnumProtocolDirection direction;
    private PacketListener packetListener;
    private IChatBaseComponent message;
    private boolean isDisconnected;

    static {

        NIO_EVENT_LOOP_GROUP = new LazyInitVar<NioEventLoopGroup>() {
            public NioEventLoopGroup get() {
                return new NioEventLoopGroup(0, new ThreadFactoryBuilder()
                        .setNameFormat("Netty Client IO #%d")
                        .setDaemon(true)
                        .build());
            }
            protected NioEventLoopGroup init() {
                return get();
            }
        };

        EPOLL_EVENT_LOOP_GROUP = new LazyInitVar<EpollEventLoopGroup>() {
            public EpollEventLoopGroup get() {
                return new EpollEventLoopGroup(0, new ThreadFactoryBuilder()
                        .setNameFormat("Netty Epoll Client IO #%d")
                        .setDaemon(true)
                        .build());
            }
            protected EpollEventLoopGroup init() {
                return get();
            }
        };

        LOCAL_EVENT_LOOP_GROUP = new LazyInitVar<LocalEventLoopGroup>() {
            public LocalEventLoopGroup get() {
                return new LocalEventLoopGroup(0, new ThreadFactoryBuilder()
                        .setNameFormat("Netty Local Client IO #%d")
                        .setDaemon(true)
                        .build());
            }
            protected LocalEventLoopGroup init() {
                return get();
            }
        };
    }

    public NetworkManager(EnumProtocolDirection direction) {
        this.direction = direction;
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);

        channel = channelhandlercontext.channel();
        socketAddress = channel.remoteAddress();
        isPreparing = false;

        try {
            autoRead(EnumProtocol.HANDSHAKING);
        } catch (Throwable throwable) {
            LOGGER.fatal(throwable);
        }
    }

    public void autoRead(EnumProtocol enumprotocol) {

        channel.attr(NetworkManager.protocol_attributeKey).set(enumprotocol);
        channel.config().setAutoRead(true);

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) {
        close(new ChatMessage("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {

        ChatMessage message;

        if (throwable instanceof TimeoutException) {
            message = new ChatMessage("disconnect.timeout");
        } else {
            message = new ChatMessage("disconnect.genericReason", "Internal Exception: " + throwable);
        }
        close(message);
        if (LOGGER.isDebugEnabled()) throwable.printStackTrace();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void autoRead(ChannelHandlerContext context, Packet packet) {
        if (channel.isOpen()) {
            try {
                packet.a(packetListener);
            } catch (CancelledPacketHandleException ignored) {
            }
        }
    }

    public void autoRead(PacketListener packetlistener) {

        Validate.notNull(packetlistener, "packetListener is null");

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Trying to set listener of " + this + " to " + packetlistener + "...");
        packetListener = packetlistener;
    }

    public void handle(Packet<?> packet) {

        if (isChannelOpened()) {
            queue();
            autoRead(packet, null);
        } else {
            readWriteLock.writeLock().lock();
            try {
                packetQueue.add(new NetworkManager.QueuedPacket(packet, (GenericFutureListener<? extends Future<? super Void>>) null));
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }
    }

    @SafeVarargs
    public final void autoRead(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> futureListener, GenericFutureListener<? extends Future<? super Void>>... futureListeners) {
        if (isChannelOpened()) {
            queue();
            autoRead(packet, ArrayUtils.insert(0, futureListeners, futureListener));
        } else {
            readWriteLock.writeLock().lock();

            try {
                packetQueue.add(new NetworkManager.QueuedPacket(packet, ArrayUtils.insert(0, futureListeners, futureListener)));
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }
    }

    private void autoRead(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>[] futureListener) {

        EnumProtocol packet_protocol = EnumProtocol.getId(packet);
        EnumProtocol attribute_protocol = channel.attr(NetworkManager.protocol_attributeKey).get();

        if (attribute_protocol != packet_protocol) {
            LOGGER.debug("Disabled auto read");
            channel.config().setAutoRead(false);
        }

        if (channel.eventLoop().inEventLoop()) {

            if (packet_protocol != attribute_protocol) {
                autoRead(packet_protocol);
            }

            ChannelFuture channelfuture = channel.writeAndFlush(packet);

            if (futureListener != null) {
                channelfuture.addListeners(futureListener);
            }
            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            channel.eventLoop().execute(() -> {

                if (packet_protocol != attribute_protocol) {
                    autoRead(packet_protocol);
                }

                ChannelFuture channelfuture = channel.writeAndFlush(packet);

                if (futureListener != null) {
                    channelfuture.addListeners(futureListener);
                }
                channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    private void queue() {

        if (channel != null && channel.isOpen()) {

            readWriteLock.readLock().lock();

            try {
                while (!packetQueue.isEmpty()) {

                    NetworkManager.QueuedPacket queuedPacket = packetQueue.poll();

                    autoRead(queuedPacket.packet, queuedPacket.futureListeners);
                }
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
    }

    public void autoRead() {
        queue();
        channel.flush();
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void close(IChatBaseComponent message) {

        isPreparing = false;

        if (channel.isOpen()) {
            channel.close();
            this.message = message;
        }
    }

    public boolean isChannelLocal() {
        return channel instanceof LocalChannel || channel instanceof LocalServerChannel;
    }

    public boolean isChannelOpened() {
        return channel != null && channel.isOpen();
    }

    public boolean isChannelClosed() {
        return channel == null;
    }

    public PacketListener getPacketListener() {
        return packetListener;
    }

    public IChatBaseComponent getMessage() {
        return message;
    }

    public void setManualRead() {
        channel.config().setAutoRead(false);
    }

    public void autoRead(int i) {

        if (i >= 0) {

            if (channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor) channel.pipeline().get("decompress")).a(i);
            } else {
                channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(i));
            }

            if (channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor) channel.pipeline().get("decompress")).a(i);
            } else {
                channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(i));
            }
        } else {

            if (channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                channel.pipeline().remove("decompress");
            }

            if (channel.pipeline().get("compress") instanceof PacketCompressor) {
                channel.pipeline().remove("compress");
            }
        }
    }

    public void handleDisconnection() {

        if (channel != null && !channel.isOpen()) {
            if (!isDisconnected) {

                isDisconnected = true;

                if (getMessage() != null) {
                    getPacketListener().terminate(getMessage());
                } else if (getPacketListener() != null) {
                    getPacketListener().terminate(new ChatComponentText("Disconnected"));
                }
                packetQueue.clear();
            } else {
                LOGGER.warn("handleDisconnection() called twice");
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext context, Packet packet) {
        autoRead(context, packet);
    }

    static class QueuedPacket {

        private final Packet<?> packet;
        private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

        @SafeVarargs
        public QueuedPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>... futureListeners) {
            this.packet = packet;
            this.futureListeners = futureListeners;
        }
    }

    public SocketAddress getRawAddress() {
        return channel.remoteAddress();
    }
}
