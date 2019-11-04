package cc.funkemunky.api.reflection;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedEnumAnimation;
import cc.funkemunky.api.utils.BoundingBox;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MinecraftReflection {
    public static WrappedClass entity = Reflections.getNMSClass("Entity");
    public static WrappedClass axisAlignedBB = Reflections.getNMSClass("AxisAlignedBB");
    public static WrappedClass entityHuman = Reflections.getNMSClass("EntityHuman");
    public static WrappedClass block = Reflections.getNMSClass("Block");
    public static WrappedClass iBlockData;
    public static WrappedClass world = Reflections.getNMSClass("World");
    public static WrappedClass worldServer = Reflections.getNMSClass("WorldServer");
    public static WrappedClass playerInventory = Reflections.getNMSClass("PlayerInventory");
    public static WrappedClass itemStack = Reflections.getNMSClass("ItemStack");
    public static WrappedClass enumAnimation = Reflections.getNMSClass("EnumAnimation");
    public static WrappedClass chunk = Reflections.getNMSClass("Chunk");

    //BoundingBoxes
    public static WrappedMethod getCubes;
    public static WrappedField aBB = axisAlignedBB.getFieldByName("a");
    public static WrappedField bBB = axisAlignedBB.getFieldByName("b");
    public static WrappedField cBB = axisAlignedBB.getFieldByName("c");
    public static WrappedField dBB = axisAlignedBB.getFieldByName("d");
    public static WrappedField eBB = axisAlignedBB.getFieldByName("e");
    public static WrappedField fBB = axisAlignedBB.getFieldByName("f");
    public static WrappedConstructor aabbConstructor;
    public static WrappedMethod idioticOldStaticConstructorAABB;
    public static WrappedField entityBoundingBox = entity.getFirstFieldByType(axisAlignedBB.getParent());

    //ItemStack methods and fields
    public static WrappedMethod enumAnimationStack;

    //1.13+ only
    public static WrappedClass voxelShape;
    public static WrappedClass worldReader;
    public static WrappedMethod getCubesFromVoxelShape;

    //Blocks
    public static WrappedMethod addCBoxes;
    public static WrappedClass blockPos;
    public static WrappedConstructor blockPosConstructor;
    public static WrappedMethod getBlockData;
    public static WrappedField chunkProvider = MinecraftReflection.world
            .getFieldByType(Reflections.getNMSClass("IChunkProvider").getParent(), 0);
    public static WrappedField chunksList = Reflections.getNMSClass("ChunkProviderServer")
            .getFieldByName("chunks");

    public static WrappedEnumAnimation getArmAnimation(HumanEntity entity) {
        if(entity.getItemInHand() != null) {
            return getItemAnimation(entity.getItemInHand());
        }
        return WrappedEnumAnimation.NONE;
    }

    public static WrappedEnumAnimation getItemAnimation(ItemStack stack) {
        Object itemStack = CraftReflection.getVanillaItemStack(stack);

        return WrappedEnumAnimation.fromNMS(enumAnimationStack.invoke(itemStack));
    }

    public static List<BoundingBox> getBlockBox(@Nullable Entity entity, Block block) {
        Object vanillaBlock = CraftReflection.getVanillaBlock(block);
        Object world = CraftReflection.getVanillaWorld(block.getWorld());

        //TODO Use increasedHeight if it doesnt get fence or wall boxes properly.
        //boolean increasedHeight = BlockUtils.isFence(block) || BlockUtils.isWall(block);
        //We do this so we can get the block inside
        BoundingBox box = new BoundingBox(
                block.getLocation().toVector(),
                block.getLocation().clone()
                        .add(1,1,1)
                        .toVector());

        List<Object> aabbs = new ArrayList<>();

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            addCBoxes.invoke(vanillaBlock, world,
                    block.getX(), block.getY(), block.getZ(),
                    box.toAxisAlignedBB(), aabbs,
                    entity != null ? CraftReflection.getEntity(entity) : null); //Entity is always null for these
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            BaseBlockPosition blockPos = new BaseBlockPosition(block.getX(), block.getY(), block.getZ());
            Object blockData = getBlockData.invoke(vanillaBlock);

            addCBoxes.invoke(vanillaBlock, world, blockPos.getAsBlockPosition(), blockData,
                    box.toAxisAlignedBB(), aabbs, entity != null ? CraftReflection.getEntity(entity) : null); //Entity is always null for these
        }

        return aabbs.stream().map(MinecraftReflection::fromAABB).collect(Collectors.toList());
    }

    //1.7 field is boundingBox
    //1.8+ method is getBoundingBox.
    public static BoundingBox getEntityBoundingBox(Entity entity) {
        Object vanillaEntity = CraftReflection.getEntity(entity);

        return fromAABB(entityBoundingBox.get(vanillaEntity));
    }

    public static List<BoundingBox> getCollidingBoxes(@Nullable Entity entity, World world, BoundingBox box) {
        Object vWorld = CraftReflection.getVanillaWorld(world);
        List<BoundingBox> boxes = new ArrayList<>();
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            List<Object> aabbs = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)
                    ? getCubes.invoke(vWorld, entity != null ? CraftReflection.getEntity(entity) : null, box.toAxisAlignedBB())
                    : getCubes.invoke(vWorld, box.toAxisAlignedBB(), false, entity != null ? CraftReflection.getEntity(entity) : null);

            boxes = aabbs.stream().map(MinecraftReflection::fromAABB).collect(Collectors.toList());
        } else {
            Object voxelShape = getCubes.invoke(vWorld, null, box.toAxisAlignedBB(), 0D, 0D, 0D);

            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13_2)) {
                List<Object> aabbs = getCubesFromVoxelShape.invoke(voxelShape);

                boxes = aabbs.stream().map(MinecraftReflection::fromAABB).collect(Collectors.toList());
            } else {
                List<Object> aabbs = new ArrayList<>();

                ((List<Object>) voxelShape).stream()
                        .map(ob -> {
                            List<Object> aabbList = getCubesFromVoxelShape.invoke(ob);
                            return aabbList;
                        }).forEach(aabbs::addAll);

               boxes = aabbs.stream().map(MinecraftReflection::fromAABB).collect(Collectors.toList());
            }
        }
        return boxes;
    }

    //a, b, c is minX, minY, minZ
    //d, e, f is maxX, maxY, maxZ
    public static BoundingBox fromAABB(Object aabb) {
        double a, b, c, d, e, f;

        a = aBB.get(aabb);
        b = bBB.get(aabb);
        c = cBB.get(aabb);
        d = dBB.get(aabb);
        e = eBB.get(aabb);
        f = fBB.get(aabb);

        return new BoundingBox((float) a,(float) b,(float) c,(float) d,(float) e,(float) f);
    }

    public static <T> T toAABB(BoundingBox box) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            return idioticOldStaticConstructorAABB
                    .invoke(null,
                            (double)box.minX, (double)box.minY, (double)box.minZ,
                            (double)box.maxX, (double)box.maxY, (double)box.maxZ);
        } else return aabbConstructor
                .newInstance((double)box.minX, (double)box.minY, (double)box.minZ,
                (double)box.maxX, (double)box.maxY, (double)box.maxZ);
    }

    //Either bukkit or vanilla world object can be used.
    public static <T> T getChunkProvider(Object world) {
        Object vanillaWorld;
        if(world instanceof World) {
            vanillaWorld = CraftReflection.getVanillaWorld((World)world);
        } else vanillaWorld = world;

        return chunkProvider.get(vanillaWorld);
    }

    public static <T> List<T> getVanillaChunks(Object provider) {
        return chunksList.get(provider);
    }

    static {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
            blockPos = Reflections.getNMSClass("BlockPosition");
            blockPosConstructor = blockPos.getConstructor(int.class, int.class, int.class);
            getBlockData = block.getMethod("getBlockData");
            aabbConstructor = axisAlignedBB
                    .getConstructor(double.class, double.class, double.class, double.class, double.class, double.class);
            iBlockData = Reflections.getNMSClass("IBlockData");
        } else {
            idioticOldStaticConstructorAABB = axisAlignedBB.getMethod("a",
                    double.class, double.class, double.class, double.class, double.class, double.class);
        }
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) {
            getCubes = world.getMethod("getCubes", entity.getParent(), axisAlignedBB.getParent());

            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
                //1.7.10 does not have the BlockPosition object yet.
                addCBoxes = block.getMethod("a", world.getParent(), int.class, int.class, int.class,
                        axisAlignedBB.getParent(), List.class, entity.getParent());
            } else {
                addCBoxes = block.getMethod("a", world.getParent(), blockPos.getParent(), iBlockData.getParent(),
                        axisAlignedBB.getParent(), List.class, entity.getParent());
            }
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            getCubes = world.getMethod("a", entity.getParent(), axisAlignedBB.getParent(), boolean.class, List.class);
            addCBoxes = block.getMethod("a", world.getParent(), blockPos.getParent(), iBlockData.getParent(),
                    axisAlignedBB.getParent(), List.class, entity.getParent());
        } else {
            worldReader = Reflections.getNMSClass("IWorldReader");
            //1.13 and 1.13.1 returns just VoxelShape while 1.13.2+ returns a Stream<VoxelShape>
            getCubes = worldReader.getMethod("a", entity.getParent(), axisAlignedBB.getParent(),
                    double.class, double.class, double.class);
            voxelShape = Reflections.getNMSClass("VoxelShape");
            getCubesFromVoxelShape = voxelShape.getMethodByType(List.class, 0);
        }
        try {
            enumAnimationStack = itemStack.getMethodByType(enumAnimation.getParent(), 0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
