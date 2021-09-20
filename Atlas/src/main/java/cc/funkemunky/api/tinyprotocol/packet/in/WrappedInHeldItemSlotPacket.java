package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInHeldItemSlotPacket extends NMSObject {
    private static final String packet = Client.HELD_ITEM;

    // Fields
    private static FieldAccessor<Integer> fieldHeldSlot = fetchField(packet, int.class, 0);

    // Decoded data
    private int slot;


    public WrappedInHeldItemSlotPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        slot = fetch(fieldHeldSlot);
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), packet, slot));
    }
}
