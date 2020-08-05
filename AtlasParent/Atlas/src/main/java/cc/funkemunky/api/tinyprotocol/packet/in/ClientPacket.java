package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.PacketType;
import org.bukkit.entity.Player;

public abstract class ClientPacket extends NMSObject {

    public ClientPacket(Object object) {
        super(object);
    }

    public ClientPacket(Object object, Player player) {
        super(object, player);
    }

    public ClientPacket(PacketReceiveEvent event) {
        super(event);
    }

    public ClientPacket(PacketSendEvent event) {
        super(event);
    }

    @Override
    public void updateObject() {

    }

    public abstract PacketType.Client getType();
}
