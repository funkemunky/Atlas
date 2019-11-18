package cc.funkemunky.api.utils.messages.modifiers;

import cc.funkemunky.api.utils.Tuple;
import cc.funkemunky.api.utils.messages.Modifier;
import cc.funkemunky.api.utils.messages.ModifierType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ClickModifier extends Modifier {


    public ClickModifier(ClickableType type, String value) {
        super(type.action, value);
    }

    @Override
    public ModifierType getEvent() {
        return ModifierType.CLICKABLE;
    }

    @Override
    public Tuple<String, String> getFormatter() {
        return new Tuple<>(event, "\"" + value + "\"");
    }

    public enum ClickableType {
        RunCommand("run_command"),
        SuggestCommand("suggest_command"),
        OpenURL("open_url");

        final String action;

        ClickableType(String action) {
            this.action = action;
        }
    }
}

