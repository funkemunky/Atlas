package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInClientCommandPacket extends NMSObject {
    private static final String packet = Client.CLIENT_COMMAND;

    // Fields
    private static FieldAccessor<Enum> fieldCommand = fetchField(packet, Enum.class, 0);
    private static WrappedClass enumClientCommand = Reflections.getNMSClass(
            (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_5) ? packet + "." : "")
                    + "EnumClientCommand");

    // Decoded data
    EnumClientCommand command;

    public WrappedInClientCommandPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        command = EnumClientCommand.values()[fetch(fieldCommand).ordinal()];
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), packet, enumClientCommand.getEnum(command.name())));
    }

    public enum EnumClientCommand {
        PERFORM_RESPAWN,
        REQUEST_STATS,
        OPEN_INVENTORY_ACHIEVEMENT;

        private EnumClientCommand() {
        }
    }
}
