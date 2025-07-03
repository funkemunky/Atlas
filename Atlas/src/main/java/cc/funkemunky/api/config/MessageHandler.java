package cc.funkemunky.api.config;

import cc.funkemunky.api.utils.FunkeFile;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageHandler {

    public List<MessageConfig> messageConfigs = new ArrayList<>();
    private Plugin plugin;
    @Setter
    private String currentLang;

    public MessageHandler(Plugin plugin) {
        this.plugin = plugin;

        load();
    }

    public MessageConfig getLanguage(String language) {
        return messageConfigs.stream().filter(cnf -> cnf.language.equals(language))
                .findFirst().orElseGet(() -> {
                    FunkeFile fFile = new FunkeFile(plugin, "messages", "messages_" + language);
                    MessageConfig config = new MessageConfig(fFile, language);

                    messageConfigs.add(config);
                    return config;
                });
    }

    public MessageConfig getLanguage() {
        return getLanguage(currentLang);
    }

    private void load() {
        File dir = new File(plugin.getDataFolder().getPath() + File.separator + "messages");

        dir.mkdirs();

        File[] files = dir.listFiles();

        if(files == null) return;

        Arrays.stream(files)
                .filter(file -> file.getName().startsWith("messages_"))
                .forEach(file -> {
                    FunkeFile fFile = new FunkeFile(file);

                    MessageConfig config = new MessageConfig(fFile, file.getName().split("_")[1]);

                    messageConfigs.add(config);
                });
    }

    public void reloadAll() {
        messageConfigs.clear();
        load();
    }
}
