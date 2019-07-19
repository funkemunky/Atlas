package cc.funkemunky.bungee.utils;

import cc.funkemunky.bungee.utils.json.JSONException;
import cc.funkemunky.bungee.utils.json.JSONObject;
import cc.funkemunky.bungee.utils.reflection.FieldAccessor;
import cc.funkemunky.bungee.utils.reflection.Reflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.lang.reflect.Field;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
public class BungeePlayer {
    public String name, server, uuid;
    public List<String> permissions;
    public Map<String, Object> values = new HashMap<>();
    public Map<String, String> forgeMods = new HashMap<>();
    public boolean usingForge;

    public static BungeePlayer fromProxied(ProxiedPlayer player) {
        BungeePlayer bplayer = new BungeePlayer();
        bplayer.forgeMods = player.getModList();
        bplayer.usingForge = player.isForgeUser();
        bplayer.name = player.getName();
        bplayer.uuid = player.getUniqueId().toString();
        bplayer.permissions = new ArrayList<>(player.getPermissions());
        bplayer.server = player.getServer().getInfo().getName();

        return bplayer;
    }

    public String toJson() {
        try {
            JSONObject object = new JSONObject();

            for (Field field : getClass().getFields()) {
                FieldAccessor access = Reflection.fieldToAccessor(field);

                object.put(field.getName(), access.get(this));
            }

            return object.toString();
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Server getServer() {
        return BungeeCord.getInstance().getPlayer(UUID.fromString(uuid)).getServer();
    }
}
