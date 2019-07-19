package cc.funkemunky.bungee.utils;

import cc.funkemunky.bungee.AtlasBungee;
import net.md_5.bungee.api.connection.Server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeUtils {

    public static void sendData(Server server, byte[] data) {
        server.sendData(AtlasBungee.INSTANCE.outChannel, data);
    }

    public static void sendPluginMessage(Server server, Object... objects) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bStream);

        try {
            for (Object object : objects) {
                if(object instanceof String) {
                    outputStream.writeUTF((String)object);
                } else if(object instanceof Long) {
                    outputStream.writeLong((long)object);
                } else if(object instanceof Double) {
                    outputStream.writeDouble((double)object);
                } else if(object instanceof Float) {
                    outputStream.writeFloat((float)object);
                } else if(object instanceof Integer) {
                    outputStream.writeInt((int)object);
                } else if(object instanceof Boolean) {
                    outputStream.writeBoolean((boolean)object);
                }
            }

            sendData(server, bStream.toByteArray());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
