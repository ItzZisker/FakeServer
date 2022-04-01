package me.kallix.fakeserver.config;

import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Config {

    public final static int MAX_PLAYERS_COUNT = 69;
    public final static int PLAYERS_COUNT = 2;
    public final static List<GameProfile> PLAYER_LIST = new ArrayList<>();
    public final static String VERSION = "1.0";
    public final static String SERVER_INFO = VERSION + " test";
    public final static String MOTD_TEXT = "motd test";

    static {
        PLAYER_LIST.add(new GameProfile(UUID.randomUUID(), "kos"));
        PLAYER_LIST.add(new GameProfile(UUID.randomUUID(), "kir"));
    }

}
