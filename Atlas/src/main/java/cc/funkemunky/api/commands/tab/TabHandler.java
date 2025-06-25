package cc.funkemunky.api.commands.tab;

import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
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