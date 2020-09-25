package cc.funkemunky.api.tinyprotocol.api.channel;

import org.bukkit.entity.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ChannelListener {

    public static boolean serverStopped = false, registered;
    public static String handle = "custom_packet_listener";

    public abstract void inject(Player player);

    public abstract void uninject(Player player);

    public abstract void sendPacket(Player player, Object packet);

    public abstract void receivePacket(Player player, Object packet);

    public abstract int getProtocolVersion(Player player);
}
