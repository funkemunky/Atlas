package cc.funkemunky.api.commands.tab;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.listener.functions.AsyncPacketListener;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInTabComplete;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutTabComplete;
import cc.funkemunky.api.tinyprotocol.packet.types.v1_13.DontImportIfNotLatestThanks;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.Priority;

import java.util.*;

@Init(priority = Priority.HIGH)
@Deprecated
public class TabHandler implements AtlasListener {

    public static TabHandler INSTANCE;
    private Map<String[], Set<String>> tabArgs = new HashMap<>();
    private static DontImportIfNotLatestThanks stuff;

    public TabHandler() {
        INSTANCE = this;
    }

    private AsyncPacketListener tabComplete = Atlas.getInstance().getPacketProcessor().processAsync(Atlas.getInstance(),
            event -> {
                WrappedInTabComplete packet = new WrappedInTabComplete(event.getPacket(), event.getPlayer());

                String[] args = (packet.getMessage().startsWith("/")
                        ? packet.getMessage().toLowerCase().substring(1) : packet.getMessage().toLowerCase())
                        .split(" ");

                if (tabArgs.containsKey(args)) {
                    Set<String> options = tabArgs.get(args);

                    WrappedOutTabComplete complete = new WrappedOutTabComplete(options.stream()
                            .sorted(Comparator.comparing(s -> s))
                            .toArray(String[]::new));

                    TinyProtocolHandler.sendPacket(event.getPlayer(), complete.getObject());

                }
            }, Packet.Client.TAB_COMPLETE);

    public void addTabComplete(String[] requirement, String... args) {
        if (stuff != null) {
            for (String arg : args) {
                String[] array = new String[requirement.length + 1];

                System.arraycopy(requirement, 0, array, 0, requirement.length);

                array[array.length - 1] = arg;

                stuff.registerTabComplete(array);
            }
            return;
        }
        Set<String> complete = tabArgs.getOrDefault(requirement, new HashSet<>());

        complete.addAll(Arrays.asList(args));

        tabArgs.put(requirement, complete);
    }

    static {
        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            stuff = new DontImportIfNotLatestThanks();
        }
    }
}