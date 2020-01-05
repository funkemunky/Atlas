package cc.funkemunky.api.commands.tab;

import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.ListenerPriority;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInTabComplete;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutTabComplete;
import cc.funkemunky.api.utils.Init;

import java.util.*;

@Init
public class TabHandler implements AtlasListener {

    public static TabHandler INSTANCE;
    private Map<String[], Set<String>> tabArgs = new HashMap<>();

    public TabHandler() {
        INSTANCE = this;
    }

    @Listen(priority = ListenerPriority.LOWEST)
    public void onTab(PacketReceiveEvent event) {
        if(event.getType().equals(Packet.Client.TAB_COMPLETE)
                && ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            WrappedInTabComplete packet = new WrappedInTabComplete(event.getPacket(), event.getPlayer());

            String[] args = (packet.getMessage().startsWith("/")
                    ? packet.getMessage().substring(1) : packet.getMessage())
                    .split(" ");

            if(tabArgs.containsKey(args)) {
                Set<String> options = tabArgs.get(args);

                WrappedOutTabComplete complete = new WrappedOutTabComplete(options.stream()
                        .sorted(Comparator.comparing(s -> s))
                        .toArray(String[]::new));

                TinyProtocolHandler.sendPacket(event.getPlayer(), complete.getObject());
            }
        }
    }

    public void addTabComplete(String[] requirement, String... args) {
        Set<String> complete = tabArgs.getOrDefault(requirement, new HashSet<>());

        complete.addAll(Arrays.asList(args));

        tabArgs.put(requirement, complete);
    }
}
