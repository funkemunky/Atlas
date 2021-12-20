package cc.funkemunky.api.reflections.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.Vec3D;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumAnimation;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.Materials;
import cc.funkemunky.api.utils.exceptions.Validate;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.NoCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MinecraftReflection {
    public static WrappedClass entity = Reflections.getNMSClass("Entity");
    public static WrappedClass axisAlignedBB = Reflections.getNMSClass("AxisAlignedBB");
    public static WrappedClass entityHuman = Reflections.getNMSClass("EntityHuman");
    public static WrappedClass entityLiving = Reflections.getNMSClass("EntityLiving");
    public static WrappedClass block = Reflections.getNMSClass("Block");
    public static WrappedClass iBlockData, blockBase,
            chunkProviderServer = Reflections.getNMSClass("ChunkProviderServer");
    public static WrappedClass itemClass = Reflections.getNMSClass("Item"),
            enumChatFormat = Reflections.getNMSClass("EnumChatFormat");;
    public static WrappedClass world = Reflections.getNMSClass("World");
    public static WrappedClass worldServer = Reflections.getNMSClass("WorldServer");
    public static WrappedClass playerInventory = Reflections.getNMSClass("PlayerInventory");
    public static WrappedClass itemStack = Reflections.getNMSClass("ItemStack"),
            item = Reflections.getNMSClass("Item");
    public static WrappedClass chunk = Reflections.getNMSClass("Chunk");
    public static WrappedClass classBlockInfo;
    public static WrappedClass minecraftServer = Reflections.getNMSClass("MinecraftServer");
    public static WrappedClass entityPlayer = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
            ? Reflections.getClass("net.minecraft.server.level.EntityPlayer")
            : Reflections.getNMSClass("EntityPlayer");
    public static WrappedClass playerConnection = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
            ? Reflections.getClass("net.minecraft.server.network.PlayerConnection")
            : Reflections.getNMSClass("PlayerConnection");
    public static WrappedClass networkManager = Reflections.getNMSClass("NetworkManager");
    public static WrappedClass serverConnection = Reflections.getNMSClass("ServerConnection");
    public static WrappedClass gameProfile = Reflections.getUtilClass("com.mojang.authlib.GameProfile");
    private static final WrappedClass propertyMap = Reflections.getUtilClass("com.mojang.authlib.properties.PropertyMap");
    private static final WrappedClass forwardMultiMap = Reflections.getUtilClass("com.google.common.collect.ForwardingMultimap");
    public static WrappedClass iChatBaseComponent = Reflections.getNMSClass("IChatBaseComponent"),
            chatComponentSerializer =  Reflections.getNMSClass("IChatBaseComponent");
    public static WrappedClass vec3D = Reflections.getNMSClass("Vec3D");

    private static final WrappedMethod getProfile = CraftReflection.craftPlayer.getMethod("getProfile");
    private static final WrappedMethod methodGetServerConnection = minecraftServer
            .getMethodByType(serverConnection.getParent(), ProtocolVersion.getGameVersion()
                    .isBelow(ProtocolVersion.V1_13) ? 1 : 0);
    private static final WrappedMethod getProperties = gameProfile.getMethod("getProperties");
    private static final WrappedMethod removeAll = forwardMultiMap.getMethod("removeAll", Object.class);
    private static final WrappedMethod putAll = propertyMap.getMethod("putAll", Object.class, Iterable.class);
    private static final WrappedMethod worldGetType;
    //SimpleCollisionBoxes
    private static final WrappedMethod getCubes;
    private static final WrappedField aBB = axisAlignedBB.getFieldByName("a");
    private static final WrappedField bBB = axisAlignedBB.getFieldByName("b");
    private static final WrappedField cBB = axisAlignedBB.getFieldByName("c");
    private static final WrappedField dBB = axisAlignedBB.getFieldByName("d");
    private static final WrappedField eBB = axisAlignedBB.getFieldByName("e");
    private static final WrappedField fBB = axisAlignedBB.getFieldByName("f");
    private static WrappedConstructor aabbConstructor;
    private static WrappedMethod idioticOldStaticConstructorAABB, methodBlockCollisionBox;
    private static final WrappedField entitySimpleCollisionBox = entity.getFirstFieldByType(axisAlignedBB.getParent());
    public static WrappedClass enumAnimation = Reflections.getNMSClass("EnumAnimation");

    //ItemStack methods and fields
    private static WrappedMethod enumAnimationStack;
    private static final WrappedField activeItemField;
    private static final WrappedMethod getItemMethod = itemStack.getMethodByType(item.getParent(), 0);
    private static final WrappedMethod getAnimationMethod = itemClass.getMethodByType(enumAnimation.getParent(), 0);
    private static final WrappedMethod canDestroyMethod;

    //1.13+ only
    private static WrappedClass voxelShape;
    private static WrappedClass worldReader;
    private static WrappedMethod getCubesFromVoxelShape;

    private static final WrappedMethod itemStackAsBukkitCopy = CraftReflection.craftItemStack
            .getMethod("asBukkitCopy", itemStack.getParent());

    //Blocks
    private static WrappedMethod addCBoxes;
    public static WrappedClass blockPos;
    private static WrappedConstructor blockPosConstructor;
    private static WrappedMethod getBlockData, getBlock;
    private static WrappedField blockData;
    private static final WrappedField frictionFactor;
    private static final WrappedField strength;
    private static final WrappedField chunkProvider = MinecraftReflection.worldServer
            .getFieldByType(Reflections.getNMSClass(ProtocolVersion.getGameVersion()
                    .isBelow(ProtocolVersion.v1_16)
                    && ProtocolVersion.getGameVersion()
                    .isOrAbove(ProtocolVersion.V1_9) ? "IChunkProvider" : "ChunkProviderServer")
                    .getParent(), 0);


    //Entity Player fields
    private static final WrappedField connectionField = entityPlayer
            .getFieldByType(playerConnection.getParent(), 0);
    private static final WrappedField connectionNetworkField = playerConnection
            .getFieldByType(networkManager.getParent(), 0);
    private static final WrappedField networkChannelField = networkManager.getFieldByType(Reflections
            .getUtilClass("io.netty.channel.Channel").getParent(), 0);

    //General Fields
    private static final WrappedField primaryThread = minecraftServer.getFirstFieldByType(Thread.class);

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
        Object vanillaBlock = getBlock(block);
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
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) {
            BaseBlockPosition blockPos = new BaseBlockPosition(block.getX(), block.getY(), block.getZ());
            Object blockData = getBlockData.invoke(vanillaBlock);

            addCBoxes.invoke(vanillaBlock, world, blockPos.getAsBlockPosition(), blockData,
                    box.toAxisAlignedBB(), aabbs, entity != null ? CraftReflection.getEntity(entity) : null); //Entity is always null for these
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            BaseBlockPosition blockPos = new BaseBlockPosition(block.getX(), block.getY(), block.getZ());
            Object blockData = getBlockData.invoke(vanillaBlock);

            addCBoxes.invoke(vanillaBlock, blockData, world, blockPos.getAsBlockPosition(),
                    box.toAxisAlignedBB(), aabbs, entity != null ? CraftReflection.getEntity(entity) : null, true); //Entity is always null for these
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

        return entitySimpleCollisionBox.get(vanillaEntity);
    }

    public static <T> T getItemInUse(HumanEntity entity) {
        Object humanEntity = CraftReflection.getEntityHuman(entity);
        return activeItemField.get(humanEntity);
    }

    public static <T> T getBlock(Block block) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            Object blockData = getBlockData(block);

            return getBlock.invoke(blockData);
        } else {
            return worldGetType.invoke(CraftReflection.getVanillaWorld(block.getWorld()),
                    block.getX(), block.getY(), block.getZ());
        }
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
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)) {
            Object inventory = CraftReflection.getVanillaInventory(player);
            Object vBlock;
            if(block instanceof Block) {
                vBlock = getBlock((Block)block);
            } else vBlock = block;

            return canDestroyMethod.invoke(inventory,
                    ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)
                            ? blockData.get(vBlock) : vBlock);
        } else {
            Object vanillaItem = CraftReflection.getVanillaItemStack(player.getItemInHand());

            Object vBlock;
            if(block instanceof Block) {
                vBlock = getBlock((Block)block);
            } else vBlock = block;

            return canDestroyMethod.invoke(vanillaItem, getBlockData(vBlock));
        }
    }

    /* Gets the friction of a block. Input can be NMS Block or Bukkit Block. */
    public static float getFriction(Object block) {
        Object vBlock;
        if(block instanceof Block) {
            vBlock = getBlock((Block)block);
        } else vBlock = block;

        return frictionFactor.get(vBlock);
    }

    public static int getPing(Player player) {
        return -1;
    }

    public static <T> T getServerConnection() {
        return methodGetServerConnection.invoke(CraftReflection.getMinecraftServer());
    }

    public static <T> T getServerConnection(Object minecraftServer) {
        return methodGetServerConnection.invoke(minecraftServer);
    }

    /* Gets the amount of mining required to break a block. Input can be NMS Block or Bukkit Block. */
    public static float getBlockDurability(Object block) {
        Object vBlock;
        if(block instanceof Block) {
            vBlock = getBlock((Block)block);
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
            Object voxelShape = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
                    ? getCubes.invoke(vWorld, null, box.toAxisAlignedBB())
                    : (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)
                    ? getCubes.invoke(vWorld, null, box.toAxisAlignedBB(), 0D, 0D, 0D)
                    : getCubes.invoke(vWorld, null, box.toAxisAlignedBB(), (Predicate<Object>)obj -> true));

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

    public static CollisionBox getCollisionBox(Block block) {
        Validate.isTrue(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13));

        Object vanillaBlock = CraftReflection.getVanillaBlock(block);
        Object vanillaWorld = CraftReflection.getVanillaWorld(block.getWorld());

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            Object axisAlignedBB = methodBlockCollisionBox
                    .invoke(vanillaBlock, vanillaWorld, block.getX(), block.getY(), block.getZ());

            if(axisAlignedBB != null) {
                return new SimpleCollisionBox(axisAlignedBB);
            } else return NoCollisionBox.INSTANCE;
        } else {
            Object blockPos = new BaseBlockPosition(block.getX(), block.getY(), block.getZ()).getAsBlockPosition();
            Object blockData = getBlockData(vanillaBlock);
            Object axisAlignedBB = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)
                    ? methodBlockCollisionBox.invoke(vanillaBlock, blockData, vanillaWorld, blockPos)
                    : methodBlockCollisionBox.invoke(vanillaBlock, vanillaWorld, blockPos, blockData);

            if(axisAlignedBB != null) {
                return new SimpleCollisionBox(axisAlignedBB);
            } else return NoCollisionBox.INSTANCE;
        }
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

    private static WrappedMethod fluidMethod, getFlowMethod;

    public static Vec3D getBlockFlow(Block block) {
        if(Materials.checkFlag(block.getType(), Materials.LIQUID)) {
            Object world = CraftReflection.getVanillaWorld(block.getWorld());
            BaseBlockPosition pos = new BaseBlockPosition(block.getX(), block.getY(), block.getZ());
            if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_13)) {
                Object vanillaBlock = CraftReflection.getVanillaBlock(block);

                return new Vec3D((Object)getFlowMethod.invoke(vanillaBlock, world, pos.getAsBlockPosition()));
            } else if(Atlas.getInstance().getBlockBoxManager().getBlockBox().isChunkLoaded(block.getLocation())) {
                Object fluid = fluidMethod.invoke(world, pos.getAsBlockPosition());

                return new Vec3D((Object)getFlowMethod.invoke(fluid, world, pos.getAsBlockPosition()));
            }
        }
        return new Vec3D(0,0,0);
    }

    public static ItemStack toBukkitItemStack(Object vanillaItemStack) {
        return itemStackAsBukkitCopy.invoke(null, vanillaItemStack);
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

    public static <T> List<T> getVanillaChunks(World world) {
        return Arrays.stream(world.getLoadedChunks())
                .map(c -> (T) CraftReflection.getVanillaChunk(c))
                .collect(Collectors.toList());
    }

    static {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
            iBlockData = Reflections.getNMSClass("IBlockData");
            blockPos = Reflections.getNMSClass("BlockPosition");
            blockPosConstructor = blockPos.getConstructor(int.class, int.class, int.class);
            getBlock = iBlockData.getMethodByType(block.getParent(), 0);
            blockData = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
                    ? block.getFieldByType(iBlockData.getParent(), 0) :  block.getFieldByName("blockData");
            blockPosConstructor = blockPos.getConstructor(int.class, int.class, int.class);
            getBlockData = block.getMethodByType(iBlockData.getParent(), 0);
            aabbConstructor = axisAlignedBB
                    .getConstructor(double.class, double.class, double.class, double.class, double.class, double.class);
            worldGetType = worldServer.getMethodByType(iBlockData.getParent(), 0, blockPos.getParent());
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
                methodBlockCollisionBox = block
                        .getMethod("a", world.getParent(), int.class, int.class, int.class);
            } else {
                addCBoxes = block.getMethod("a", world.getParent(), blockPos.getParent(), iBlockData.getParent(),
                        axisAlignedBB.getParent(), List.class, entity.getParent());
                if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                    methodBlockCollisionBox = block
                            .getMethod("a", iBlockData.getParent(), world.getParent(), blockPos.getParent());
                } else methodBlockCollisionBox = block
                        .getMethod("a", world.getParent(), blockPos.getParent(), iBlockData.getParent());
            }

            getFlowMethod = Reflections.getNMSClass("BlockFluids")
                    .getDeclaredMethodByType(vec3D.getParent(), 0);
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            getCubes = world.getMethod("getCubes", entity.getParent(), axisAlignedBB.getParent());
            addCBoxes = block.getMethod("a", iBlockData.getParent(), world.getParent(), blockPos.getParent(),
                    axisAlignedBB.getParent(), List.class, entity.getParent(), boolean.class);
            methodBlockCollisionBox = block
                    .getMethod("a", iBlockData.getParent(), world.getParent(), blockPos.getParent());
            getFlowMethod = Reflections.getNMSClass("BlockFluids")
                    .getDeclaredMethodByType(vec3D.getParent(), 0);
        } else {
            classBlockInfo = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)
                    ? Reflections.getNMSClass("BlockBase$Info") : Reflections.getNMSClass("Block$Info");
            worldReader = Reflections.getNMSClass("IWorldReader");
            //1.13 and 1.13.1 returns just VoxelShape while 1.13.2+ returns a Stream<VoxelShape>
            getCubes = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_18)
                    ? worldReader.getMethodByType(List.class, 0, entity.getParent(), axisAlignedBB.getParent())
                    : (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16) ?
                    worldReader.getMethod("a", entity.getParent(), axisAlignedBB.getParent(),
                            double.class, double.class, double.class)
                    : world.getMethod("c", entity.getParent(), axisAlignedBB.getParent(), Predicate.class));
            voxelShape = Reflections.getNMSClass("VoxelShape");
            getCubesFromVoxelShape = voxelShape.getMethodByType(List.class, 0);
            fluidMethod = world.getMethodByType(Reflections.getNMSClass("Fluid").getParent(), 0, blockPos.getParent());
            getFlowMethod = Reflections.getNMSClass("Fluid").getMethodByType(vec3D.getParent(), 0);
        }

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
            blockBase = Reflections.getNMSClass("BlockBase");
        }
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            activeItemField = entityHuman.getFieldByType(itemStack.getParent(), 0);
        } else {
            activeItemField = entityLiving.getFieldByType(itemStack.getParent(), 0);
        }

        canDestroyMethod = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)
                ? playerInventory.getMethod("b",
                ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)
                        ? iBlockData.getParent() : itemClass.getParent())
                : itemStack.getMethodByType(boolean.class, 0, iBlockData.getParent());
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            frictionFactor = (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)
                    ? block : blockBase).getFieldByName("frictionFactor");
        } else frictionFactor = blockBase.getFieldByType(float.class, 1);
        strength = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
                ? blockBase.getFieldByType(float.class, 0)
                : (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)
                    ? block.getFieldByName("strength") : blockBase.getFieldByName("durability"));
    }
}
