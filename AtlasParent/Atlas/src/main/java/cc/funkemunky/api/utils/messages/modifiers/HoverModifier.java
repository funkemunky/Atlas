package cc.funkemunky.api.utils.messages.modifiers;

import cc.funkemunky.api.utils.Tuple;
import cc.funkemunky.api.utils.messages.Modifier;
import cc.funkemunky.api.utils.messages.ModifierType;
import lombok.Getter;

@Getter
public class HoverModifier extends Modifier {

    private String[] messages;

    public HoverModifier(String... messages) {
        super("show_text", "");

        this.messages = messages;
    }

    @Override
    public ModifierType getEvent() {
        return ModifierType.HOVER;
    }

    @Override
    public Tuple<String, String> getFormatter() {
        String event = "show_text";
        StringBuilder value = new StringBuilder();
        if (this.messages.length == 1) {
            value = new StringBuilder("{\"text\":\"" + this.messages[0] + "\"}");
        } else {
            value = new StringBuilder("{\"text\":\"\",\"extra\":[");
            for (String message : messages) {
                value.append("{\"text\":\"").append(message).append("\"},");
            }
            value = new StringBuilder(value.substring(0, value.length() - 1));
            value.append("]}");
        }
        return new Tuple<>(event, value.toString());
    }
}
