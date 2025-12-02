package org.powernukkitx.authpnx.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.form.element.ElementDivider;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.custom.ElementInput;
import cn.nukkit.form.window.CustomForm;
import cn.nukkit.level.Sound;
import org.powernukkitx.authpnx.AuthPNX;
import org.powernukkitx.authpnx.session.AuthSession;
import org.powernukkitx.authpnx.utils.Messages;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerJoinListener implements Listener {

    public final static int PASSWORD_LENGTH = AuthPNX.get().getConfig().getInt("minPasswordLength", 16);
    private static final int MAX_TRIES = AuthPNX.get().getConfig().getInt("maxLoginTries");


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        AuthSession session = AuthPNX.get().getSessionManager().get(player);
        AtomicInteger attempts = new AtomicInteger();
        if(!session.isAuthenticated()) {
            if(session.getType() == AuthSession.AuthType.UNREGISTERED) {
                CustomForm form = new CustomForm(Messages.INSTANCE.get("register.form.title"));
                form.addElement(new ElementLabel(Messages.INSTANCE.get("register.form.description")));
                form.addElement(new ElementDivider());
                form.addElement(new ElementInput(Messages.INSTANCE.get("password")));
                form.addElement(new ElementInput(Messages.INSTANCE.get("register.form.confirm_password")));
                form.onClose(player1 -> {
                   if(!session.isAuthenticated()) {
                       form.viewers().clear();
                       form.send(player);
                   }
                });
                form.onSubmit((player1, response) -> {
                    String password = response.getInputResponse(2);
                    if(password.length() >= PASSWORD_LENGTH) {
                        String passwordConfirm = response.getInputResponse(3);
                        if(password.equals(passwordConfirm)) {
                            session.setPassword(password);
                            player.sendMessage(Messages.INSTANCE.get("register.success"));
                            player.level.addSound(player, Sound.RANDOM_LEVELUP, 1, 1,  player);
                        } else {
                            if(form.elements().size() > 4) form.elements().removeLast();
                            form.addElement(new ElementLabel(Messages.INSTANCE.get("register.password.not_match")));
                            form.viewers().clear();
                            form.send(player);
                        }
                    } else {
                        if(form.elements().size() > 4) form.elements().removeLast();
                        form.addElement(new ElementLabel(String.format(Messages.INSTANCE.get("register.password.length"), PASSWORD_LENGTH)));
                        form.viewers().clear();
                        form.send(player);
                    }
                });
                form.send(player);
            } else if(session.getType() == AuthSession.AuthType.PASSWORD) {
                CustomForm form = new CustomForm(Messages.INSTANCE.get("login.form.title"));
                form.addElement(new ElementLabel(Messages.INSTANCE.get("login.form.description")));
                form.addElement(new ElementDivider());
                form.addElement(new ElementInput(Messages.INSTANCE.get("password")));
                form.onClose((player1) -> {
                    if(!session.isAuthenticated()) {
                        form.viewers().clear();
                        form.send(player);
                    }
                });
                form.onSubmit((player1, response) -> {
                    String password = response.getInputResponse(2);
                    if(session.doesPasswordMatch(password)) {
                        session.setAuthenticated();
                        player.sendMessage(Messages.INSTANCE.get("login.success"));
                        player.level.addSound(player, Sound.RANDOM_LEVELUP, 1, 1,  player);
                    } else {

                        attempts.getAndIncrement();
                        if(attempts.get() >= MAX_TRIES) {
                            player.kick(Messages.INSTANCE.get("login.attempts"), false);
                        }
                        if(form.elements().size() == 3) {
                            form.addElement(new ElementLabel(Messages.INSTANCE.get("login.wrong")));
                        }
                        player.level.addSound(player, Sound.NOTE_BASS, 1, 1,  player);
                        form.viewers().clear();
                        form.send(player);
                    }
                });
                form.send(player);
            }
        }
    }

}
