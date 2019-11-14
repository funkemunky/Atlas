package cc.funkemunky.api.utils.messages;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonMessage {
    private final Map<String, List<Modifier>> messages = new LinkedHashMap<>();

    public String getFormattedMessage() {
        StringBuilder builder = new StringBuilder("[\"\",");
        messages.forEach((message, mods) -> {
            builder.append(format(message, mods)).append(",");
        });

        return new StringBuilder(builder.substring(0, builder.length() - 1))
                .append("]")
                .toString();
    }

    public void sendMessage(Player player) {
        player.sendRawMessage(getFormattedMessage());
    }

    public void addMessage(String message, Modifier... modifiers) {
        messages.put(message, Arrays.asList(modifiers));
    }

    private String format(String text, List<Modifier> mods) {
        StringBuilder msgBuilder = new StringBuilder("{\"text\":\"" + text + "\"");

        mods.stream().forEach(mod -> {
            msgBuilder.append(",\"").append(mod.getEvent().value).append("\":{\"action\":\"")
                    .append(mod.event)
                    .append("\",\"value\":")
                    .append(mod.value)
                    .append("}");
        });
        msgBuilder.append("}");
        return msgBuilder.toString();
    }
}

