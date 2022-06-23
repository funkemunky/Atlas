package cc.funkemunky.api.tinyprotocol.packet.login;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedGameProfile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Deprecated
public class WrappedPacketLoginInStart extends NMSObject {

    private static final WrappedClass packet = Reflections.getNMSClass(Login.LOGIN_START);
    private static final WrappedField gameProfileField = packet.getFields().get(0);

    private WrappedGameProfile gameProfile;

    public WrappedPacketLoginInStart(Object object) {
        super(object);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_19)) {
            gameProfile = new WrappedGameProfile((Object)fetch(gameProfileField));
            return;
        }

        String username = fetch(gameProfileField);
        UUID uuid = Bukkit.getOfflinePlayer(username).getUniqueId();

        this.gameProfile = new WrappedGameProfile(uuid, username);
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_19)) {
            set(gameProfileField, gameProfile.getObject());
            return;
        }

        set(gameProfileField, gameProfile.getName());
    }
}
