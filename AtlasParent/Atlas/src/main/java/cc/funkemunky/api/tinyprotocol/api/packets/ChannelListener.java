package cc.funkemunky.api.tinyprotocol.api.packets;

import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.Priority;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Init(priority = Priority.HIGH)
public abstract class ChannelListener {

    public static String handle = "atlas_packet_listener";

    @Getter
    @ConfigSetting(name = "packet", path = "threadType")
    private static ThreadType threadType = ThreadType.PER_PLAYER;

    @ConfigSetting(name = "packet.setThreading", path = "threadCount")
    static int threadCount = 8;

    public abstract void inject(Player player);

    public abstract void uninject(Player player);

    public abstract Object onReceive(Player player, Object packet);

    public abstract Object onSend(Player player, Object packet);

    public enum ThreadType {
        PER_PLAYER,
        SET_THREADING,
        NETTY_ONLY;
    }
}
