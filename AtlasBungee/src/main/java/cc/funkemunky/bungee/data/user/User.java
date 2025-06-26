package cc.funkemunky.bungee.data.user;

import cc.funkemunky.bungee.data.ModData;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {

    private static Map<UUID, User> users = new HashMap<>();

    public ModData modData;
    public final UUID uuid;
    private ProxiedPlayer player;
    public int version;
    public String brand;
    public boolean legacy;

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public ProxiedPlayer getPlayer() {
        if(player != null) {
            return player;
        }

        return this.player = BungeeCord.getInstance().getPlayer(uuid);
    }

    public static User getUser(UUID uuid) {
        return users.computeIfAbsent(uuid, User::new);
    }
}
