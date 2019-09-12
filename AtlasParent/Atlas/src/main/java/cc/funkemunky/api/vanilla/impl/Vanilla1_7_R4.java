package cc.funkemunky.api.vanilla.impl;

import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.vanilla.Vanilla;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class Vanilla1_7_R4 implements Vanilla {
    @Override
    public List<BoundingBox> getCollidingBoxes(World world, BoundingBox box) {
        return null;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Block block) {
        return null;
    }

    @Override
    public List<BoundingBox> getCollidingBoxes(Entity entity, BoundingBox box) {
        return null;
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_7_R4.World world = ((org.bukkit.craftbukkit.v1_7_R4.CraftWorld) loc.getWorld()).getHandle();

        return !world.isStatic && world.isLoaded(loc.getBlockX(), 0, loc.getBlockZ()) && world.getChunkAtWorldCoords(loc.getBlockX(), loc.getBlockZ()).d;
    }

    //If an item is in use
    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_7_R4.EntityHuman entity = ((org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity) player).getHandle();
        return entity.bF() != null && entity.bF().getItem().d(entity.bF()) != net.minecraft.server.v1_7_R4.EnumAnimation.NONE;
    }



    //Riptiding is not a thing in this version.
    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    //This is the base movement factor of a player.
    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
    }

    //This gets the friction of a block. The lower the number, the slower the movement.
    //Used in factoring in movement of an entity.
    @Override
    public float getFrictionFactor(Block block) {
        return 0;
    }

    @Override
    public long getPing(Player player) {
        return 0;
    }

    @Override
    public double getTPS() {
        return 0;
    }
}
