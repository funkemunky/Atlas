package cc.funkemunky.api.tinyprotocol.packet.in.impl;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.PacketType;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.in.ClientPacket;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedMinecraftKey;
import org.bukkit.entity.Player;

//TODO Test to see if this works at all.
public class WrappedInAdvancementsPacket extends ClientPacket {

    private static final WrappedClass packet = Reflections.getNMSClass(PacketType.Client.ADVANCEMENTS);

    private static WrappedClass statusClass = Reflections
            .getNMSClass(PacketType.Client.ADVANCEMENTS.vanillaName + "$Status");

    public Status status;
    public WrappedMinecraftKey key;

    private static WrappedField fieldStatus = fetchField(packet, statusClass.getParent(), 0),
            fieldKey = fetchField(packet, WrappedMinecraftKey.vanilla.getParent(), 0);

    public WrappedInAdvancementsPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {
        set(fieldStatus, status.toVanilla());
        key.updateObject(); //Updating any changes set in the key wrapper.
        set(fieldKey, key.getObject());
    }

    @Override
    public PacketType.Client getType() {
        return PacketType.Client.ADVANCEMENTS;
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        status = Status.fromVanilla(fetch(fieldStatus));
        key = new WrappedMinecraftKey(fetch(fieldKey));
    }

    public enum Status {
        OPENED_TAB,
        CLOSED_SCREEN;

        public static Status fromVanilla(Enum obj) {
            return valueOf(obj.name());
        }

        public <T> T toVanilla() {
            return (T) statusClass.getEnum(name());
        }
    }
}
