package cc.funkemunky.api.utils.world.types;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumParticle;
import cc.funkemunky.api.utils.world.CollisionBox;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class DynamicCollisionBox implements CollisionBox {

    private CollisionFactory box;
    @Setter
    private Block block;
    @Setter
    private ProtocolVersion version;
    private double x,y,z;

    public DynamicCollisionBox(CollisionFactory box, Block block, ProtocolVersion version) {
        this.box = box;
        this.block = block;
        this.version = version;
    }

    @Override
    public boolean isCollided(CollisionBox other) {
        return box.fetch(version, block).offset(x,y,z).isCollided(other);
    }

    @Override
    public boolean isIntersected(CollisionBox other) {
        return box.fetch(version, block).offset(x, y, z).isIntersected(other);
    }

    @Override
    public void draw(WrappedEnumParticle particle, Collection<? extends Player> players) {
        box.fetch(version, block).offset(x,y,z).draw(particle,players);
    }

    @Override
    public DynamicCollisionBox copy() {
        return new DynamicCollisionBox(box,block,version).offset(x,y,z);
    }

    @Override
    public DynamicCollisionBox offset(double x, double y, double z) {
        this.x+=x;
        this.y+=y;
        this.z+=z;
        return this;
    }

    @Override
    public DynamicCollisionBox shrink(double x, double y, double z) {
        return this;
    }

    @Override
    public DynamicCollisionBox expand(double x, double y, double z) {
        return this;
    }

    @Override
    public void downCast(List<SimpleCollisionBox> list) {
        box.fetch(version,block).offset(x,y,z).downCast(list);
    }

    @Override
    public boolean isNull() {
        return box.fetch(version,block).isNull();
    }
}