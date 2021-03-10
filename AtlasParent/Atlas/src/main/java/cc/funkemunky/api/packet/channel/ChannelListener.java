package cc.funkemunky.api.packet.channel;

import org.bukkit.entity.Player;

public interface ChannelListener {

    void inject(Player player);

    void uninject(Player player);

    void sendPacket(Player player, Object packet);

    void receivePacket(Player player, Object packet);

    int getProtocolVersion(Player player);
}
