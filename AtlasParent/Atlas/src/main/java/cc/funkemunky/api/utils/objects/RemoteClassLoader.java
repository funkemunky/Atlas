package cc.funkemunky.api.utils.objects;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RemoteClassLoader extends ClassLoader {

    private final byte[] jarBytes;
    public final Set<String> names;
    public Map<String, Class> classes = new HashMap<>();

    public RemoteClassLoader(byte[] jarBytes, ClassLoader parent) throws IOException {
        super(parent);
        this.jarBytes = jarBytes;
        this.names = RemoteClassLoader.loadNames(jarBytes);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        if(names != null) {
            names.forEach(name -> {
                try {
                    String shit = name.replace(".class", "shitnibba123@")
                            .replace("/", ".")
                            .replace("shitnibba123@", ".class");
                    classes.put(shit,
                            getClass(shit));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } else Bukkit.getLogger().warning("names null.");
    }

    /**
     * This will put all the entries into a thread-safe Set
     */
    private static Set<String> loadNames(byte[] jarBytes) throws IOException {
        Set<String> set = new HashSet<>();
        try (ZipInputStream jis =
                     new ZipInputStream(new ByteArrayInputStream(jarBytes))) {
            ZipEntry entry;
            while ((entry = jis.getNextEntry()) != null) {
                set.add(entry.getName());
            }
        }
        return set;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        // I moved the JarInputStream declaration outside the
        // try-with-resources statement as it must not be closed otherwise
        // the returned InputStream won't be readable as already closed
        boolean found = false;
        ZipInputStream jis = null;
        try {
            jis = new ZipInputStream(new ByteArrayInputStream(jarBytes));
            ZipEntry entry;
            while ((entry = jis.getNextEntry()) != null) {
                if (entry.getName().equals(name)) {
                    found = true;
                    return jis;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Only close the stream if the entry could not be found
            if (jis != null && !found) {
                try {
                    jis.close();
                } catch (IOException e) {
                    // ignore me
                }
            }
        }
        return null;
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        if(!classes.containsKey(name)) {
            byte[] b = loadClassFromFile(name);
            return defineClass(name, b, 0, b.length);
        } else return classes.get(name);
    }

    private Class getClass(String name) throws ClassNotFoundException {
        byte[] b = loadClassFromFile(name);
        return defineClass(name, b, 0, b.length);
    }

    private byte[] loadClassFromFile(String fileName)  {
        String name = fileName.replace(".class", "shitguy123@").replace('.', File.separatorChar).replace("shitguy123@", ".class") + (!fileName.contains(".class") ? ".class" : "");
        InputStream inputStream = getResourceAsStream(name);
        byte[] buffer;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue = 0;
        try {
            if(inputStream != null) {
                while ( (nextValue = inputStream.read()) != -1 ) {
                    byteStream.write(nextValue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer = byteStream.toByteArray();
        return buffer;
    }
}