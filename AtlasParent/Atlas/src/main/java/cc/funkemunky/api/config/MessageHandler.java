package cc.funkemunky.api.config;

import cc.funkemunky.api.utils.FunkeFile;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageHandler {

    public List<MessageConfig> messageConfigs = new ArrayList<>();
    private JavaPlugin plugin;
    @Setter
    private String currentLang;

    public MessageHandler(JavaPlugin plugin) {
        this.plugin = plugin;

        Arrays.stream(plugin.getDataFolder().listFiles())
                .filter(file -> file.getName().startsWith("messages_"))
                .forEach(file -> {
                    FunkeFile fFile = new FunkeFile(file);

                    MessageConfig config = new MessageConfig(fFile, file.getName().split("_")[1]);


                });
    }

    public MessageConfig getLanguage(String language) {
        if(messageConfigs.size() > 0) {
            return messageConfigs.stream().filter(cnf -> cnf.language.equals(language))
                    .findFirst().orElse(messageConfigs.get(0));
        }
        return null;
    }

    public MessageConfig getLanguage() {
        return getLanguage(currentLang);
    }

}
