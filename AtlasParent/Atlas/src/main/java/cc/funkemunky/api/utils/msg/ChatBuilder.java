package cc.funkemunky.api.utils.msg;

import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MathUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            List<TextComponent> components = textComponents.get(MathUtils.getByte(currentLine));

            components.add(component);
            textComponents.put(MathUtils.getByte(currentLine), components);
        }
        return this;
    }

    public ChatBuilder newLine() {
        textComponents.put(MathUtils.getByte(++currentLine), new ArrayList<>());

        return this;
    }

    public ChatBuilder sendToPlayer(Player player) {
        return this;
    }
}
