package cc.funkemunky.api.utils.world.types;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.WrappedBlock;
import org.bukkit.Location;

public interface CollisionFactory {
    CollisionBox fetch(ProtocolVersion version, WrappedBlock block);
}