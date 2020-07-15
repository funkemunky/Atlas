package cc.funkemunky.api.utils.msg;

import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.exceptions.InvalidObjectException;
import cc.funkemunky.api.utils.exceptions.impl.ColorFormatException;
import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.*;

//TODO ChatBuilder base on TextComponent
public class ChatBuilder {

    public Map<Byte, List<TextComponent>> textComponents = new HashMap<>();

    private int currentLine = 1;
    private int addedMin, addedMax;
    public ChatBuilder() {
        textComponents.put(MathUtils.getByte(1), new ArrayList<>());
    }

    public ChatBuilder addText(String text) {
        String[] splitColors = text.split("&");

        for (String splitColor : splitColors) {
            TextComponent component = new TextComponent(splitColor.substring(1));

            component.setColor(ChatColor.getByChar(splitColor.charAt(0)));
            List<TextComponent> components = getCurrentComps();

            components.add(component);
            textComponents.put(MathUtils.getByte(currentLine), components);
        }
        return this;
    }

    public ChatBuilder addText(TextComponent... component) {
        List<TextComponent> components = getCurrentComps();

        components.addAll(Arrays.asList(component));

        textComponents.put(MathUtils.getByte(currentLine), components);

        return this;
    }

    @SneakyThrows
    public ChatBuilder addText(String color, String string, Object... events) {
        List<TextComponent> components = getCurrentComps();

        TextComponent component = new TextComponent(string);

        for (Object obj : events) {
            if(obj instanceof HoverEvent) {
                HoverEvent event = (HoverEvent) obj;

                component.setHoverEvent(event);
            } else if(obj instanceof ClickEvent) {
                ClickEvent event = (ClickEvent) obj;

                component.setClickEvent(event);
            } else {
                throw new InvalidObjectException(obj, HoverEvent.class, ClickEvent.class);
            }
        }

        val translated = Color.translate(color);
        boolean bold = translated.contains(Color.Bold),
                italic = translated.contains(Color.Italics),
                underline = translated.contains(Color.Underline),
                strikeThru = translated.contains(Color.Strikethrough);

        String finalized = color.replace(Color.Bold, "")
                .replace(Color.Italics, "")
                .replace(Color.Underline, "")
                .replace(Color.Strikethrough, "");

        if(finalized.length() != 2)
            throw new ColorFormatException("Color string was not length 2 (length=" + finalized.length() + ").");

        component.setBold(bold);
        component.setItalic(italic);
        component.setUnderlined(underline);
        component.setStrikethrough(strikeThru);

        components.add(component);

        return this;
    }

    public ChatBuilder newLine() {
        textComponents.put(MathUtils.getByte(++currentLine), new ArrayList<>());

        return this;
    }

    private void update(List<TextComponent> components) {
        textComponents.put(MathUtils.getByte(currentLine), components);
    }

    private List<TextComponent> getCurrentComps() {
        return textComponents.computeIfAbsent(MathUtils.getByte(currentLine), key -> {
            List<TextComponent> list = new ArrayList<>();

            textComponents.put(MathUtils.getByte(currentLine), list);

            return list;
        });
    }
    public ChatBuilder sendToPlayer(Player player) {
        return this;
    }
}
