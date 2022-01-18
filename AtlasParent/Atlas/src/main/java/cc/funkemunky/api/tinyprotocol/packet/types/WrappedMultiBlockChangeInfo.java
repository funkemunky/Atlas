package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.math.IntVector;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedMultiBlockChangeInfo extends NMSObject {

    private static final WrappedClass objectClass = Reflections
            .getNMSClass("PacketPlayOutMultiBlockChange.MultiBlockChangeInfo");

    private static final WrappedField fieldPos = fetchField(objectClass, short.class, 0),
            fieldBlockInfo = fetchField(objectClass, MinecraftReflection.iBlockData.getParent(), 0);

    public WrappedMultiBlockChangeInfo(Object object) {
        super(object);
    }

    private IntVector blockPos;
    private XMaterial material;
    private byte data;

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        short pos = fetch(fieldPos);

        blockPos = new IntVector(pos >> 12 & 15, pos & 255, pos >> 8 & 15);

        Object iBlockData = fetch(fieldBlockInfo);
        material = XMaterial.matchXMaterial(MinecraftReflection
                .getMaterialFromVanillaBlock(MinecraftReflection
                        .getBlockFromData(iBlockData)));
        data = MinecraftReflection.toLegacyData(material.parseMaterial(), iBlockData);
    }
}
