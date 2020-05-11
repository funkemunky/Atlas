package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

public class WrappedOutAbilitiesPacket extends NMSObject {
    private static final String packet = Server.ABILITIES;

    private static WrappedClass abilitiesClass = Reflections.getNMSClass(Server.ABILITIES);
    private static WrappedField invulnerableField = abilitiesClass.getFieldByType(boolean.class, 0);
    private static WrappedField flyingField = abilitiesClass.getFieldByType(boolean.class, 1);
    private static WrappedField allowedFlightField = abilitiesClass.getFieldByType(boolean.class, 2);
    private static WrappedField creativeModeField = abilitiesClass.getFieldByType(boolean.class, 3);
    private static WrappedField flySpeedField = abilitiesClass.getFieldByType(float.class, 0);
    private static WrappedField walkSpeedField = abilitiesClass.getFieldByType(float.class, 1);
    @Getter
    private boolean invulnerable, flying, allowedFlight, creativeMode;
    @Getter
    private float flySpeed, walkSpeed;


    public WrappedOutAbilitiesPacket(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutAbilitiesPacket(boolean invulernable, boolean flying, boolean allowedFlight, boolean creativeMode, float flySpeed, float walkSpeed) {
        Object abilities = abilitiesClass.getConstructorAtIndex(0).newInstance();
        invulnerableField.set(abilities, invulernable);
        flyingField.set(abilities, flying);
        allowedFlightField.set(abilities, allowedFlight);
        creativeModeField.set(abilities, creativeMode);
        flySpeedField.set(abilities, flySpeed);
        walkSpeedField.set(abilities, walkSpeed);

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

    @Override
    public void updateObject() {
        invulnerableField.set(getObject(), invulnerable);
        flyingField.set(getObject(), flying);
        allowedFlightField.set(getObject(), allowedFlight);
        creativeModeField.set(getObject(), creativeMode);
        flySpeedField.set(getObject(), flySpeed);
        walkSpeedField.set(getObject(), walkSpeed);
    }
}