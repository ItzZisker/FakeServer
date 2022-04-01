package me.kallix.fakeserver.connection;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import me.kallix.fakeserver.connection.handlers.*;
import me.kallix.fakeserver.network.HandshakeListener;
import me.kallix.fakeserver.network.NetworkManager;
import me.kallix.fakeserver.network.protocol.EnumProtocolDirection;
import me.kallix.fakeserver.utils.LazyInitVar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class ServerConnection {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final LazyInitVar<NioEventLoopGroup> nioEventLoopGroup = new LazyInitVar<NioEventLoopGroup>() {
        private NioEventLoopGroup a() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
        }
        protected NioEventLoopGroup init() {
            return this.a();
        }
    };

    public static final LazyInitVar<EpollEventLoopGroup> epollEventLoopGroup = new LazyInitVar<EpollEventLoopGroup>() {
        private EpollEventLoopGroup a() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
        }
        protected EpollEventLoopGroup init() {
            return this.a();
        }
    };

    public static final LazyInitVar<LocalEventLoopGroup> localEventLoopGroup = new LazyInitVar<LocalEventLoopGroup>() {
        private LocalEventLoopGroup a() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
        }
        protected LocalEventLoopGroup init() {
            return this.a();
        }
    };

    public volatile boolean isOpen;
    private final List<ChannelFuture> channelFutureList = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

    public ServerConnection() {
        this.isOpen = true;
    }

    public void bind(InetAddress inetaddress, int port) {

        synchronized (this.channelFutureList) {

            Class<? extends ServerSocketChannel> serverSocketClass;
            LazyInitVar<? extends EventLoopGroup> eventLoopGroup;

            if (Epoll.isAvailable()) {
                serverSocketClass = EpollServerSocketChannel.class;
                eventLoopGroup = ServerConnection.epollEventLoopGroup;
                ServerConnection.LOGGER.info("Using epoll channel type");
            } else {
                serverSocketClass = NioServerSocketChannel.class;
                eventLoopGroup = ServerConnection.nioEventLoopGroup;
                ServerConnection.LOGGER.info("Using default channel type");
            }

            this.channelFutureList.add((new ServerBootstrap()).channel(serverSocketClass).childHandler(new ChannelInitializer<Channel>() {
                protected void initChannel(Channel channel) {

                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException ignored) {
                    }

                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30))
                            .addLast("legacy_query", new LegacyPingHandler())
                            .addLast("splitter", new PacketSplitter())
                            .addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND))
                            .addLast("prepender", new PacketPrepender())
                            .addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));

                    NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);

                    ServerConnection.this.networkManagers.add(networkmanager);
                    channel.pipeline().addLast("packet_handler", networkmanager);
                    networkmanager.autoRead(new HandshakeListener(networkmanager));
                }
            }).group(eventLoopGroup.get()).localAddress(inetaddress, port).bind().syncUninterruptibly());
        }
    }

    public void close() {

        this.isOpen = false;

        for (ChannelFuture channelfuture : this.channelFutureList) {
            try {
                channelfuture.channel().close().sync();
            } catch (InterruptedException interruptedexception) {
                ServerConnection.LOGGER.error("Interrupted whilst closing channel");
            }
        }
    }
}
