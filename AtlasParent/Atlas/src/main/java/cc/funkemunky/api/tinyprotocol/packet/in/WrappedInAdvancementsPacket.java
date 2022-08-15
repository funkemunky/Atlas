package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedMinecraftKey;
import org.bukkit.entity.Player;

//TODO Test to see if this works at all.
//TODO Test on 1.17
public class WrappedInAdvancementsPacket extends NMSObject {

    private static final WrappedClass packet = Reflections.getNMSClass(Client.ADVANCEMENTS);

    private static WrappedClass statusClass = Reflections.getNMSClass(Client.ADVANCEMENTS + "$Status");

    public Status status;
    public WrappedMinecraftKey key;

    public WrappedInAdvancementsPacket(Object object, Player player) {
        super(object, player);
    }

    private static WrappedField fieldStatus = fetchField(packet, statusClass.getParent(), 0),
            fieldKey = fetchField(packet, WrappedMinecraftKey.vanilla.getParent(), 0);

    @Override
    public void updateObject() {
        set(fieldStatus, status.toVanilla());
        key.updateObject(); //Updating any changes set in the key wrapper.
        set(fieldKey, key.getObject());
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
