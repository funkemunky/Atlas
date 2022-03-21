package cc.funkemunky.api.utils.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class YamlConfiguration extends ConfigurationProvider
{

    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>()
    {
        @Override
        protected Yaml initialValue()
        {
            Representer representer = new Representer()
            {
                {
                    representers.put( Configuration.class, data -> represent( ( (Configuration) data ).self ));
                    representers.put(ConfigurationSerializable.class, new Represent() {
                        @Override
                        public Node representData(Object data) {
                            ConfigurationSerializable serializable = (ConfigurationSerializable)data;
                            Map<String, Object> values = new LinkedHashMap();
                            values.put("==", ConfigurationSerialization.getAlias(serializable.getClass()));
                            values.putAll(serializable.serialize());
                            return represent(values);
                        }
                    });
                }
            };

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle( DumperOptions.FlowStyle.BLOCK );
            representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            return new Yaml( new Constructor(), representer, options );
        }
    };

    @Override
    public void save(Configuration config, File file) throws IOException
    {
        try ( Writer writer = new OutputStreamWriter( new FileOutputStream( file ), StandardCharsets.UTF_8 ) )
        {
            save( config, writer );
        }
    }

    @Override
    public void save(Configuration config, Writer writer)
    {
        String contents = this.yaml.get().dump(config.self);


        List<String> list = new ArrayList<>();
        Collections.addAll(list, contents.split("\n"));

        int currentLayer = 0;
        StringBuilder currentPath = new StringBuilder();

        StringBuilder sb = new StringBuilder();

        int lineNumber = 0;
        for(Iterator<String> iterator = list.iterator(); iterator.hasNext(); lineNumber++) {
            String line = iterator.next();
            sb.append(line);
            sb.append('\n');

            if (!line.isEmpty()) {
                if (line.contains(":")) {

                    int layerFromLine = config.getLayerFromLine(line, lineNumber);

                    if (layerFromLine < currentLayer) {
                        currentPath = new StringBuilder(config.regressPathBy(currentLayer - layerFromLine, currentPath.toString()));
                    }

                    String key = config.getKeyFromLine(line);

                    if (currentLayer == 0) {
                        currentPath = new StringBuilder(key);
                    } else {
                        currentPath.append("." + key);
                    }

                    String path = currentPath.toString();
                    if (config.comments.containsKey(path)) {
                        config.comments.get(path).forEach(string -> {
                            sb.append(string);
                            sb.append('\n');
                        });
                    }
                }
            }
        }

        try {
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Configuration load(File file) throws IOException
    {
        return load( file, null );
    }

    @Override
    public Configuration load(File file, Configuration defaults) throws IOException
    {
        try ( FileInputStream is = new FileInputStream( file ) )
        {
            return load( is, defaults );
        }
    }

    @Override
    public Configuration load(Reader reader)
    {
        return load( reader, null );
    }

    @SneakyThrows
    @Override
    public Configuration load(Reader reader, Configuration defaults)
    {
        BufferedReader input = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();

        String line;
        try {
            while((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } finally {
            input.close();
        }


        return load(builder.toString(), defaults);
    }

    @Override
    public Configuration load(InputStream is)
    {
        return this.load(new InputStreamReader(is, Charset.defaultCharset()));
    }

    @Override
    public Configuration load(InputStream is, Configuration defaults)
    {
        return this.load(new InputStreamReader(is, Charset.defaultCharset()), defaults);
    }

    @Override
    public Configuration load(String string)
    {
        return load( string, null );
    }


    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(String contents, Configuration defaults)
    {
        Map<String, Object> map;
        map = this.yaml.get().loadAs(contents, LinkedHashMap.class);

        Configuration config = new Configuration( map, defaults );
        config.loadFromString(contents);

        return config;
    }

}