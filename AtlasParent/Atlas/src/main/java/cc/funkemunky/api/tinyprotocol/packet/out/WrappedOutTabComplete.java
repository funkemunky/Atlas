package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.v1_13.WrappedSuggestions;
import lombok.Getter;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class WrappedOutTabComplete extends NMSObject {

    private static String packet = Server.TAB_COMPLETE;
    private static WrappedClass tabClass = Reflections.getNMSClass(packet);
    private static WrappedClass suggestionsClass = WrappedSuggestions.suggestionsClass;

    @Getter
    private String[] result;

    //Below 1.13
    private static WrappedField suggestionsAccessor;

    public WrappedOutTabComplete(Object object) {
        super(object);
    }

    public WrappedOutTabComplete(Object object, Player player) {
        super(object, player);
    }

    //For everything below 1.13. There will be 1.13+ support for this soon.
    public WrappedOutTabComplete(String[] result) {
        setPacket(packet, (Object) result);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            result = suggestionsAccessor.get(getObject());
        } else {
            //TODO Tomorrow
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            suggestionsAccessor = tabClass.getFieldByType(String[].class, 0);
        } else {
            suggestionsAccessor = tabClass.getFieldByType(suggestionsClass.getParent(), 0);
        }
    }
}
