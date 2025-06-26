package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedChatComponent extends NMSObject {

    private static WrappedMethod getText = MinecraftReflection.iChatBaseComponent.getMethod("getText");

    public WrappedChatComponent(Object object) {
        super(object);
    }

    private String text = "";

    @Override
    public void process(Player player, ProtocolVersion version) {
        text = getText.invoke(getObject());
    }

    @Override
    public void updateObject() {

    }
}
