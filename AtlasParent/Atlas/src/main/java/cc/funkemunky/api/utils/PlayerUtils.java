package cc.funkemunky.api.utils;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtils {

    private static Enchantment DEPTH;

    public static int getDepthStriderLevel(Player player) {
        if(DEPTH == null) return 0;

        val boots = player.getInventory().getBoots();

        if(boots == null) return 0;

        return boots.getEnchantmentLevel(DEPTH);
    }

    public static boolean hasBlocksAround(Location loc) {
        Location one = loc.clone().subtract(1, 0, 1), two = loc.clone().add(1, 1, 1);

        int minX = Math.min(one.getBlockX(), two.getBlockX()), minY = Math.min(one.getBlockY(), two.getBlockY()), minZ = Math.min(one.getBlockZ(), two.getBlockZ());
        int maxX = Math.max(one.getBlockX(), two.getBlockX()), maxY = Math.max(one.getBlockY(), two.getBlockY()), maxZ = Math.max(one.getBlockZ(), two.getBlockZ());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Location blockLoc = new Location(loc.getWorld(), x, y, z);

                    if (BlockUtils.isSolid(BlockUtils.getBlock(blockLoc))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean facingOpposite(Entity one, Entity two) {
        return one.getLocation().getDirection().distance(two.getLocation().getDirection()) < 0.5;
    }

    public static boolean isGliding(Player p) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) return false;

        boolean isGliding = false;
        try {
            isGliding = (boolean) p.getClass().getMethod("isGliding", new Class[0]).invoke(p, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isGliding;
    }

    public static double getAccurateDistance(LivingEntity attacked, LivingEntity entity) {
        Location origin = attacked.getEyeLocation(), point;
        if (entity.getLocation().getY() > attacked.getLocation().getBlockY()) {
            point = entity.getLocation();
        } else {
            point = entity.getEyeLocation();
        }


        return origin.distance(point);
    }

    public static double getAccurateDistance(Location origin, Location point) {
        return origin.distance(point) * Math.cos(origin.getPitch());
    }

    public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (!pe.getType().getName().equals(pet.getName())) continue;
            return pe.getAmplifier() + 1;
        }
        return 0;
    }

    public static float getJumpHeight(Player player) {
        float baseHeight = 0.42f;

        if(player.hasPotionEffect(PotionEffectType.JUMP)) {
            baseHeight+= PlayerUtils.getPotionEffectLevel(player, PotionEffectType.JUMP) * 0.1f;
        }

        return baseHeight;
    }

    static {
        try {
            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
                DEPTH = Enchantment.getByName("DEPTH_STRIDER");
            }
        } catch(Exception e) {
            DEPTH = null;
        }
    }
}