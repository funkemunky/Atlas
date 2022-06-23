package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.MathHelper;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Deprecated
public class WrappedOutRelativePosition extends NMSObject {
    private static final String packet = Server.ENTITY;

    private static boolean useLegacy = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8);
    private static WrappedClass packetClass = Reflections.getNMSClass(packet),
            posClass = Reflections.getNMSClass(useLegacy ? Server.LEGACY_REL_POSITION : Server.REL_POSITION),
            posLookClass = Reflections.getNMSClass(useLegacy
                    ? Server.LEGACY_REL_POSITION_LOOK : Server.REL_POSITION_LOOK),
            lookClass = Reflections.getNMSClass(useLegacy ? Server.LEGACY_REL_LOOK : Server.REL_LOOK);
    private static WrappedConstructor emptyConst = packetClass.getConstructor(), posLookConst, posConst, lookConst;
    private static WrappedField fieldId, fieldX, fieldY, fieldZ, fieldYaw, fieldPitch, fieldGround;

    // Decoded data
    private int id;
    private boolean look, pos, ground;

    public WrappedOutRelativePosition(int id, float yaw, float pitch, boolean ground) {
        super(new Object());
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            setObject(lookClass.getConstructor().newInstance());

            set(fieldId, id);
            setYaw(yaw);
            setPitch(pitch);
            set(fieldGround, ground);
        } else {
            setObject(lookConst.newInstance(id, (byte)MathHelper.floor_double(yaw * 256.0F / 360.0F),
                    (byte)MathHelper.floor_double(pitch * 256.0F / 360.0F),
                    ground));
        }
    }

    public WrappedOutRelativePosition(int id, double x, double y, double z, boolean ground) {
        super(new Object());
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            setObject(posClass.getConstructor().newInstance());

            set(fieldId, id);
            setX(x);
            setY(y);
            setZ(z);
            set(fieldGround, ground);
        } else {
            setObject(posConst.newInstance(id, (short) MathHelper.floor_double(x * 4096.0D),
                    (short) MathHelper.floor_double(y * 4096.0D),
                    (short) MathHelper.floor_double(z * 4096.0D),
                    ground));
        }
    }

    public WrappedOutRelativePosition(int id, double x, double y, double z, float yaw, float pitch, boolean ground) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            setObject(posLookClass.getConstructor().newInstance());

            set(fieldId, id);
            setX(x);
            setY(y);
            setZ(z);
            setYaw(yaw);
            setPitch(pitch);
            set(fieldGround, ground);
        } else {
            setObject(posLookConst.newInstance(id, (short) MathHelper.floor_double(x * 4096.0D),
                    (short) MathHelper.floor_double(y * 4096.0D),
                    (short) MathHelper.floor_double(z * 4096.0D),
                    (byte)MathHelper.floor_double(yaw * 256.0F / 360.0F),
                    (byte)MathHelper.floor_double(pitch * 256.0F / 360.0F),
                    ground));
        }
    }

    public WrappedOutRelativePosition(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        String name = getPacketName();

        if (useLegacy) {
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
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_14)) {
            set(fieldX, (short) MathHelper.floor_double(x * 4096.0D));
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldX, (int)MathHelper.floor_double_long(x * 4096));
        } else {
            set(fieldX, (byte)MathHelper.floor_double(x * 32D));
        }
    }

    public void setY(double y) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_14)) {
            set(fieldY, (short) MathHelper.floor_double(y * 4096.0D));
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldY, (int)MathHelper.floor_double_long(y * 4096));
        } else {
            set(fieldY, (byte)MathHelper.floor_double(y * 32D));
        }
    }


    public void setZ(double z) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_14)) {
            set(fieldZ, (short) MathHelper.floor_double(z * 4096.0D));
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldZ, (int)MathHelper.floor_double_long(z * 4096));
        } else {
            set(fieldZ, (byte)MathHelper.floor_double(z * 32D));
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

        int addIndex = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
                && ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_19) ? 1 : 0;

        fieldId = fields.get(0 + addIndex);
        fieldX = fields.get(1 + addIndex);
        fieldY = fields.get(2 + addIndex);
        fieldZ = fields.get(3 + addIndex);
        fieldYaw = fields.get(4 + addIndex);
        fieldPitch = fields.get(5 + addIndex);
        fieldGround = fields.get(6 + addIndex);

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)) {
            posLookConst = posLookClass
                    .getConstructor(int.class, short.class, short.class,
                            short.class, byte.class, byte.class, boolean.class);
            posConst = posClass.getConstructor(int.class, short.class, short.class, short.class, boolean.class);
            lookConst = lookClass.getConstructor(int.class, byte.class, byte.class, boolean.class);
        }
    }
}
