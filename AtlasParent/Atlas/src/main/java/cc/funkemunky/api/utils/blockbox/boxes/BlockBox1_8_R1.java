package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBox1_8_R1 implements BlockBox {

    @Override
    public boolean isChunkLoaded(Location loc) {

        net.minecraft.server.v1_8_R1.World world =
                ((org.bukkit.craftbukkit.v1_8_R1.CraftWorld) loc.getWorld()).getHandle();

        return !world.isStatic
                && world.isLoaded(
                        new net.minecraft.server.v1_8_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()))
                && world.getChunkAtWorldCoords(
                        new net.minecraft.server.v1_8_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).o();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
    }
}
