package cc.funkemunky.api.config;

import cc.funkemunky.api.utils.FunkeFile;
import cc.funkemunky.carbon.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class MessageConfig {

    public String language;
    private FunkeFile file;
    private Map<String, String> messages = new HashMap<>();

    public MessageConfig(FunkeFile file, String language) {
        this.file = file;
        this.language = language;
        load();
    }

    public void save() {
        messages.forEach((key, value) -> {
            file.getLines().clear();
            file.addLine(key + ": " + "\"" + value + "\"");
        });
        file.write();
    }

    public void reload() {
        file.readFile();
        load();
    }

    private void load() {
        messages.clear();
        file.getLines().stream().map(line -> {
            String[] split = line.split(": ");

            return new Pair<>(split[0], split[1]);
        }).forEach(pair -> messages.put(pair.key, pair.value));
    }

    public String msg(String key, String def) {
        return messages.computeIfAbsent(key, stringKey -> {
            messages.put(key, def);
            return def;
        });
    }
}
