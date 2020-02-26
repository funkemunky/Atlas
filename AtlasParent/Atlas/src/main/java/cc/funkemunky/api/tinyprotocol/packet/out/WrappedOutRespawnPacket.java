package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedChatMessage;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumDifficulty;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumGameMode;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutRespawnPacket extends NMSObject {

    public WrappedOutRespawnPacket(Object object, Player player) {
        super(object, player);
    }

    private static String packet = Server.RESPAWN;

    private static FieldAccessor<Enum> difficultyAcessor;
    private static FieldAccessor<Enum> gamemodeAccessor;
    private static WrappedClass worldTypeClass;
    private static WrappedField worldTypeField;

    //Before 1.13
    private static FieldAccessor<Integer> dimensionAccesor;

    //1.13 and newer version of World ID
    private static FieldAccessor<Object> dimensionManagerAcceessor;
    private static WrappedClass dimensionManagerClass;
    private static WrappedField dimensionManagerField;
    private static WrappedMethod dimensionManagerFromId;

    private int dimension;
    private WrappedEnumGameMode gamemode;
    private WrappedEnumDifficulty difficulty;
    private WorldType worldType;

    public WrappedOutRespawnPacket(int dimension, WrappedEnumGameMode gamemode,
                                   WrappedEnumDifficulty difficulty, WorldType worldType) {
        this.dimension = dimension;
        this.gamemode = gamemode;
        this.difficulty = difficulty;
        this.worldType = worldType;

        updateObject();
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_13)) {
            Object dimensionManager = fetch(dimensionManagerAcceessor);
            dimension = dimensionManagerField.get(dimensionManager);
        } else {
            dimension = fetch(dimensionAccesor);
        }
        gamemode = WrappedEnumGameMode.fromObject(fetch(gamemodeAccessor));
        difficulty = WrappedEnumDifficulty.fromObject(fetch(difficultyAcessor));
        worldType = WorldType.getByName(worldTypeField.get(getObject()));
    }

    @Override
    public void updateObject() {
        setPacket(packet, ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_13)
                ? dimensionManagerFromId.invoke(dimension) : dimension,
                difficulty.getObject(), gamemode.getObject(),
                worldTypeClass.getEnum(worldType.getName()));
    }

    static {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_13)) {
            dimensionManagerAcceessor = fetchField(packet, Object.class, 0);
            dimensionManagerClass = Reflections.getNMSClass("DimensionManager");
            dimensionManagerField = dimensionManagerClass.getFirstFieldByType(int.class);
            dimensionManagerFromId = dimensionManagerClass.getMethod("a", int.class);
        } else dimensionAccesor = fetchField(packet, int.class, 0);

        difficultyAcessor = fetchField(packet, Enum.class, 0);
        gamemodeAccessor = fetchField(packet, Enum.class, 1);
        worldTypeClass = Reflections.getNMSClass("WorldType");
        worldTypeField = worldTypeClass.getFirstFieldByType(String.class);
    }
}
