package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedGameProfile;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedPlayerInfoData;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumGameMode;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumPlayerInfoAction;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.utils.ReflectionsUtil;
import lombok.val;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class WrappedOutPlayerInfo extends NMSObject {
    private static String packet = Server.PLAYER_INFO;

    private static WrappedClass playerInfoClass = Reflections.getNMSClass(packet);
    private static WrappedClass playerInfoDataClass;
    private static WrappedConstructor constructor;
    private static WrappedClass chatBaseComp = Reflections.getNMSClass("IChatBaseComponent");
    private static WrappedClass chatSerialClass = Reflections.getNMSClass("IChatBaseComponent$ChatSerializer");

    private static WrappedMethod stcToComponent = chatSerialClass.getMethod("a", new Class[]{String.class});

    public WrappedOutPlayerInfo(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutPlayerInfo(WrappedEnumPlayerInfoAction action, Player player) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            val construct = playerInfoClass.getConstructor(WrappedEnumPlayerInfoAction.enumPlayerInfoAction.getParent(),
                    Array.newInstance(MinecraftReflection.entityPlayer.getParent(), 0).getClass());

            Object array = Array.newInstance(MinecraftReflection.entityPlayer.getParent(), 1);
            Array.set(array, 0, ReflectionsUtil.getEntityPlayer(player));
            setObject(construct.newInstance(action.toVanilla(), array));
        } else {
            Object packet = playerInfoClass.getConstructor().newInstance();
            playerInfoClass.getMethod(action.legacyMethodName, ReflectionsUtil.EntityPlayer)
                    .invoke(packet, ReflectionsUtil.getEntityPlayer(player));

            setObject(packet);
        }
    }

    //1.8+
    private static FieldAccessor<List> playerInfoListAccessor;
    private static FieldAccessor<Enum> actionAcessorEnum;

    //1.7.10
    private static FieldAccessor<Integer> actionAcessorInteger;
    private static FieldAccessor<Integer> gamemodeAccessor;
    private static FieldAccessor<Object> profileAcessor;
    private static FieldAccessor<Integer> pingAcessor;


    private List<WrappedPlayerInfoData> playerInfo = new ArrayList<>();
    private WrappedEnumPlayerInfoAction action;

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            playerInfoListAccessor = fetchField(packet, List.class, 0);
            actionAcessorEnum = fetchField(packet, Enum.class, 0);

            List list = fetch(playerInfoListAccessor);

            for (Object object : list) {
                playerInfo.add(new WrappedPlayerInfoData(object));
            }

            action = WrappedEnumPlayerInfoAction.valueOf(fetch(actionAcessorEnum).name());
        } else {
            actionAcessorInteger = fetchField(packet, Integer.class, 5);
            profileAcessor = fetchFieldByName(packet, "player", Object.class);
            gamemodeAccessor = fetchField(packet, Integer.class, 6);
            pingAcessor = fetchField(packet, Integer.class, 7);

            action = WrappedEnumPlayerInfoAction.values()[fetch(actionAcessorInteger)];

            WrappedGameProfile profile = new WrappedGameProfile(fetch(profileAcessor));
            WrappedEnumGameMode gamemode = WrappedEnumGameMode.getById(fetch(gamemodeAccessor));
            int ping = fetch(pingAcessor);
            playerInfo.add(new WrappedPlayerInfoData(profile, gamemode, ping));
        }
    }

    @Override
    public void updateObject() {

    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            playerInfoDataClass = Reflections.getNMSClass(packet + "$PlayerInfoData");
            //constructor = playerInfoDataClass.getConstructor(Object.class, int.class, Object.class, Object.class);
        }
    }
}
