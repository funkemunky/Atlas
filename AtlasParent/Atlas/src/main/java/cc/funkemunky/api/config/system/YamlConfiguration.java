package cc.funkemunky.api.config.system;

import cc.funkemunky.api.utils.MiscUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class YamlConfiguration extends ConfigurationProvider {

    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
        @Override
        protected Yaml initialValue() {
            Representer representer = new Representer() {
                {
                    representers.put(Configuration.class, new Represent() {
                        @Override
                        public Node representData(Object data) {
                            return represent(((Configuration) data).self);
                        }
                    });
                }
            };

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            return new Yaml(new Constructor(), representer, options);
        }
    };

    @Override
    public void save(Configuration config, File file) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            save(config, writer);
        }
    }

    @Override
    public void save(Configuration config, Writer writer) {
        yaml.get().dump(config.self, writer);
    }

    @Override
    public Configuration load(File file) throws IOException {
        return load(file, null);
    }

    @Override
    public Configuration load(File file, Configuration defaults) throws IOException {
        try (FileInputStream is = new FileInputStream(file)) {
            return load(is, defaults);
        }
    }

    @Override
    public Configuration load(Reader reader) {
        return load(reader, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(Reader reader, Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    @Override
    public Configuration load(InputStream is) {
        return load(is, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(InputStream is, Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(is, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    @Override
    public Configuration load(String string) {
        return load(string, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(String string, Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(string, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    public static Configuration saveDefaultConfig(JavaPlugin plugin, String resourceName) {
        File directory = plugin.getDataFolder();

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, resourceName);

        try {
            if (!file.exists()) {
                file.createNewFile();
                try (final InputStream is = plugin.getResource(resourceName);
                     final OutputStream os = new FileOutputStream(file)) {

                    MiscUtils.copy(is, os);
                }
            }
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}