package cc.funkemunky.api.abstraction.impl;

import cc.funkemunky.api.abstraction.AbstractVersion;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class AbstractV1_9 implements AbstractVersion {

    @Override
    public CollisionBox getBlockBox(Block block) {
        return null;
    }

    @Override
    public List<CollisionBox> getCollidingBoxes(World world, SimpleCollisionBox box) {
        return null;
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isGliding(Player player) {
        return false;
    }

    @Override
    public boolean isChunkLoaded(Location location) {
        return false;
    }

    @Override
    public float getMovementFactor(LivingEntity entity) {
        return 0;
    }

    @Override
    public float getBlockFriction(Block block) {
        return 0;
    }
}
