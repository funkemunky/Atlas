package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.utils.MathHelper;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class WrappedOutRelativePosition extends NMSObject {
    private static final String packet = Server.ENTITY;

    private static WrappedClass packetClass = Reflections.getNMSClass(packet);
    private static WrappedField fieldId, fieldX, fieldY, fieldZ, fieldYaw, fieldPitch, fieldGround;

    // Decoded data
    private int id;
    private boolean look, pos, ground;

    public WrappedOutRelativePosition(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        String name = getPacketName();

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            pos = name.equals(Server.LEGACY_REL_POSITION) || name.equals(Server.LEGACY_REL_POSITION_LOOK);
            look = name.equals(Server.LEGACY_REL_LOOK) || name.equals(Server.LEGACY_REL_POSITION_LOOK);
        } else {
            pos = name.equals(Server.REL_POSITION) || name.equals(Server.REL_POSITION_LOOK);
            look = name.equals(Server.REL_LOOK) || name.equals(Server.REL_POSITION_LOOK);
        }
        id = fetch(fieldId);
        ground = fetch(fieldGround);
    }

    @Override
    public void updateObject() {

    }

    public <T> T getX() {
        return fetch(fieldX);
    }

    public <T> T getY() {
        return fetch(fieldY);
    }

    public <T> T getZ() {
        return fetch(fieldZ);
    }

    public <T> T getYaw() {
        return fetch(fieldYaw);
    }

    public <T> T getPitch() {
        return fetch(fieldPitch);
    }

    public void setX(double x) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldX, MathHelper.floor_double_long(x * 4096));
        } else {
            set(fieldX, (byte)MathHelper.floor_double(x * 32));
        }
    }

    public void setY(double y) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldY, MathHelper.floor_double_long(y * 4096));
        } else {
            set(fieldY, (byte)MathHelper.floor_double(y * 32));
        }
    }


    public void setZ(double z) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldZ, MathHelper.floor_double_long(z * 4096));
        } else {
            set(fieldZ, (byte)MathHelper.floor_double(z * 32));
        }
    }

    public void setYaw(float yaw) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldYaw, MathHelper.floor_double(yaw * 256.0F / 360.0F));
        } else {
            set(fieldYaw, (byte)MathHelper.floor_double(yaw * 256.0F / 360.0F));
        }
    }

    public void setPitch(float pitch) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldPitch, MathHelper.floor_double(pitch * 256.0F / 360.0F));
        } else {
            set(fieldPitch, (byte)MathHelper.floor_double(pitch * 256.0F / 360.0F));
        }
    }


    static {
        List<WrappedField> fields = packetClass.getFields(true);

        fieldId = fields.get(0);
        fieldX = fields.get(1);
        fieldY = fields.get(2);
        fieldZ = fields.get(3);
        fieldYaw = fields.get(4);
        fieldPitch = fields.get(5);
        fieldGround = fields.get(6);
    }
}
