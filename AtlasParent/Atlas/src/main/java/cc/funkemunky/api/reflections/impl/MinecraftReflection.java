package cc.funkemunky.api.reflections.impl;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumAnimation;
import cc.funkemunky.api.utils.BoundingBox;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MinecraftReflection {
    public static WrappedClass entity = Reflections.getNMSClass("Entity");
    public static WrappedClass axisAlignedBB = Reflections.getNMSClass("AxisAlignedBB");
    public static WrappedClass entityHuman = Reflections.getNMSClass("EntityHuman");
    public static WrappedClass entityLiving = Reflections.getNMSClass("EntityLiving");
    public static WrappedClass block = Reflections.getNMSClass("Block");
    public static WrappedClass iBlockData;
    public static WrappedClass itemClass = Reflections.getNMSClass("Item");
    public static WrappedClass world = Reflections.getNMSClass("World");
    public static WrappedClass worldServer = Reflections.getNMSClass("WorldServer");
    public static WrappedClass playerInventory = Reflections.getNMSClass("PlayerInventory");
    public static WrappedClass itemStack = Reflections.getNMSClass("ItemStack");
    public static WrappedClass enumAnimation = Reflections.getNMSClass("EnumAnimation");
    public static WrappedClass chunk = Reflections.getNMSClass("Chunk");
    public static WrappedClass minecraftServer = Reflections.getNMSClass("MinecraftServer");
    public static WrappedClass entityPlayer = Reflections.getNMSClass("EntityPlayer");
    public static WrappedClass playerConnection = Reflections.getNMSClass("PlayerConnection");
    public static WrappedClass networkManager = Reflections.getNMSClass("NetworkManager");
    public static WrappedClass serverConnection = Reflections.getNMSClass("ServerConnection");
    private static WrappedClass gameProfile = Reflections.getClass("com.mojang.authlib.GameProfile");
    private static WrappedClass propertyMap = Reflections.getClass("com.mojang.authlib.properties.PropertyMap");
    public static WrappedClass iChatBaseComponent = Reflections.getNMSClass("IChatBaseComponent");

    private static WrappedMethod getProfile = CraftReflection.craftPlayer.getMethod("getProfile");
    private static WrappedMethod getProperties = gameProfile.getMethod("getProperties");
    private static WrappedMethod removeAll = propertyMap.getMethod("removeAll", String.class);
    private static WrappedMethod putAll = propertyMap.getMethod("putAll", String.class, Collection.class);
    private static WrappedMethod worldGetType;
    //BoundingBoxes
    private static WrappedMethod getCubes;
    private static WrappedField aBB = axisAlignedBB.getFieldByName("a");
    private static WrappedField bBB = axisAlignedBB.getFieldByName("b");
    private static WrappedField cBB = axisAlignedBB.getFieldByName("c");
    private static WrappedField dBB = axisAlignedBB.getFieldByName("d");
    private static WrappedField eBB = axisAlignedBB.getFieldByName("e");
    private static WrappedField fBB = axisAlignedBB.getFieldByName("f");
    private static WrappedConstructor aabbConstructor;
    private static WrappedMethod idioticOldStaticConstructorAABB;
    private static WrappedField entityBoundingBox = entity.getFirstFieldByType(axisAlignedBB.getParent());

    //ItemStack methods and fields
    private static WrappedMethod enumAnimationStack;
    private static WrappedField activeItemField;
    private static WrappedMethod getItemMethod = itemStack.getMethod("getItem");
    private static WrappedMethod getAnimationMethod = itemClass.getMethodByType(enumAnimation.getParent(), 0);
    private static WrappedMethod canDestroyMethod;

    //1.13+ only
    private static WrappedClass voxelShape;
    private static WrappedClass worldReader;
    private static WrappedMethod getCubesFromVoxelShape;

    private static WrappedField pingField = entityPlayer.getFieldByName("ping");

    //Blocks
    private static WrappedMethod addCBoxes;
    public static WrappedClass blockPos;
    private static WrappedConstructor blockPosConstructor;
    private static WrappedMethod getBlockData;
    private static WrappedField blockData = block.getFieldByName("blockData");
    private static WrappedField frictionFactor = block.getFieldByName("frictionFactor");
    private static WrappedField strength = block.getFieldByName("strength");
    private static WrappedField chunkProvider = MinecraftReflection.world
            .getFieldByType(Reflections.getNMSClass("IChunkProvider").getParent(), 0);
    private static WrappedField chunksList = Reflections.getNMSClass("ChunkProviderServer")
            .getFieldByName("chunks");

    //Entity Player fields
    private static WrappedField connectionField = entityPlayer.getFieldByName("playerConnection");
    private static WrappedField connectionNetworkField = playerConnection
            .getFieldByType(networkManager.getParent(), 0);
    private static WrappedField networkChannelField = networkManager.getFieldByName("channel");

    //General Fields
    private static WrappedField primaryThread = minecraftServer.getFirstFieldByType(Thread.class);

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

    public static <T> T getGameProfile(Player player) {
       return getProfile.invoke(player);
    }

    //1.7 field is boundingBox
    //1.8+ method is getBoundingBox.
    public static <T> T getEntityBoundingBox(Entity entity) {
        Object vanillaEntity = CraftReflection.getEntity(entity);

        return entityBoundingBox.get(vanillaEntity);
    }

    public static <T> T getItemInUse(HumanEntity entity) {
        Object humanEntity = CraftReflection.getEntityHuman(entity);
        return activeItemField.get(humanEntity);
    }

    //Can use either a Bukkit or vanilla object
    public static <T> T getItemFromStack(Object object) {
        Object vanillaStack;
        if(object instanceof ItemStack) {
            vanillaStack = CraftReflection.getVanillaItemStack((ItemStack)object);
        } else vanillaStack = object;

        return getItemMethod.invoke(vanillaStack);
    }

    //Can use either a Bukkit or vanilla object
    public static <T> T getItemAnimation(Object object) {
        Object vanillaStack;
        if(object instanceof ItemStack) {
            vanillaStack = CraftReflection.getVanillaItemStack((ItemStack)object);
        } else vanillaStack = object;

        Object item = getItemFromStack(vanillaStack);

        return getAnimationMethod.invoke(item, vanillaStack);
    }

    /* Checks if the player is able to destroy a block. Input can be NMS Block or Bukkit Block */
    public static boolean canDestroyBlock(Player player, Object block) {
        Object inventory = CraftReflection.getVanillaInventory(player);
        Object vBlock;
        if(block instanceof Block) {
            vBlock = CraftReflection.getVanillaBlock((Block)block);
        } else vBlock = block;

        return canDestroyMethod.invoke(inventory,
                ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)
                        ? blockData.get(vBlock) : vBlock);
    }

    /* Gets the friction of a block. Input can be NMS Block or Bukkit Block. */
    public static float getFriction(Object block) {
        Object vBlock;
        if(block instanceof Block) {
            vBlock = CraftReflection.getVanillaBlock((Block)block);
        } else vBlock = block;

        return frictionFactor.get(vBlock);
    }

    public static int getPing(Player player) {
        return pingField.get(CraftReflection.getEntityPlayer(player));
    }

    /* Gets the amount of mining required to break a block. Input can be NMS Block or Bukkit Block. */
    public static float getBlockDurability(Object block) {
        Object vBlock;
        if(block instanceof Block) {
            vBlock = CraftReflection.getVanillaBlock((Block)block);
        } else vBlock = block;

        return strength.get(vBlock);
    }

    //Argument can either be org.bukkit.block.Block or vanilla Block.
    public static <T> T getBlockData(Object object) {
        if(object instanceof Block) {
            Block block = (Block) object;
            Object vworld = CraftReflection.getVanillaWorld(block.getWorld());
            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
                return worldGetType.invoke(vworld, block.getX(), block.getY(), block.getZ());
            } else {
                Object bpos = new BaseBlockPosition(block.getX(), block.getY(), block.getZ()).getAsBlockPosition();

                return worldGetType.invoke(vworld, bpos);
            }
        } else return blockData.get(object);
    }

    public static List<BoundingBox> getCollidingBoxes(@Nullable Entity entity, World world, BoundingBox box) {
        Object vWorld = CraftReflection.getVanillaWorld(world);
        List<BoundingBox> boxes = new ArrayList<>();
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            List<Object> aabbs = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)
                    ? getCubes.invoke(vWorld, box.toAxisAlignedBB())
                    : getCubes.invoke(vWorld, entity != null ? CraftReflection.getEntity(entity) : null, box.toAxisAlignedBB());

            boxes = aabbs
                    .stream()
                    .map(MinecraftReflection::fromAABB)
                    .collect(Collectors.toList());
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

    public static Thread getMainThread(Object minecraftServer) {
        return primaryThread.get(minecraftServer);
    }

    public static Thread getMainThread() {
        return getMainThread(CraftReflection.getMinecraftServer());
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


    //Can either use Player or EntityPlayer object.
    public static <T> T getPlayerConnection(Object player) {
        Object entityPlayer;
        if(player instanceof Player) {
            entityPlayer = CraftReflection.getEntityPlayer((Player)player);
        } else entityPlayer = player;

        return connectionField.get(entityPlayer);
    }

    //Can either use Player or EntityPlayer object.
    public static <T> T getNetworkManager(Object player) {
        return connectionNetworkField.get(getPlayerConnection(player));
    }

    //Can either use Player or EntityPlayer object.
    public static <T> T getChannel(Object player) {
        Object networkManager = getNetworkManager(player);

        return networkChannelField.get(networkManager);
    }

    //Use the netty Channel class.
    public static void disconnectChannel(Object channel) {
        new WrappedClass(channel.getClass()).getMethod("close").invoke(channel);
    }

    //1.13 Method


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
            worldGetType = worldServer.getMethod("getType", blockPos.getParent());
        } else {
            idioticOldStaticConstructorAABB = axisAlignedBB.getMethod("a",
                    double.class, double.class, double.class, double.class, double.class, double.class);
            worldGetType = worldServer.getMethod("getType", int.class, int.class, int.class);
        }
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) {
            getCubes = world.getMethod("a", axisAlignedBB.getParent());

            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
                //1.7.10 does not have the BlockPosition object yet.
                addCBoxes = block.getMethod("a", world.getParent(), int.class, int.class, int.class,
                        axisAlignedBB.getParent(), List.class, entity.getParent());
            } else {
                addCBoxes = block.getMethod("a", world.getParent(), blockPos.getParent(), iBlockData.getParent(),
                        axisAlignedBB.getParent(), List.class, entity.getParent());
            }
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            getCubes = world.getMethod("getCubes", entity.getParent(), axisAlignedBB.getParent());
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

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            activeItemField = entityHuman.getFieldByType(itemStack.getParent(), 0);
        } else {
            activeItemField = entityLiving.getFieldByType(itemStack.getParent(), 0);
        }
        try {
            enumAnimationStack = itemStack.getMethodByType(enumAnimation.getParent(), 0);
        } catch(Exception e) {
            e.printStackTrace();
        }
        canDestroyMethod = playerInventory.getMethod("b",
                ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)
                        ? iBlockData.getParent() : block.getParent());
    }
}
