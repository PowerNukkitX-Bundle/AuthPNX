package org.powernukkitx.authpnx.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.structure.Structure;
import de.buddelbubi.database.DBClient;
import org.powernukkitx.authpnx.AuthPNX;
import org.powernukkitx.authpnx.session.AuthSession;
import org.powernukkitx.authpnx.utils.Messages;

import java.util.Map;

import static org.powernukkitx.authpnx.listener.PlayerJoinListener.PASSWORD_LENGTH;

public class LoginMicrosoftCommand extends PluginCommand<AuthPNX> {

    public LoginMicrosoftCommand() {
        super("loginwithmicrosoft", AuthPNX.get());
        this.setDescription("Changes your authentication method to microsoft account.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(sender instanceof Player player) {
            AuthSession session = AuthPNX.get().getSessionManager().get(player);
            if(!session.isAuthenticated()) return false;
            if(player.getLoginChainData().isXboxAuthed()) {
                DBClient client = AuthPNX.get().getSessionManager().getDatabase();
                client.executeUpdate("UPDATE players SET password = '', type = 2 WHERE username = ?", player.getName().toLowerCase());
                session.load();
                player.sendMessage(Messages.INSTANCE.get("changed.type"));
            } else player.sendMessage("§c%disconnectionScreen.notAuthenticated");
        } else sender.sendMessage("This command can only be executed by a player.");
        return true;
    }
}
