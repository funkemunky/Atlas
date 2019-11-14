package cc.funkemunky.api.utils.messages;

import cc.funkemunky.api.utils.FunkeFile;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class MessageConfig {
    public Map<String, String> messageMappings = new HashMap<>();
    private FunkeFile file;

    public MessageConfig(Plugin plugin, String directory, String name) {
        file = new FunkeFile(plugin, directory, name);
    }


    public String getMessage(String key) {
        if(messageMappings.containsKey(key)) {
            return messageMappings.get(key);
        }
        return null;
    }

    public void insert(String key, String message) {
        messageMappings.put(key, message);
    }

    public void loadConfig() {
        file.readFile();
        String parent;
        try {
            for (int i = 0; i < file.getLines().size(); i++) {
                String line = file.getLines().get(i);
                if(!line.contains(":")) {
                    throw new ParsingError("There is no ':' on line " + i + "!");
                }
                if(!line.startsWith("  ")) {
                    String[] split = line.split(":");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
