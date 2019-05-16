package cc.funkemunky.api.utils;

import cc.funkemunky.api.Atlas;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BlockUtils {

    public static Map<Material, BoundingBox> collisionBoundingBoxes;

    public BlockUtils() {
        collisionBoundingBoxes = new HashMap<>();

        setupCollisionBB();
    }

    public static Block getBlock(Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(Atlas.getInstance(), futureTask);
            try {
                return futureTask.get(4, TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
               return null;
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static boolean isSolid(Block block) {
        int type = block.getType().getId();

        switch (type) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 7:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 29:
            case 34:
            case 33:
            case 35:
            case 36:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 52:
            case 53:
            case 54:
            case 56:
            case 57:
            case 58:
            case 60:
            case 61:
            case 62:
            case 64:
            case 65:
            case 67:
            case 71:
            case 73:
            case 74:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 116:
            case 117:
            case 118:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 144:
            case 145:
            case 146:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
            case 216:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 249:
            case 250:
            case 251:
            case 252:
            case 255:
            case 397:
            case 355:
                return true;

        }
        return false;
    }

    public static List<BoundingBox> getBlockBoundingBox(Block block) {
        List<BoundingBox> boxes = Atlas.getInstance().getBlockBoxManager().getBlockBox().getSpecificBox(block.getLocation());

        for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i).getMaximum().length() == boxes.get(i).getMinimum().length()) {
                boxes.remove(i);
                boxes.add(collisionBoundingBoxes.getOrDefault(block.getType(), new BoundingBox(0, 0, 0, 1, 1, 1)).add(block.getLocation().toVector()));
            }
        }
        return boxes;
    }

    public static boolean isLiquid(Block block) {
        return block.getType().toString().contains("WATER") || block.getType().toString().contains("LAVA");
    }

    public static boolean isClimbableBlock(Block block) {
        return block.getType() == Material.LADDER || block.getType() == Material.VINE;
    }

    public static boolean isIce(Block block) {
        return block.getType().toString().contains("ICE");
    }

    public static boolean isFence(Block block) {
        return (block.getType().toString().contains("FENCE") && !block.getType().toString().contains("GATE")) | block.getType().toString().contains("WALL");
    }

    public static boolean isDoor(Block block) {
        return block.getType().toString().contains("DOOR") && !block.getType().toString().contains("TRAP");
    }

    public static boolean isBed(Block block) {
        return block.getType().toString().contains("BED");
    }

    public static boolean isTrapDoor(Block block) {
        return block.getType().toString().contains("DOOR") && block.getType().toString().contains("TRAP");
    }

    public static boolean isChest(Block block) {
        return block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST) || block.getType().equals(Material.ENDER_CHEST) || block.getType().toString().contains("SHULKER");
    }

    public static boolean isPiston(Block block) {
        return block.getType().getId() == 36 || block.getType().getId() == 34 || block.getType().getId() == 33 || block.getType().getId() == 29;
    }

    public static boolean isFenceGate(Block block) {
        return block.getType().toString().contains("FENCE") && block.getType().toString().contains("GATE");
    }

    public static boolean isStair(Block block) {
        return block.getType().toString().contains("STAIR");
    }

    public static boolean isSlab(Block block) {
        return block.getType().toString().contains("STEP") || block.getType().toString().contains("SLAB");
    }

    public static boolean isEdible(Material material) {
        return material.equals(Material.COOKED_BEEF) || material.equals(Material.COOKED_CHICKEN) || material.getId() == 350 || material.getId() == 424 || material.getId() == 412 || material.equals(Material.ROTTEN_FLESH) || material.getId() == 391 || material.equals(Material.CARROT) || material.equals(Material.GOLDEN_APPLE) || material.equals(Material.GOLDEN_CARROT) || material.getId() == 320 || material.getId() == 363 || material.getId() == 365 || material.getId() == 349 || material.equals(Material.SPIDER_EYE) || material.equals(Material.getMaterial("BEETROOT_SOUP")) || material.getId() == 282 || material.getId() == 392 || material.equals(Material.BAKED_POTATO) || material.equals(Material.POISONOUS_POTATO) || material.equals(Material.PUMPKIN_PIE) || material.equals(Material.APPLE) || material.getId() == 423 || material.getId() == 411 || material.equals(Material.MELON) || material.equals(Material.getMaterial("CHORUS_FRUIT")) || material.equals(Material.COOKIE) || material.equals(Material.POTION);
    }

    public static boolean isTool(ItemStack stack) {
        String name = stack.getType().name().toLowerCase();

        return name.contains("axe") || name.contains("spade") || name.contains("shovel") || name.contains("shear") || name.contains("sword");
    }

    public static List<Block> getBlocks(BoundingBox box, World world) {
        List<Block> block = new ArrayList<>();

        Atlas.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(world, box).forEach(box2 -> BlockUtils.getBlock(box2.getMinimum().toLocation(world)));
        return block;
    }

    public static Location findGround(World world, Location point) {
        for (int y = point.toVector().getBlockY(); y > 0; y--) {
            Location loc = new Location(world, point.getX(), y, point.getZ());
            Block block = BlockUtils.getBlock(loc);

            if (block.getType().isBlock() && block.getType().isSolid() && !block.isEmpty()) {
                Location toReturn = loc.clone();

                toReturn.setY(y + 1);

                return toReturn;
            }
        }
        return point;
    }

    private void setupCollisionBB() {
        collisionBoundingBoxes.put(Material.FIRE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.STONE_PLATE, new BoundingBox((float) 0.0625, (float) 0.0, (float) 0.0625, (float) 0.9375, (float) 0.0625, (float) 0.9375));
        collisionBoundingBoxes.put(Material.GRAVEL, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.COBBLESTONE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.NETHER_BRICK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.PUMPKIN, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.CARROT, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
        collisionBoundingBoxes.put(Material.TNT, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.SAND, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.WOOD_PLATE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.SIGN_POST, new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 1.0, (float) 0.75));
        collisionBoundingBoxes.put(Material.COCOA, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.DETECTOR_RAIL, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
        collisionBoundingBoxes.put(Material.HARD_CLAY, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.NETHERRACK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.STONE_BUTTON, new BoundingBox((float) 0.3125, (float) 0.0, (float) 0.375, (float) 0.6875, (float) 0.125, (float) 0.625));
        collisionBoundingBoxes.put(Material.CLAY, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.QUARTZ_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.HUGE_MUSHROOM_1, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.HUGE_MUSHROOM_2, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.LAVA, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.BEACON, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.GRASS, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.DEAD_BUSH, new BoundingBox((float) 0.09999999403953552, (float) 0.0, (float) 0.09999999403953552, (float) 0.8999999761581421, (float) 0.800000011920929, (float) 0.8999999761581421));
        collisionBoundingBoxes.put(Material.GLOWSTONE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.ICE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.BRICK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.REDSTONE_TORCH_ON, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.REDSTONE_TORCH_OFF, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.POWERED_RAIL, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
        collisionBoundingBoxes.put(Material.DISPENSER, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.JUKEBOX, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.EMERALD_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.STONE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.BOOKSHELF, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.MYCEL, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.OBSIDIAN, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.PORTAL, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.GOLD_PLATE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.COAL_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.GOLD_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.STAINED_CLAY, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.MOB_SPAWNER, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.BEDROCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.IRON_ORE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.REDSTONE_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.SIGN, new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 1.0, (float) 0.75));
        collisionBoundingBoxes.put(Material.IRON_PLATE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.GOLD_ORE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.POTATO, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
        collisionBoundingBoxes.put(Material.MOSSY_COBBLESTONE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.RAILS, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
        collisionBoundingBoxes.put(Material.HAY_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.TORCH, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.CARPET, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.0625, (float) 1.0));
        collisionBoundingBoxes.put(Material.DIRT, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.EMERALD_ORE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.REDSTONE_LAMP_ON, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.REDSTONE_LAMP_OFF, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.NETHER_WARTS, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
        collisionBoundingBoxes.put(Material.SPONGE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.WORKBENCH, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.SANDSTONE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.LAPIS_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.NOTE_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.WOOL, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.COMMAND, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.ENDER_STONE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.TRIPWIRE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.15625, (float) 1.0));
        collisionBoundingBoxes.put(Material.SAPLING, new BoundingBox((float) 0.09999999403953552, (float) 0.0, (float) 0.09999999403953552, (float) 0.8999999761581421, (float) 0.800000011920929, (float) 0.8999999761581421));
        collisionBoundingBoxes.put(Material.PACKED_ICE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.LAPIS_ORE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.SMOOTH_BRICK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.RED_MUSHROOM, new BoundingBox((float) 0.30000001192092896, (float) 0.0, (float) 0.30000001192092896, (float) 0.699999988079071, (float) 0.4000000059604645, (float) 0.699999988079071));
        collisionBoundingBoxes.put(Material.BROWN_MUSHROOM, new BoundingBox((float) 0.30000001192092896, (float) 0.0, (float) 0.30000001192092896, (float) 0.699999988079071, (float) 0.4000000059604645, (float) 0.699999988079071));
        collisionBoundingBoxes.put(Material.DIAMOND_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.CROPS, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
        collisionBoundingBoxes.put(Material.IRON_BLOCK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.MELON, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.DIAMOND_ORE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.LEVER, new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 0.6000000238418579, (float) 0.75));
        collisionBoundingBoxes.put(Material.SUGAR_CANE, new BoundingBox((float) 0.125, (float) 0.0, (float) 0.125, (float) 0.875, (float) 1.0, (float) 0.875));
        collisionBoundingBoxes.put(Material.COAL_ORE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.WATER_LILY, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.015625, (float) 1.0));
        collisionBoundingBoxes.put(Material.QUARTZ_ORE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.GLASS, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.TRIPWIRE_HOOK, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.VINE, new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
        collisionBoundingBoxes.put(Material.WEB, new BoundingBox(0, 0, 0, 1, 1, 1));
        collisionBoundingBoxes.put(Material.WATER, new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
        collisionBoundingBoxes.put(Material.getMaterial("STATIONARY_WATER"), new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
        collisionBoundingBoxes.put(Material.getMaterial("STATIONARY_LAVA"), new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
    }
}

