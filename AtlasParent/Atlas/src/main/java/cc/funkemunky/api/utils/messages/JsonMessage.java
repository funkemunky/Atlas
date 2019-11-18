package cc.funkemunky.api.utils.messages;

import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.Tuple;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.*;

public class JsonMessage {
    private final List<Tuple<String, List<Modifier>>> messages = new ArrayList<>();

    public String getFormattedMessage() {
        StringBuilder builder = new StringBuilder("[\"\",");
        for (Tuple<String, List<Modifier>> tuple : messages) {
            builder = builder.append(format(tuple.one, tuple.two)).append(",");
        }

        return new StringBuilder(builder.substring(0, builder.length() - 1))
                .append("]")
                .toString();
    }

    public void sendMessage(Player player) {
        try {
            Object base;
            Constructor titleConstructor = ReflectionsUtil.getNMSClass("PacketPlayOutChat").getConstructor(ReflectionsUtil.getNMSClass("IChatBaseComponent"));
            base = ReflectionsUtil.isBukkitVerison("1_7") || ReflectionsUtil.isBukkitVerison("1_8_R1") ? ReflectionsUtil.getNMSClass("ChatSerializer").getMethod("a", String.class).invoke(null, this.getFormattedMessage()) : ReflectionsUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, this.getFormattedMessage());
            Object packet = titleConstructor.newInstance(base);
            this.sendPacket(player, packet);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", ReflectionsUtil.getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String message, Modifier... modifiers) {
        messages.add(new Tuple<>(message, Arrays.asList(modifiers)));
    }

    private String format(String text, List<Modifier> mods) {
        StringBuilder chat = new StringBuilder("{\"text\":\"" + text + "\"");
        for (Modifier mod : mods) {
            chat.append(",\"").append(mod.getEvent().value)
                    .append("\":{\"action\":\"").append(mod.event).append("\",\"value\":").append(mod.value)
                    .append("}");
        }
        chat.append("}");
        return chat.toString();
    }
}

