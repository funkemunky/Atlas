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
    public String name, server, brand;
    public UUID uuid;
    public List<String> permissions;
    public Map<String, Object> values = new HashMap<>();
    public Map<String, String> forgeMods = new HashMap<>();
    public boolean usingForge, legacy;
}
