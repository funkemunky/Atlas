package cc.funkemunky.api.utils;

import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockUtils {

    public static Map<Material, BoundingBox> collisionBoundingBoxes = new HashMap<>();

    @Deprecated
    public static Block getBlock(Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            return null;
        }
    }

    public static Optional<Block> getBlockAsync(Location location) {
        if(Bukkit.isPrimaryThread()
                || location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4))  {
            return Optional.of(location.getBlock());
        }

        return Optional.empty();
    }

    private static WrappedField fieldBlocksMovement;
    public static boolean blocksMovement(Block block) {
        return blocksMovement(block.getType());
    }

    public static boolean blocksMovement(Material material) {
        return material.isSolid();
    }


    public static Optional<Block> getRelativeAsync(Block block, BlockFace face) {
        return getRelativeAsync(block, face.getModX(), face.getModY(), face.getModZ());
    }

    public static Optional<Block> getRelativeAsync(Block block, BlockFace face, int distance) {
        return getRelativeAsync(block,
                face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    public static Optional<Block> getRelativeAsync(Block block, int modX, int modY, int modZ) {
        if(block == null) return Optional.empty();

        return getBlockAsync(block.getLocation().clone().add(modX, modY, modZ));
    }

    public static float getFriction(XMaterial material) {
        switch(material) {
            case SLIME_BLOCK:
                return 0.8f;
            case ICE:
            case PACKED_ICE:
            case FROSTED_ICE:
                return 0.98f;
            case BLUE_ICE:
                return 0.989f;
            default:
                return 0.6f;
        }
    }

    public static boolean isSolid(Block block) {
        return isSolid(block.getType());
    }

    public static boolean isSolid(Material material) {
        int type = material.getId();

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

    public static boolean isLiquid(Block block) {
        return block.getType().toString().contains("WATER") || block.getType().toString().contains("LAVA");
    }

    public static boolean isClimbableBlock(Block block) {
        return block.getType().toString().contains("LADDER") || block.getType().toString().contains("VINE");
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
        return block.getType().toString().contains("CHEST") || block.getType().toString().contains("SHULKER");
    }

    public static boolean isWall(Block block) {
        return block.getType().toString().contains("WALL");
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

    public static boolean isTool(ItemStack stack) {
        String name = stack.getType().name().toLowerCase();

        return name.contains("axe") || name.contains("spade") || name.contains("shovel") || name.contains("shear") || name.contains("sword");
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

    private static void setupCollisionBB() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            collisionBoundingBoxes.put(Material.getMaterial("FIRE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("STONE_PLATE"), new BoundingBox((float) 0.0625, (float) 0.0, (float) 0.0625, (float) 0.9375, (float) 0.0625, (float) 0.9375));
            collisionBoundingBoxes.put(Material.getMaterial("GRAVEL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("COBBLESTONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("NETHER_BRICK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("PUMPKIN"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("CARROT"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("TNT"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SOUL_SAND"), new BoundingBox(0f, 0f,0f, 1f, 0.875f, 1f));
            collisionBoundingBoxes.put(Material.getMaterial("SAND"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WOOD_PLATE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SIGN_POST"), new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 1.0, (float) 0.75));
            collisionBoundingBoxes.put(Material.getMaterial("COCOA"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DETECTOR_RAIL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("HARD_CLAY"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("NETHERRACK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("STONE_BUTTON"), new BoundingBox((float) 0.3125, (float) 0.0, (float) 0.375, (float) 0.6875, (float) 0.125, (float) 0.625));
            collisionBoundingBoxes.put(Material.getMaterial("CLAY"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("QUARTZ_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("HUGE_MUSHROOM_1"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("HUGE_MUSHROOM_2"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("LAVA"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("BEACON"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GRASS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DEAD_BUSH"), new BoundingBox((float) 0.09999999403953552, (float) 0.0, (float) 0.09999999403953552, (float) 0.8999999761581421, (float) 0.800000011920929, (float) 0.8999999761581421));
            collisionBoundingBoxes.put(Material.getMaterial("GLOWSTONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("ICE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("BRICK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_TORCH_ON"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_TORCH_OFF"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("POWERED_RAIL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DISPENSER"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("JUKEBOX"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("EMERALD_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("STONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("BOOKSHELF"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("MYCEL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("OBSIDIAN"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("PORTAL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GOLD_PLATE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("COAL_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GOLD_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("STAINED_CLAY"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("MOB_SPAWNER"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("BEDROCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("IRON_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SIGN"), new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 1.0, (float) 0.75));
            collisionBoundingBoxes.put(Material.getMaterial("IRON_PLATE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GOLD_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("POTATO"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("MOSSY_COBBLESTONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("RAILS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("HAY_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("TORCH"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("CARPET"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.0625, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DIRT"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("EMERALD_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_LAMP_ON"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_LAMP_OFF"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("NETHER_WARTS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SPONGE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WORKBENCH"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SANDSTONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("LAPIS_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("NOTE_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WOOL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("COMMAND"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("ENDER_STONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("TRIPWIRE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.15625, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SAPLING"), new BoundingBox((float) 0.09999999403953552, (float) 0.0, (float) 0.09999999403953552, (float) 0.8999999761581421, (float) 0.800000011920929, (float) 0.8999999761581421));
            collisionBoundingBoxes.put(Material.getMaterial("PACKED_ICE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("LAPIS_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SMOOTH_BRICK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("RED_MUSHROOM"), new BoundingBox((float) 0.30000001192092896, (float) 0.0, (float) 0.30000001192092896, (float) 0.699999988079071, (float) 0.4000000059604645, (float) 0.699999988079071));
            collisionBoundingBoxes.put(Material.getMaterial("BROWN_MUSHROOM"), new BoundingBox((float) 0.30000001192092896, (float) 0.0, (float) 0.30000001192092896, (float) 0.699999988079071, (float) 0.4000000059604645, (float) 0.699999988079071));
            collisionBoundingBoxes.put(Material.getMaterial("DIAMOND_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("CROPS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("IRON_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("MELON"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DIAMOND_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("LEVER"), new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 0.6000000238418579, (float) 0.75));
            collisionBoundingBoxes.put(Material.getMaterial("SUGAR_CANE"), new BoundingBox((float) 0.125, (float) 0.0, (float) 0.125, (float) 0.875, (float) 1.0, (float) 0.875));
            collisionBoundingBoxes.put(Material.getMaterial("COAL_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WATER_LILY"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.015625, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("QUARTZ_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GLASS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("TRIPWIRE_HOOK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("VINE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WEB"), new BoundingBox(0, 0, 0, 1, 1, 1));
            collisionBoundingBoxes.put(Material.getMaterial("WATER"), new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
            collisionBoundingBoxes.put(Material.getMaterial("STATIONARY_WATER"), new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
            collisionBoundingBoxes.put(Material.getMaterial("STATIONARY_LAVA"), new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
        }
    }

    static {
        setupCollisionBB();

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            fieldBlocksMovement = MinecraftReflection.classBlockInfo.getFieldByType(boolean.class, 0);
        }
    }
}

