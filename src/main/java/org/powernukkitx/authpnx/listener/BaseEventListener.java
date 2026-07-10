package org.powernukkitx.authpnx.listener;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.Listener;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityEvent;
import org.powernukkitx.event.inventory.InventoryEvent;
import org.powernukkitx.event.inventory.InventoryOpenEvent;
import org.powernukkitx.event.player.*;
import org.powernukkitx.authpnx.AuthPNX;
import org.powernukkitx.authpnx.session.AuthSession;

public class BaseEventListener implements Listener {

    @EventHandler
    public void on(PlayerMoveEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(InventoryOpenEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerTransferItemEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerMouseOverEntityEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerExperienceChangeEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerFoodLevelChangeEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerBlockPickEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void on(PlayerChatEvent event) {
        handleEvent(event);
    }

    private void handleEvent(Event event) {
        if(event instanceof Cancellable) {
            Player player = null;

            if(event instanceof PlayerEvent playerEvent) {
                player = playerEvent.getPlayer();
            } else if(event instanceof EntityEvent entityEvent) {
                if(entityEvent.getEntity() instanceof Player entity) {
                    player = entity;
                }
            } else if(event instanceof InventoryEvent inventoryEvent) {
                if(inventoryEvent.getInventory().getHolder() instanceof Player inventoryHolder) {
                    player = inventoryHolder;
                }
            }
            if(player != null) {
                AuthSession session = AuthPNX.get().getSessionManager().get(player);
                if(!session.isAuthenticated()) event.setCancelled();
            }
        }
    }

}
