package cc.funkemunky.api.handlers.chat;

import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInChatPacket;
import cc.funkemunky.api.utils.Init;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Init
public class ChatHandler implements AtlasListener {

    private static List<OnChat> toSortThrough = new ArrayList<>();
    private static Map<UUID, Consumer<String>> chatChecks = new HashMap<>();

    @Listen
    public void onReceive(PacketReceiveEvent event) {
        if(event.getType().equals(Packet.Client.CHAT)) {
            val optional = toSortThrough.stream()
                    .filter(chat -> chat.player.getUniqueId().equals(event.getPlayer().getUniqueId()))
                    .findFirst();

            WrappedInChatPacket packet = new WrappedInChatPacket(event.getPacket(), event.getPlayer());
            if(optional.isPresent()) {
                OnChat chat = optional.get();

                chat.message.accept(packet.getMessage());

                if(chat.removeOnFirstChat) toSortThrough.remove(chat);
            }
            chatChecks.computeIfPresent(event.getPlayer().getUniqueId(),
                    (key, consumer) -> {
                        consumer.accept(packet.getMessage());

                        event.setCancelled(true);
                        return consumer;
                    });
        }
    }

    public static void remove(Player player) {
        toSortThrough.stream().filter(pl -> pl.player.getUniqueId().equals(player.getUniqueId()))
                .forEach(toSortThrough::remove);
        chatChecks.remove(player.getUniqueId());
    }

    public static void onChat(Player player, Consumer<String> consumer) {
        chatChecks.put(player.getUniqueId(), consumer);
    }
}
