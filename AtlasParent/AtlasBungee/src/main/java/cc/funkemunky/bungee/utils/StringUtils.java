package cc.funkemunky.bungee.utils;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class StringUtils {

    private static CommandSender consoleSender = BungeeCord.getInstance().getConsole();

    public static void printMessage(String message) {
        consoleSender.sendMessage(new TextComponent(Color.translate(message)));
    }
}
