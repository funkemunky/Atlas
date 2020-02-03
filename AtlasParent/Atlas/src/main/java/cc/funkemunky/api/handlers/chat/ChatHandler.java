package cc.funkemunky.api.handlers.chat;

import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInChatPacket;
import cc.funkemunky.api.utils.Init;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Init
public class ChatHandler implements AtlasListener {

    private static List<OnChat> toSortThrough = new ArrayList<>();

    @Listen
    public void onReceive(PacketReceiveEvent event) {
        if(event.getType().equals(Packet.Client.CHAT)) {
            val optional = toSortThrough.stream()
                    .filter(chat -> chat.player.getUniqueId().equals(event.getPlayer().getUniqueId()))
                    .findFirst();

            if(optional.isPresent()) {
                WrappedInChatPacket packet = new WrappedInChatPacket(event.getPacket(), event.getPlayer());
                OnChat chat = optional.get();

                chat.message.accept(packet.getMessage());

                if(chat.removeOnFirstChat) toSortThrough.remove(chat);

            }
        }
    }

    static void remove(Player player) {
        toSortThrough.stream().filter(pl -> pl.player.getUniqueId().equals(player.getUniqueId()))
                .forEach(toSortThrough::remove);
    }
}
