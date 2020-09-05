package cc.funkemunky.api.bungee.objects;

import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import dev.brighten.db.utils.json.JSONException;
import dev.brighten.db.utils.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
public class BungeePlayer {
    public String name, server;
    public UUID uuid;
    public List<String> permissions;
    public Map<String, Object> values = new HashMap<>();
    public Map<String, String> forgeMods = new HashMap<>();
    public boolean usingForge;

    public static BungeePlayer fromJson(String json) {
        try {
            JSONObject object = new JSONObject(json);

            BungeePlayer player = new BungeePlayer();

            Iterator iterator = object.keys();

            while(iterator.hasNext()) {
                String key = (String) iterator.next();

                Reflection.getField(BungeePlayer.class, key, 0).set(player, object.get(key));
            }

            player.uuid = Bukkit.getPlayer(player.name).getUniqueId();
            return player;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
