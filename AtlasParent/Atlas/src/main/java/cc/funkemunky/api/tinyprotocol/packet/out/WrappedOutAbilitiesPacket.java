package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

public class WrappedOutAbilitiesPacket extends NMSObject {
    private static final String packet = Server.ABILITIES;

    private static FieldAccessor<Boolean>
            invulnerableField = fetchField(packet, boolean.class, 0),
            flyingField = fetchField(packet, boolean.class, 1),
            allowedFlightField = fetchField(packet, boolean.class, 2),
            creativeModeField = fetchField(packet, boolean.class, 3);
    private static FieldAccessor<Float>
            flySpeedField = fetchField(packet, float.class, 0),
            walkSpeedField = fetchField(packet, float.class, 1);

    private static WrappedClass abilitiesClass = Reflections.getNMSClass("PlayerAbilities");
    private static WrappedField invulnerableAcc = abilitiesClass.getFieldByType(boolean.class, 0);
    private static WrappedField flyingAcc = abilitiesClass.getFieldByType(boolean.class, 1);
    private static WrappedField allowedFlightAcc = abilitiesClass.getFieldByType(boolean.class, 2);
    private static WrappedField creativeModeAcc = abilitiesClass.getFieldByType(boolean.class, 3);
    private static WrappedField flySpeedAcc = abilitiesClass.getFieldByType(float.class, 0);
    private static WrappedField walkSpeedAcc = abilitiesClass.getFieldByType(float.class, 1);
    @Getter
    private boolean invulnerable, flying, allowedFlight, creativeMode;
    @Getter
    private float flySpeed, walkSpeed;


    public WrappedOutAbilitiesPacket(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutAbilitiesPacket(boolean invulernable, boolean flying, boolean allowedFlight, boolean creativeMode, float flySpeed, float walkSpeed) {
        Object abilities = abilitiesClass.getConstructorAtIndex(0).newInstance();
        invulnerableAcc.set(abilities, invulernable);
        flyingAcc.set(abilities, flying);
        allowedFlightAcc.set(abilities, allowedFlight);
        creativeModeAcc.set(abilities, creativeMode);
        flySpeedAcc.set(abilities, flySpeed);
        walkSpeedAcc.set(abilities, walkSpeed);

       setObject(Reflections.getNMSClass(packet).getConstructor(abilitiesClass.getParent()).newInstance(abilities));
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        invulnerable = fetch(invulnerableField);
        flying = fetch(flyingField);
        allowedFlight = fetch(allowedFlightField);
        creativeMode = fetch(creativeModeField);
        flySpeed = fetch(flySpeedField);
        walkSpeed = fetch(walkSpeedField);
    }
}