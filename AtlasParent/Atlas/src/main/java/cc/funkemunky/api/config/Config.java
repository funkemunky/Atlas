package cc.funkemunky.api.config;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {

    private File file;
    private String name;
    private JavaPlugin plugin;
    private Configuration configuration;

    public Config(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.file = new File(plugin.getDataFolder(), name);
        try {
            if (!this.file.exists()) {
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdir();
                }
                this.file.createNewFile();
                try (final InputStream is = plugin.getClass().getClassLoader().getResourceAsStream(name);
                     final OutputStream os = new FileOutputStream(this.file)) {
                    ByteStreams.copy(is, os);
                }
            }
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        this.file = new File(plugin.getDataFolder(), name);
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider( YamlConfiguration.class).save(this.configuration, this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public File getFile() {
        return this.file;
    }

    public double getDouble(final String path) {
        if (this.configuration.get(path) != null) {
            return this.configuration.getDouble(path);
        }
        return 0.0;
    }

    public Object get(String path) {
        return configuration.get(path);
    }

    public int getInt(final String path) {
        if (this.configuration.get(path) != null) {
            return this.configuration.getInt(path);
        }
        return 0;
    }

    public boolean getBoolean(final String path) {
        return this.configuration.get(path) != null && this.configuration.getBoolean(path);
    }

    public String getString(final String path) {
        if (this.configuration.get(path) != null) {
            return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
        }
        return "String at path: " + path + " not found!";
    }

    public List<String> getStringList(final String path) {
        if (this.configuration.get(path) != null) {
            final ArrayList<String> strings = new ArrayList<String>();
            for (final String string : this.configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return Arrays.asList("String List at path: " + path + " not found!");
    }
}