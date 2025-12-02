package org.powernukkitx.authpnx.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import org.powernukkitx.authpnx.AuthPNX;
import org.powernukkitx.authpnx.session.AuthSession;
import org.powernukkitx.authpnx.utils.Messages;

public class PlayerLoginListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if(!event.isCancelled()) {
            Player player = event.getPlayer();
            AuthSession session = AuthPNX.get().getSessionManager().get(player);
            if(session.getType() == AuthSession.AuthType.XBOX) {
                if(player.getLoginChainData().isXboxAuthed()) {
                    session.setAuthenticated();
                } else {
                    event.setKickMessage(Messages.INSTANCE.get("failed_xbox_account"));
                    event.setCancelled(true);
                }
            }
        }
    }

}
