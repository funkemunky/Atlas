package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutAttachEntity extends NMSObject {

    //Not tested in all versions, just a basically wrapped packet.

    private static final String packet = Server.ATTACH;
    private static final FieldAccessor<Integer> fieldA = fetchField(packet, int.class, 0);
    private static final FieldAccessor<Integer> fieldB = fetchField(packet, int.class, 1);
    private static final FieldAccessor<Integer> fieldC = fetchField(packet, int.class, 2);

    private int a, b, c;


    public WrappedOutAttachEntity(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        a = fetch(fieldA);
        b = fetch(fieldB);
        c = fetch(fieldC);
    }
}