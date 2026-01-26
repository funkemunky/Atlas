package cc.funkemunky.api.utils.compiler.msg;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

public class ChatBuilder {

    private ComponentBuilder componentBuilder;

    public ChatBuilder(String text, Object... objects) {
        componentBuilder = new ComponentBuilder(String.format(text, objects));

        //componentBuilder.color()
        //componentBuilder.append(String, FormatRetention);
    }

    public ChatBuilder text(String text, Object... objects) {
        componentBuilder.append(String.format(text, objects), ComponentBuilder.FormatRetention.ALL);
        return this;
    }

    public ChatBuilder color(String color) {
        String[] colors = color.split("\u00A7");

        for (String s : colors) {
            if(s.length() < 1) continue;
            switch(s.toCharArray()[0]) {
                case 'l':
                    componentBuilder.bold(true);
                    break;
                case 'k':
                    componentBuilder.obfuscated(true);
                    break;
                case 'm':
                    componentBuilder.strikethrough(true);
                    break;
                case 'n':
                    componentBuilder.underlined(true);
                    break;
                case 'o':
                    componentBuilder.italic(true);
                    break;
                case 'r':
                    componentBuilder.reset();
                    break;
                default:
                    componentBuilder.color(ChatColor.getByChar(s.toCharArray()[0]));
                    break;
            }
        }

        return this;
    }

    public ChatBuilder event(HoverEvent.Action action, BaseComponent... message) {
        componentBuilder.event(new HoverEvent(action, message));

        return this;
    }

    public ChatBuilder event(HoverEvent.Action action, TextComponent component) {
        return event(action, TextComponent.fromLegacyText(TextComponent.toLegacyText(component)));
    }

    public ChatBuilder event(HoverEvent.Action action, String string) {
        return event(action, TextComponent.fromLegacyText(string));
    }

    public ChatBuilder event(ClickEvent.Action action, String value) {
        componentBuilder.event(new ClickEvent(action, value));
        return this;
    }

    public ChatBuilder reset() {
        componentBuilder.reset();

        return this;
    }

    public BaseComponent[] build() {
        return componentBuilder.create();
    }

    /* STATICS */
    public static ChatBuilder create() {
        return new ChatBuilder("");
    }

    public static ChatBuilder create(String text, Object... objects) {
        return new ChatBuilder(text, objects);
    }
    /* END STATICS */
}
