package org.powernukkitx.authpnx.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import org.powernukkitx.authpnx.AuthPNX;
import org.powernukkitx.authpnx.session.AuthSession;
import org.powernukkitx.authpnx.utils.Messages;

import java.util.Map;

import static org.powernukkitx.authpnx.listener.PlayerJoinListener.PASSWORD_LENGTH;

public class ChangePasswordCommand extends PluginCommand<AuthPNX> {

    public ChangePasswordCommand() {
        super("changepassword", AuthPNX.get());
        this.setDescription("Changes your password.");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("password", CommandParamType.STRING),
                CommandParameter.newType("confirm_password", CommandParamType.STRING)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        if(sender instanceof Player player) {
            AuthSession session = AuthPNX.get().getSessionManager().get(player);
            if(!session.isAuthenticated()) return 0;
            String password = list.getResult(0);
            if(password.length() >= PASSWORD_LENGTH) {
                String passwordConfirm = list.getResult(1);
                if(password.equals(passwordConfirm)) {
                    session.setPassword(password);
                    log.addSuccess(Messages.INSTANCE.get("changed.password"));
                } else {
                    log.addError(Messages.INSTANCE.get("register.password.not_match"));
                }
            } else {
                log.addError(String.format(Messages.INSTANCE.get("register.password.length"), PASSWORD_LENGTH));
            }
        } else log.addError("This command can only be executed by a player.");
        log.output();
        return 1;
    }
}
