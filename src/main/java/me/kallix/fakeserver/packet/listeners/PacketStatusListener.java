package me.kallix.fakeserver.packet.listeners;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import me.kallix.fakeserver.config.Config;
import me.kallix.fakeserver.network.NetworkManager;
import me.kallix.fakeserver.packet.packets.PacketStatusInPing;
import me.kallix.fakeserver.packet.packets.PacketStatusInStart;
import me.kallix.fakeserver.packet.packets.PacketStatusOutPong;
import me.kallix.fakeserver.packet.packets.PacketStatusOutServerInfo;
import me.kallix.fakeserver.utils.ServerPing;
import me.kallix.fakeserver.utils.chat.ChatComponentText;
import me.kallix.fakeserver.utils.chat.IChatBaseComponent;
import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PacketStatusListener implements PacketStatusInListener {

    private static final IChatBaseComponent handleMessage = new ChatComponentText("Status request has been handled.");
    private final NetworkManager networkManager;
    private boolean isStarted;

    public PacketStatusListener(NetworkManager networkmanager) {
        this.networkManager = networkmanager;
    }

    public void terminate(IChatBaseComponent ichatbasecomponent) {}

    public void a(PacketStatusInStart packetstatusinstart) {

        if (this.isStarted) {
            this.networkManager.close(PacketStatusListener.handleMessage);
            return;
        }

        this.isStarted = true;

        ServerPing.ServerPingPlayerSample playerSample = new ServerPing.ServerPingPlayerSample(Config.MAX_PLAYERS_COUNT, Config.PLAYERS_COUNT);

        playerSample.setProfiles(Config.PLAYER_LIST.toArray(new GameProfile[0]));

        ServerPing ping = new ServerPing();
        ping.setFavicon(getServerIcon());
        ping.setMOTD(new ChatComponentText("test"));
        ping.setPlayerSample(playerSample);
        ping.setServerInfo(new ServerPing.ServerData(Config.SERVER_INFO, 47)); // 47: protocol version

        this.networkManager.handle(new PacketStatusOutServerInfo(ping));
    }

    public void a(PacketStatusInPing packetstatusinping) {
        this.networkManager.handle(new PacketStatusOutPong(packetstatusinping.a()));
        this.networkManager.close(PacketStatusListener.handleMessage);
    }

    public String getServerIcon() {

        try {
            File imageFile = new File(new File("."), "server-icon.png");

            Validate.isTrue(imageFile.isFile() && imageFile.exists(), "Invalid server icon");

            BufferedImage image = ImageIO.read(new File(".", "server-icon.png"));
            ByteBuf bytebuf = Unpooled.buffer();

            Validate.isTrue(image.getWidth() == 64, "Must be 64 pixels wide");
            Validate.isTrue(image.getHeight() == 64, "Must be 64 pixels high");
            ImageIO.write(image, "PNG", new ByteBufOutputStream(bytebuf));

            ByteBuf base64 = Base64.encode(bytebuf);

            return "data:image/png;base64," + base64.toString(Charsets.UTF_8);
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("couldn't load server icon");
        }
        return null;
    }
}
