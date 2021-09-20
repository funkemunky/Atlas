package cc.funkemunky.api.bungee.objects;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
