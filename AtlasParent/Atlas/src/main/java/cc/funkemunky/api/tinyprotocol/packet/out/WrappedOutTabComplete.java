package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.v1_13.DontImportIfNotLatestThanks;
import cc.funkemunky.api.tinyprotocol.packet.types.v1_13.WrappedSuggestions;
import org.bukkit.entity.Player;

public class WrappedOutTabComplete extends NMSObject {

    private static String packet = Server.TAB_COMPLETE;
    private static WrappedClass tabClass = Reflections.getNMSClass(packet);
    private static WrappedClass suggestionsClass = WrappedSuggestions.suggestionsClass;
    private static DontImportIfNotLatestThanks stuff;

    public String[] suggestions;

    //1.13 only
    public int id = -1;

    private static WrappedField suggestionsAccessor;

    //1.13 and above
    private static WrappedField idAccessor;

    public WrappedOutTabComplete(Object object) {
        super(object);
    }

    public WrappedOutTabComplete(Object object, Player player) {
        super(object, player);
    }

    //For everything below 1.13.
    public WrappedOutTabComplete(String... result) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            setPacket(packet, (Object) result);
        }
    }

    //For 1.13 and above
    public WrappedOutTabComplete(int id, String input, String... result) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            Object suggestions = stuff.getSuggestions(input, result);

            setPacket(packet, id, suggestions);
        }
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            //Getting suggestions.
            suggestions = suggestionsAccessor.get(getObject());
        } else {
            //Getting suggestions
            id = idAccessor.get(getObject());
            suggestions = stuff.getArrayFromSuggestions(suggestionsAccessor.get(getObject()));
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            suggestionsAccessor = tabClass.getFieldByType(String[].class, 0);
        } else {
            suggestionsAccessor = tabClass.getFieldByType(suggestionsClass.getParent(), 0);
            idAccessor = tabClass.getFieldByType(int.class, 0);
            stuff = new DontImportIfNotLatestThanks();
        }
    }
}
