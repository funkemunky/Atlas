package cc.funkemunky.api.handlers.chat;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@AllArgsConstructor
@RequiredArgsConstructor
public class OnChat {
    final Player player;
    final Consumer<String> message;
    boolean removeOnFirstChat;

    public void remove() {
        ChatHandler.remove(player);
    }
}
