package org.powernukkitx.authpnx.utils;

import cn.nukkit.utils.Config;
import org.powernukkitx.authpnx.AuthPNX;

import java.io.File;

public class Messages {

    public static final Messages INSTANCE = new Messages();

    private Config config;

    private Messages() {
        this.config = new Config(new File(AuthPNX.get().getDataFolder(), "messages.yml"));
    }

    public String get(String key) {
        return config.getString(key);
    }

}
