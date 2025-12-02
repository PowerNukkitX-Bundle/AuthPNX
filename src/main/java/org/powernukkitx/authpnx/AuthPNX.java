package org.powernukkitx.authpnx;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jline.utils.Log;
import org.powernukkitx.authpnx.commands.ChangePasswordCommand;
import org.powernukkitx.authpnx.commands.LoginMicrosoftCommand;
import org.powernukkitx.authpnx.listener.PlayerJoinListener;
import org.powernukkitx.authpnx.listener.PlayerLoginListener;
import org.powernukkitx.authpnx.listener.BaseEventListener;
import org.powernukkitx.authpnx.session.SessionManager;
import org.powernukkitx.authpnx.utils.PBKDF2;

import java.security.SecureRandom;

public class AuthPNX extends PluginBase {

    private static AuthPNX INSTANCE;
    @Getter
    private SessionManager sessionManager;

    @Override
    public void onEnable() {

        if(getServer().getSettings().baseSettings().xboxAuth()) {
            getLogger().critical("This server has xbox auth enabled. Therefore this plugin is not required.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        INSTANCE = this;

        super.onEnable();
        this.saveDefaultConfig();
        this.saveResource("messages.yml");

        Config config = getConfig();
        if(config.getString("pbkdf2.salt").equals("THIS_WILL_BE_GENERATED")) {
            config.set("pbkdf2.salt", PBKDF2.bytesToHex(new SecureRandom().generateSeed(16)));
            config.save();
        }

        this.sessionManager = new SessionManager();
        registerListeners();
        registerCommands();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this.sessionManager, this);
        pluginManager.registerEvents(new PlayerLoginListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new BaseEventListener(), this);
    }

    private void registerCommands() {
        CommandMap commandMap = getServer().getCommandMap();
        commandMap.register("changepassword", new ChangePasswordCommand());
        if(getConfig().getBoolean("allowXboxAuthRegister")) {
            commandMap.register("loginwithmicrosoft", new LoginMicrosoftCommand());
        }
    }

    public static AuthPNX get() {
        return INSTANCE;
    }
}
