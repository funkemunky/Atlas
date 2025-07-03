package cc.funkemunky.api.handlers.chat;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

@AllArgsConstructor
@RequiredArgsConstructor
public class OnChat {
    final Player player;
    final BiConsumer<OnChat, String> message;
    boolean removeOnFirstChat;

    public void remove() {
        ChatHandler.removeAll(player);
    }
}
