package cc.funkemunky.api.handlers.chat;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInChatPacket;
import cc.funkemunky.api.utils.Init;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

@Init
public class ChatHandler implements AtlasListener {

    private final static Map<UUID, List<OnChat>> chatListeners = Collections.synchronizedMap(new HashMap<>());

    public ChatHandler() {
        Atlas.getInstance().getPacketProcessor().process(Atlas.getInstance(),
                event -> {
                    WrappedInChatPacket packet = new WrappedInChatPacket(event.getPacket(), event.getPlayer());

                    if (chatListeners.size() > 0) {
                        synchronized (chatListeners) {
                            AtomicBoolean cancelled = new AtomicBoolean(false);
                            chatListeners.computeIfPresent(event.getPlayer().getUniqueId(), (key, chats) -> {
                                List<OnChat> returnChats = new ArrayList<>(chats);
                                returnChats.forEach(chat -> {
                                    chat.message.accept(chat, packet.getMessage());
                                    if (chat.removeOnFirstChat) returnChats.remove(chat);
                                });

                                cancelled.set(true);

                                //Removing player from map if theres nothing else to listen to.
                                return returnChats.size() > 0 ? returnChats : null;
                            });
                            if(cancelled.get()) event.setCancelled(true);
                        }
                    }
                }, Packet.Client.CHAT);
    }

    public static void removeAll(Player player) {
        removeAll(player.getUniqueId());
    }

    public static void removeAll(UUID uuid) {
        synchronized (chatListeners) {
            chatListeners.remove(uuid);
        }
    }

    public static void remove(UUID uuid, OnChat chat) {
        if (!chatListeners.containsKey(uuid)) return;
        synchronized (chatListeners) {
            chatListeners.compute(uuid, (key, chats) -> {
                if (chats != null) {
                    chats.remove(chat);
                    if (chats.size() == 0) chats = null;
                }

                return chats;
            });
        }
    }

    public static void remove(Player player, OnChat chat) {
        remove(player.getUniqueId(), chat);
    }

    public static void onChat(Player player, boolean removeOnFirstChat, BiConsumer<OnChat, String> consumer) {
        OnChat chat = new OnChat(player, consumer, removeOnFirstChat);

        synchronized (chatListeners) {
            chatListeners.compute(player.getUniqueId(), (key, chats) -> {
                if (chats == null) chats = new ArrayList<>();
                chats.add(chat);

                return chats;
            });
        }
    }
}
