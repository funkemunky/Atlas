//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cc.funkemunky.api.utils.msg;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class ChatBuilder {
    private ComponentBuilder componentBuilder;

    public ChatBuilder(String text, Object... objects) {
        this.componentBuilder = new ComponentBuilder(String.format(text, objects));
    }

    public ChatBuilder text(String text, Object... objects) {
        this.componentBuilder.append(String.format(text, objects), FormatRetention.ALL);
        return this;
    }

    public ChatBuilder color(String color) {
        String[] colors = color.split("§");
        String[] var3 = colors;
        int var4 = colors.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String s = var3[var5];
            if (s.length() >= 1) {
                switch(s.toCharArray()[0]) {
                case 'k':
                    this.componentBuilder.obfuscated(true);
                    break;
                case 'l':
                    this.componentBuilder.bold(true);
                    break;
                case 'm':
                    this.componentBuilder.strikethrough(true);
                    break;
                case 'n':
                    this.componentBuilder.underlined(true);
                    break;
                case 'o':
                    this.componentBuilder.italic(true);
                    break;
                case 'p':
                case 'q':
                default:
                    this.componentBuilder.color(ChatColor.getByChar(s.toCharArray()[0]));
                    break;
                case 'r':
                    this.componentBuilder.reset();
                }
            }
        }

        return this;
    }

    public ChatBuilder event(Action action, BaseComponent... message) {
        this.componentBuilder.event(new HoverEvent(action, message));
        return this;
    }

    public ChatBuilder event(Action action, TextComponent component) {
        return this.event(action, TextComponent.fromLegacyText(TextComponent.toLegacyText(new BaseComponent[]{component})));
    }

    public ChatBuilder event(Action action, String string) {
        return this.event(action, TextComponent.fromLegacyText(string));
    }

    public ChatBuilder event(net.md_5.bungee.api.chat.ClickEvent.Action action, String value) {
        this.componentBuilder.event(new ClickEvent(action, value));
        return this;
    }

    public ChatBuilder reset() {
        this.componentBuilder.reset();
        return this;
    }

    public BaseComponent[] build() {
        return this.componentBuilder.create();
    }

    public static ChatBuilder create() {
        return new ChatBuilder("", new Object[0]);
    }

    public static ChatBuilder create(String text, Object... objects) {
        return new ChatBuilder(text, objects);
    }
}
