package org.powernukkitx.authpnx.commands;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.PluginCommand;
import de.buddelbubi.database.DBClient;
import org.powernukkitx.authpnx.AuthPNX;
import org.powernukkitx.authpnx.session.AuthSession;
import org.powernukkitx.authpnx.utils.Messages;

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
            if(player.getPlayerInfo().isXboxAuth()) {
                DBClient client = AuthPNX.get().getSessionManager().getDatabase();
                client.executeUpdate("UPDATE players SET password = '', type = 2 WHERE username = ?", player.getName().toLowerCase());
                session.load();
                player.sendMessage(Messages.INSTANCE.get("changed.type"));
            } else player.sendMessage("§c%disconnectionScreen.notAuthenticated");
        } else sender.sendMessage("This command can only be executed by a player.");
        return true;
    }
}
