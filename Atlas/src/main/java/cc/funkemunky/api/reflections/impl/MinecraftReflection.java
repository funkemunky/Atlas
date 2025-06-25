package cc.funkemunky.api.reflections.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.handlers.protocolsupport.Protocol;
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
    // WrappedClass fields (ALL set to null)
    public static WrappedClass entity = null;
    public static WrappedClass axisAlignedBB = null, chunkStatus = null;
    public static WrappedClass entityHuman = null;
    public static WrappedClass entityLiving = null;
    public static WrappedClass block = null;
    public static WrappedClass iBlockData = null, blockBase = null, chunkProviderServer = null;
    public static WrappedClass itemClass = null, enumChatFormat = null;
    public static WrappedClass world = null;
    public static WrappedClass worldServer = null;
    public static WrappedClass playerInventory = null;
    public static WrappedClass itemStack = null, item = null;
    public static WrappedClass chunk = null;
    public static WrappedClass classBlockInfo = null;
    public static WrappedClass minecraftServer = null;
    public static WrappedClass entityPlayer = null;
    public static WrappedClass playerConnection = null;
    public static WrappedClass networkManager = null;
    public static WrappedClass serverConnection = null;
    public static WrappedClass gameProfile = null;
    public static WrappedClass iChatBaseComponent = null, chatComponentText = null;
    public static WrappedClass vec3D = null;
    public static WrappedClass enumAnimation = null;
    public static WrappedClass blockPos = null;

    // WrappedMethod fields (ALL set to null)
    private static WrappedMethod getProfile = null;
    private static WrappedMethod methodGetServerConnection = null;
    private static WrappedConstructor chatComponentTextConst = null;
    private static WrappedMethod worldGetType = null;
    private static WrappedMethod getCubes = null;
    private static WrappedField aBB = null;
    private static WrappedField bBB = null;
    private static WrappedField cBB = null;
    private static WrappedField dBB = null;
    private static WrappedField eBB = null;
    private static WrappedField fBB = null;
    private static WrappedConstructor aabbConstructor = null;
    private static WrappedMethod idioticOldStaticConstructorAABB = null, methodBlockCollisionBox = null;
    private static WrappedField entitySimpleCollisionBox = null;
    private static WrappedMethod enumAnimationStack = null;
    private static WrappedField activeItemField = null;
    private static WrappedMethod getItemMethod = null;
    private static WrappedMethod getAnimationMethod = null;
    private static WrappedMethod canDestroyMethod = null;
    private static WrappedMethod getCubesFromVoxelShape = null;
    private static WrappedMethod itemStackAsBukkitCopy = null;
    private static WrappedMethod addCBoxes = null;
    private static WrappedMethod getBlockData = null, getBlock = null;
    private static WrappedField blockData = null;
    private static WrappedField frictionFactor = null;
    private static WrappedField strength = null;
    private static WrappedField chunkProvider = null;
    private static WrappedField connectionField = null;
    private static WrappedField connectionNetworkField = null;
    private static WrappedField networkChannelField = null;
    private static WrappedField primaryThread = null;
    private static WrappedMethod fluidMethod = null, getFlowMethod = null;
    private static WrappedMethod getMobEffect = null;
    private static WrappedMethod getMobEffectId = null;

    // Initialization
    static {
        try {
            axisAlignedBB = Reflections.getNMSClass("AxisAlignedBB");
            chunkProviderServer = Reflections.getNMSClass(ProtocolVersion.getGameVersion()
                    .isBelow(ProtocolVersion.v1_16)
                    && ProtocolVersion.getGameVersion()
                    .isOrAbove(ProtocolVersion.V1_9) ? "IChunkProvider" : "ChunkProviderServer");
            enumChatFormat = Reflections.getNMSClass("EnumChatFormat");
            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
                chunkStatus = Reflections.getNMSClass("ChunkStatus");
            }
        } catch (Throwable ignored) {
            axisAlignedBB = Reflections.getNMSClass("AABB");
            chunkProviderServer = Reflections.getNMSClass("ServerChunkCache");
            enumChatFormat = Reflections.getNMSClass("ChatFormatting");
            chunkStatus = Reflections.getClass("net.minecraft.world.level.chunk.status.ChunkStatus");
        }

        try {
            // Begin static initialization, everything is wrapped in this try/catch
            entity = Reflections.getNMSClass("Entity");
            try {
                entityHuman = Reflections.getNMSClass("EntityHuman");
                entityLiving = Reflections.getNMSClass("EntityLiving");
                world = Reflections.getNMSClass("World");
                worldServer = Reflections.getNMSClass("WorldServer");
            } catch (Throwable t) {
                entityLiving = Reflections.getNMSClass("LivingEntity");
                entityHuman = Reflections.getNMSClass("Player");
                world = Reflections.getNMSClass("Level");
                worldServer = Reflections.getNMSClass("ServerLevel");
            }

            block = Reflections.getNMSClass("Block");
            itemClass = Reflections.getNMSClass("Item");
            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_20_1)) {
                playerInventory = Reflections.getClass("net.minecraft.world.entity.player.Inventory");
            } else playerInventory = Reflections.getNMSClass("PlayerInventory");
            itemStack = Reflections.getNMSClass("ItemStack");
            item = Reflections.getNMSClass("Item");
            chunk = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("Chunk")
                    : Reflections.getNMSClass("LevelChunk");
            minecraftServer = Reflections.getNMSClass("MinecraftServer");
            entityPlayer = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("ServerPlayer")
                    : Reflections.getNMSClass("EntityPlayer");
            playerConnection = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("ServerPlayerConnection")
                    : Reflections.getNMSClass("PlayerConnection");
            networkManager = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("NetworkManager")
                    : Reflections.getNMSClass("Connection");
            serverConnection = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("ServerConnection")
                    : Reflections.getNMSClass("ServerConnectionListener");
            gameProfile = Reflections.getUtilClass("com.mojang.authlib.GameProfile");
            iChatBaseComponent = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("IChatBaseComponent")
                    : Reflections.getClass("net.minecraft.network.chat.Component");
            vec3D = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("Vec3D")
                    : Reflections.getNMSClass("Vec3");
            enumAnimation = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("EnumAnimation")
                    : Reflections.getNMSClass("UseAnim");

            getProfile = CraftReflection.craftPlayer.getMethod("getProfile");
            methodGetServerConnection = minecraftServer
                    .getMethodByType(serverConnection.getParent(), ProtocolVersion.getGameVersion()
                            .isBelow(ProtocolVersion.V1_13) ? 1 : 0);

            if (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
                iBlockData = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                        ? Reflections.getNMSClass("IBlockData")
                        : Reflections.getNMSClass("BlockState");
                blockPos = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                        ? Reflections.getNMSClass("BlockPosition")
                        : Reflections.getNMSClass("BlockPos");
                getBlock = iBlockData.getMethodByType(block.getParent(), 0);
                blockData = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
                        ? block.getFieldByType(iBlockData.getParent(), 0)
                        : block.getFieldByName("blockData");
                getBlockData = block.getMethodByType(iBlockData.getParent(), 0);
                aabbConstructor = axisAlignedBB
                        .getConstructor(double.class, double.class, double.class, double.class, double.class, double.class);
                worldGetType = worldServer.getMethodByType(iBlockData.getParent(), 0, blockPos.getParent());
            } else {
                idioticOldStaticConstructorAABB = axisAlignedBB.getMethod("a",
                        double.class, double.class, double.class, double.class, double.class, double.class);
                worldGetType = worldServer.getMethod("getType", int.class, int.class, int.class);
            }

            aBB = axisAlignedBB.getFieldByName("a");
            bBB = axisAlignedBB.getFieldByName("b");
            cBB = axisAlignedBB.getFieldByName("c");
            dBB = axisAlignedBB.getFieldByName("d");
            eBB = axisAlignedBB.getFieldByName("e");
            fBB = axisAlignedBB.getFieldByName("f");
            entitySimpleCollisionBox = entity.getFirstFieldByType(axisAlignedBB.getParent());

            // Colliding box/collision initialization
            WrappedClass voxelShape;
            WrappedClass worldReader;
            if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) {
                getCubes = world.getMethod("a", axisAlignedBB.getParent());

                if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
                    addCBoxes = block.getMethod("a", world.getParent(), int.class, int.class, int.class,
                            axisAlignedBB.getParent(), List.class, entity.getParent());
                    methodBlockCollisionBox = block
                            .getMethod("a", world.getParent(), int.class, int.class, int.class);
                } else {
                    addCBoxes = block.getMethod("a", world.getParent(), blockPos.getParent(), iBlockData.getParent(),
                            axisAlignedBB.getParent(), List.class, entity.getParent());
                    if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                        methodBlockCollisionBox = block
                                .getMethod("a", iBlockData.getParent(), world.getParent(), blockPos.getParent());
                    } else {
                        methodBlockCollisionBox = block
                                .getMethod("a", world.getParent(), blockPos.getParent(), iBlockData.getParent());
                    }
                }
                getFlowMethod = Reflections.getNMSClass("BlockFluids")
                        .getDeclaredMethodByType(vec3D.getParent(), 0);
            } else if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
                getCubes = world.getMethod("getCubes", entity.getParent(), axisAlignedBB.getParent());
                addCBoxes = block.getMethod("a", iBlockData.getParent(), world.getParent(), blockPos.getParent(),
                        axisAlignedBB.getParent(), List.class, entity.getParent(), boolean.class);
                methodBlockCollisionBox = block
                        .getMethod("a", iBlockData.getParent(), world.getParent(), blockPos.getParent());
                getFlowMethod = Reflections.getNMSClass("BlockFluids")
                        .getDeclaredMethodByType(vec3D.getParent(), 0);
            } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)) {
                classBlockInfo = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)
                        ? Reflections.getNMSClass("BlockBase$Info") : Reflections.getNMSClass("Block$Info");
                worldReader = Reflections.getNMSClass("IWorldReader");
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

                if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_19)) {
                    chatComponentText = Reflections.getNMSClass("ChatComponentText");
                    chatComponentTextConst = chatComponentText.getConstructor(String.class);
                }
            } else {
                classBlockInfo = Reflections.getNMSClass("BlockBehaviour$Properties");
                worldReader = Reflections.getNMSClass("LevelReader");
                getCubes = worldReader.getMethod("getEntityCollisions", entity.getParent(), axisAlignedBB.getParent());
                voxelShape = Reflections.getNMSClass("VoxelShape");
                getCubesFromVoxelShape = voxelShape.getMethod("toAabbs");
                var fluidClass = Reflections.getClass("net.minecraft.world.level.material.FluidState");
                fluidMethod = world.getMethodByType(fluidClass.getParent(), 0, blockPos.getParent());
                getFlowMethod = fluidClass.getMethodByType(vec3D.getParent(), 0);
            }

            if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
                blockBase = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_20_1)
                        ? Reflections.getNMSClass("BlockBehaviour")
                        : Reflections.getNMSClass("BlockBase");
            }
            if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
                activeItemField = entityHuman.getFieldByType(itemStack.getParent(), 0);
            } else {
                activeItemField = entityLiving.getFieldByType(itemStack.getParent(), 0);
            }

            canDestroyMethod = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)
                    ? playerInventory.getMethod("b",
                    ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)
                            ? iBlockData.getParent() : itemClass.getParent())
                    : itemStack.getMethodByType(boolean.class, 0, iBlockData.getParent());

            if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
                frictionFactor = (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)
                        ? block : blockBase).getFieldByName("frictionFactor");
            } else {
                frictionFactor = blockBase.getFieldByType(float.class, 1);
            }
            strength = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
                    ? blockBase.getFieldByType(float.class, 0)
                    : (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)
                    ? block.getFieldByName("strength") : blockBase.getFieldByName("durability"));

            enumAnimationStack = itemStack.getMethodByType(enumAnimation.getParent(), 0);
            getItemMethod = itemStack.getMethodByType(item.getParent(), 0);
            getAnimationMethod = itemClass.getMethodByType(enumAnimation.getParent(), 0);
            itemStackAsBukkitCopy = CraftReflection.craftItemStack
                    .getMethod("asBukkitCopy", itemStack.getParent());

            chunkProvider = MinecraftReflection.worldServer.getFieldByType(
                    chunkProviderServer.getParent(),
                    0
            );

            connectionField = entityPlayer.getFieldByType(playerConnection.getParent(), 0);
            //TODO Fix this, this is not found within the Connection class at index 0. My gusess is the player Connection class is incorrect
            connectionNetworkField = playerConnection.getFieldByType(networkManager.getParent(), 0);
            networkChannelField = networkManager.getFieldByType(
                    Reflections.getUtilClass("io.netty.channel.Channel").getParent(), 0);
            primaryThread = minecraftServer.getFirstFieldByType(Thread.class);

            WrappedClass mobEffectList = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_20_1)
                    ? Reflections.getNMSClass("MobEffectList")
                    : Reflections.getNMSClass("MobEffect");
            getMobEffect = ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_9)
                    ? mobEffectList.getMethodByType(mobEffectList.getParent(), 0, int.class) : null;
            getMobEffectId = ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_9)
                    ? mobEffectList.getMethodByType(int.class, 0, mobEffectList.getParent()) : null;

            if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
                iBlockData = Reflections.getNMSClass("IBlockData");
                blockPos = Reflections.getNMSClass("BlockPosition");
                getBlock = iBlockData.getMethodByType(block.getParent(), 0);
                blockData = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
                        ? block.getFieldByType(iBlockData.getParent(), 0) :  block.getFieldByName("blockData");
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

                if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_19)) {
                    chatComponentText =  Reflections.getNMSClass("ChatComponentText");
                    chatComponentTextConst = chatComponentText.getConstructor(String.class);
                }
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
        } catch (Throwable t) {
            // If *any* error occurs in static field reflection, everything remains null
            t.printStackTrace(); // Or use your preferred logging mechanism
        }
    }

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

    public static <T> T getChunkStatusField(String fieldName) {
        return chunkStatus.getFieldByName(fieldName).get(null);
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

    public static <T> T getMobEffect(int effectId) {
        return getMobEffect.invoke(null, effectId);
    }

    public static int getMobEffectId(Object effect) {
        return getMobEffectId.invoke(null, effect);
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

    public static Object getChatComponentFromText(String string) {
        return chatComponentTextConst.newInstance(string);
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

    public static <T> T toAABB(SimpleCollisionBox box) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            return idioticOldStaticConstructorAABB
                    .invoke(null,
                            box.xMin, box.yMin, box.zMin,
                            box.xMax, box.yMax, box.zMax);
        } else return aabbConstructor
                .newInstance(box.xMin, box.yMin, box.zMin,
                        box.xMax, box.yMax, box.zMax);
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
}
