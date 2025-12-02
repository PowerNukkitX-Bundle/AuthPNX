package org.powernukkitx.authpnx.session;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.Config;
import de.buddelbubi.database.DBClient;
import de.buddelbubi.database.H2Client;
import de.buddelbubi.database.MySQLClient;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.Getter;
import org.powernukkitx.authpnx.AuthPNX;

import java.io.File;

public class SessionManager implements Listener {

    @Getter
    private final DBClient database;

    private final Object2ObjectArrayMap<Player, AuthSession> SESSIONS = new Object2ObjectArrayMap<>();

    public SessionManager() {
        Config config = AuthPNX.get().getConfig();
        database = switch (config.getString("provider")) {
            case "h2" -> {
                File file = new File(AuthPNX.get().getDataFolder() + "/database/", "session");
                yield new H2Client(file);
            }
            case "mysql" -> {
                String host = config.getString("mysql.host");
                Integer port = config.getInt("mysql.port");
                String user = config.getString("mysql.user");
                String password = config.getString("mysql.password");
                String database = config.getString("mysql.database");
                String url = String.format("jdbc:mysql://%s:%d/%s?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", host, port, database);
                yield new MySQLClient(url, user, password);
            }
            default -> throw new IllegalArgumentException("The database provider is invalid!");
        };
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS players (" +
                        "    username VARCHAR(255)," +
                        "    type TINYINT," +
                        "    password VARCHAR(255)," +
                        "    registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");"
        );
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if(!event.isCancelled()) {
            Player player = event.getPlayer();
            AuthSession session = new AuthSession(player);
            SESSIONS.put(player, session);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SESSIONS.remove(event.getPlayer());
    }

    public AuthSession get(Player player) {
        return SESSIONS.get(player);
    }
}
