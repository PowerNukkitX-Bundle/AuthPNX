package org.powernukkitx.authpnx.session;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import de.buddelbubi.database.DBClient;
import de.buddelbubi.database.ResultSetList;
import lombok.Getter;
import org.powernukkitx.authpnx.AuthPNX;
import org.powernukkitx.authpnx.utils.PBKDF2;


@Getter
public class AuthSession {


    private final Player player;
    private AuthType type;

    private boolean authenticated;

    private static final PBKDF2 encoder;

    static {
        Config config = AuthPNX.get().getConfig();
        int iterations = config.getInt("pbkdf2.iterations", 10000);
        int keyLength = config.getInt("pbkdf2.keyLength", 256);
        encoder = new PBKDF2(iterations, keyLength);
    }

    protected AuthSession(Player player) {
        this.player = player;
        this.load();
    }

    public void load() {
        String username = player.getName().toLowerCase();
        DBClient client = AuthPNX.get().getSessionManager().getDatabase();
        ResultSetList playerData = client.executeQuery("SELECT type FROM players WHERE username = ?", username);
        if(playerData.next()) {
            this.type = AuthType.values()[playerData.getInt("type")];
        } else {
            boolean xboxAuth = player.getLoginChainData().isXboxAuthed() && AuthPNX.get().getConfig().getBoolean("allowXboxAuthRegister", true);
            this.type = xboxAuth ? AuthType.XBOX : AuthType.UNREGISTERED;
            client.executeUpdate("INSERT INTO players (username, type) VALUES (?, ?)", username, type.ordinal());
        }
    }

    public void setPassword(String password) {
        String hashedPassword = encoder.encode(password);
        DBClient client = AuthPNX.get().getSessionManager().getDatabase();
        client.executeUpdate("UPDATE players SET password = ?, type = 1 WHERE username = ?", hashedPassword, player.getName().toLowerCase());
        this.setAuthenticated();
    }

    public boolean doesPasswordMatch(String password) {
        String hashedPassword = encoder.encode(password);
        DBClient client = AuthPNX.get().getSessionManager().getDatabase();
        ResultSetList set = client.executeQuery("SELECT password FROM players WHERE username = ?", player.getName().toLowerCase());
        if(set.next()) {
            return set.getString("password").equals(hashedPassword);
        } else {
            return false;
        }
    }

    public void setAuthenticated() {
        this.authenticated = true;
    }

    public enum AuthType {
        UNREGISTERED,
        PASSWORD,
        XBOX,
        BYPASS
    }
}
