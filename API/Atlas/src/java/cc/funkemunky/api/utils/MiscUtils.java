package cc.funkemunky.api.utils;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedPacketPlayOutWorldParticle;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedEnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class MiscUtils {

    public static Map<EntityType, Vector> entityDimensions;

    public MiscUtils() {
        entityDimensions = new HashMap<>();

        entityDimensions.put(EntityType.WOLF, new Vector(0.31, 0.8, 0.31));
        entityDimensions.put(EntityType.SHEEP, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.COW, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.PIG, new Vector(0.45, 0.9, 0.45));
        entityDimensions.put(EntityType.MUSHROOM_COW, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.WITCH, new Vector(0.31, 1.95, 0.31));
        entityDimensions.put(EntityType.BLAZE, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.PLAYER, new Vector(0.3, 1.8, 0.3));
        entityDimensions.put(EntityType.VILLAGER, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.CREEPER, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.GIANT, new Vector(1.8, 10.8, 1.8));
        entityDimensions.put(EntityType.SKELETON, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.ZOMBIE, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.SNOWMAN, new Vector(0.35, 1.9, 0.35));
        entityDimensions.put(EntityType.HORSE, new Vector(0.7, 1.6, 0.7));
        entityDimensions.put(EntityType.ENDER_DRAGON, new Vector(1.5, 1.5, 1.5));
        entityDimensions.put(EntityType.ENDERMAN, new Vector(0.31, 2.9, 0.31));
        entityDimensions.put(EntityType.CHICKEN, new Vector(0.2, 0.7, 0.2));
        entityDimensions.put(EntityType.OCELOT, new Vector(0.31, 0.7, 0.31));
        entityDimensions.put(EntityType.SPIDER, new Vector(0.7, 0.9, 0.7));
        entityDimensions.put(EntityType.WITHER, new Vector(0.45, 3.5, 0.45));
        entityDimensions.put(EntityType.IRON_GOLEM, new Vector(0.7, 2.9, 0.7));
        entityDimensions.put(EntityType.GHAST, new Vector(2, 4, 2));
    }

    public static boolean containsIgnoreCase(String toCheck, String contains) {
        return toCheck.toLowerCase().contains(contains.toLowerCase());
    }

    public static String line(String color) {
        return color + Color.Strikethrough + "-----------------------------------------------------";
    }

    public static String line() {
        return Color.Strikethrough + "-----------------------------------------------------";
    }

    public static String lineNoStrike(String color) {
        return color + "-----------------------------------------------------";
    }

    public static String lineNoStrike() {
        return "-----------------------------------------------------";
    }

    public static void createParticlesForBoundingBox(Player player, BoundingBox box) {
        for (float x = box.minX; x < box.maxX + 0.2; x += 0.2f) {
            for (float y = box.minY; y < box.maxY + 0.2; y += 0.2f) {
                for (float z = box.minZ; z < box.maxZ + 0.2; z += 0.2f) {
                    WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.FLAME, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                    packet.sendPacket(player);
                }
            }
        }
    }

    public static void createParticlesForBoundingBox(Player player, BoundingBox box, WrappedEnumParticle type) {
        for (float x = box.minX; x < box.maxX + 0.2; x += 0.2f) {
            for (float y = box.minY; y < box.maxY + 0.2; y += 0.2f) {
                for (float z = box.minZ; z < box.maxZ + 0.2; z += 0.2f) {
                    WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(type, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                    packet.sendPacket(player);
                }
            }
        }
    }

    public static void createParticlesForBoundingBox(Player player, BoundingBox box, WrappedEnumParticle type, float accuracy) {
        for (float x = box.minX; x < box.maxX + accuracy; x += accuracy) {
            for (float y = box.minY; y < box.maxY + accuracy; y += accuracy) {
                for (float z = box.minZ; z < box.maxZ + accuracy; z += accuracy) {
                    WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(type, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                    packet.sendPacket(player);
                }
            }
        }
    }

    public static String unloadPlugin(String pl) {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        SimplePluginManager spm = (SimplePluginManager)pm;
        SimpleCommandMap cmdMap = null;
        List plugins = null;
        Map names = null;
        Map commands = null;
        Map listeners = null;
        boolean reloadlisteners = true;
        if(spm != null) {
            try {
                Field tp = spm.getClass().getDeclaredField("plugins");
                tp.setAccessible(true);
                plugins = (List)tp.get(spm);
                Field arr$ = spm.getClass().getDeclaredField("lookupNames");
                arr$.setAccessible(true);
                names = (Map)arr$.get(spm);

                Field len$;
                try {
                    len$ = spm.getClass().getDeclaredField("listeners");
                    len$.setAccessible(true);
                    listeners = (Map)len$.get(spm);
                } catch (Exception var19) {
                    reloadlisteners = false;
                }

                len$ = spm.getClass().getDeclaredField("commandMap");
                len$.setAccessible(true);
                cmdMap = (SimpleCommandMap)len$.get(spm);
                Field i$ = cmdMap.getClass().getDeclaredField("knownCommands");
                i$.setAccessible(true);
                commands = (Map)i$.get(cmdMap);
            } catch (IllegalAccessException | NoSuchFieldException var20) {
                return "Failed to unload plugin!";
            }
        }

        String var21 = "";
        Plugin[] var22 = Bukkit.getServer().getPluginManager().getPlugins();
        int var23 = var22.length;

        for(int var24 = 0; var24 < var23; ++var24) {
            Plugin p = var22[var24];
            if(p.getDescription().getName().equalsIgnoreCase(pl)) {
                pm.disablePlugin(p);
                var21 = var21 + p.getName() + " ";
                if(plugins != null && plugins.contains(p)) {
                    plugins.remove(p);
                }

                if(names != null && names.containsKey(pl)) {
                    names.remove(pl);
                }

                Iterator it;
                if(listeners != null && reloadlisteners) {
                    it = listeners.values().iterator();

                    while(it.hasNext()) {
                        SortedSet entry = (SortedSet)it.next();
                        Iterator c = entry.iterator();

                        while(c.hasNext()) {
                            RegisteredListener value = (RegisteredListener)c.next();
                            if(value.getPlugin() == p) {
                                c.remove();
                            }
                        }
                    }
                }

                if(cmdMap != null) {
                    it = commands.entrySet().iterator();

                    while(it.hasNext()) {
                        Map.Entry var25 = (Map.Entry) it.next();
                        if(var25.getValue() instanceof PluginCommand) {
                            PluginCommand var26 = (PluginCommand)var25.getValue();
                            if(var26.getPlugin() == p) {
                                var26.unregister(cmdMap);
                                it.remove();
                            }
                        }
                    }
                }
            }
        }

        return var21 + "has been unloaded and disabled!";
    }

    public static <T> T parseObjectFromString(String s, Class<T> clazz) throws Exception {
        return clazz.getConstructor(new Class[] {String.class}).newInstance(s);
    }

    public static BoundingBox getEntityBoundingBox(LivingEntity entity) {
        if (entityDimensions.containsKey(entity.getType())) {
            Vector entityVector = entityDimensions.get(entity.getType());

            float minX = (float) Math.min(-entityVector.getX() + entity.getLocation().getX(), entityVector.getX() + entity.getLocation().getX());
            float minY = (float) Math.min(entity.getLocation().getY(), entityVector.getY() + entity.getLocation().getY());
            float minZ = (float) Math.min(-entityVector.getZ() + entity.getLocation().getZ(), entityVector.getZ() + entity.getLocation().getZ());
            float maxX = (float) Math.max(-entityVector.getX() + entity.getLocation().getX(), entityVector.getX() + entity.getLocation().getX());
            float maxY = (float) Math.max(entity.getLocation().getY(), entityVector.getY() + entity.getLocation().getY());
            float maxZ = (float) Math.max(-entityVector.getZ() + entity.getLocation().getZ(), entityVector.getZ() + entity.getLocation().getZ());
            return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return ReflectionsUtil.toBoundingBox(ReflectionsUtil.getBoundingBox(entity));
    }

    /* MAKE SURE TO ONLY RUN THIS METHOD IN onLoad() AND NO WHERE ELSE */
    public static void registerCommand(String name, JavaPlugin plugin) {
        plugin.getDescription().getCommands().put(name, new HashMap<>());
    }

    public static ItemStack createItem(Material material, int amount, String name, String... lore) {
        ItemStack thing = new ItemStack(material, amount);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(Color.translate(name));
        ArrayList<String> loreList = new ArrayList<>();
        for (String string : lore) {
            loreList.add(Color.translate(string));
        }
        thingm.setLore(loreList);
        thing.setItemMeta(thingm);
        return thing;
    }

    public static void loadPlugin(final String pl) {
        Plugin targetPlugin = null;
        String msg = "";
        final File pluginDir = new File("plugins");
        if (!pluginDir.isDirectory()) {
            return;
        }
        File pluginFile = new File(pluginDir, pl + ".jar");
        if (!pluginFile.isFile()) {
            for (final File f : pluginDir.listFiles()) {
                try {
                    if (f.getName().endsWith(".jar")) {
                        final PluginDescriptionFile pdf = Atlas.getInstance().getPluginLoader().getPluginDescription(f);
                        if (pdf.getName().equalsIgnoreCase(pl)) {
                            pluginFile = f;
                            msg = "(via search) ";
                            break;
                        }
                    }
                }
                catch (InvalidDescriptionException e2) {
                    return;
                }
            }
        }
        try {
            Atlas.getInstance().getServer().getPluginManager().loadPlugin(pluginFile);
            targetPlugin = getPlugin(pl);
            Atlas.getInstance().getServer().getPluginManager().enablePlugin(targetPlugin);
        }
        catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException e3) {
            e3.printStackTrace();
        }
    }


    private static Plugin getPlugin(final String p) {
        for (final Plugin pl : Atlas.getInstance().getServer().getPluginManager().getPlugins()) {
            if (pl.getDescription().getName().equalsIgnoreCase(p)) {
                return pl;
            }
        }
        return null;
    }

    public static void printToConsole(String string) {
        Atlas.getInstance().getConsoleSender().sendMessage(Color.translate(string));
    }
}

