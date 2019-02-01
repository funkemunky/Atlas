package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedWatchableObject;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WrappedOutEntityMetadata extends NMSObject {
    private static final String packet = Packet.Server.ENTITY_METADATA;

    private static FieldAccessor<Integer> entity_id;
    private static FieldAccessor<List> watchableObjects;

    private List<WrappedWatchableObject> objects;

    public WrappedOutEntityMetadata(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        watchableObjects = fetchField(packet, List.class, 0);

        objects = new ArrayList<>();

        List list = fetch(watchableObjects);

        if(list != null) {
            list.forEach(object -> objects.add(new WrappedWatchableObject(object)));
        }
    }
}
