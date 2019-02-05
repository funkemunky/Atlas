package cc.funkemunky.atlas.listeners;

import cc.funkemunky.atlas.AtlasBungee;
import cc.funkemunky.atlas.database.flatfile.FlatfileDatabase;
import cc.funkemunky.atlas.utils.Init;
import cc.funkemunky.atlas.utils.MiscUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

@Init
public class HandshakeListener implements Listener {

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        switch(event.getTag()) {
            case "Atlas_Data_Outgoing": {
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(event.getData());
                    DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
                    String databaseName = inputStream.readUTF();
                    String key = inputStream.readUTF();
                    String className = inputStream.readUTF();
                    String objectString = inputStream.readUTF();

                    if(!AtlasBungee.getInstance().getDatabaseManager().isBungeeDatabase(databaseName)) {
                        AtlasBungee.getInstance().getDatabaseManager().createBungeeDatabase(databaseName);
                    }

                    Object object = MiscUtils.parseObjectFromString(objectString, Class.forName(className));
                    AtlasBungee.getInstance().getDatabaseManager().getBungeeDatabase(databaseName).inputField(key, object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "Atlas_Data_Request": {
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(event.getData());
                    DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
                    int port = inputStream.readInt();
                    String databaseName = inputStream.readUTF();
                    String key = inputStream.readUTF();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream outputStream = new DataOutputStream(stream);

                    if(AtlasBungee.getInstance().getDatabaseManager().isBungeeDatabase(databaseName)) {
                        FlatfileDatabase database = AtlasBungee.getInstance().getDatabaseManager().getBungeeDatabase(databaseName);


                        if(key.equals("*")) {
                            outputStream.writeUTF(databaseName);
                            outputStream.writeUTF("*");
                            database.getDatabaseValues().keySet().forEach(keyLoop -> {
                                Object object = database.getField(keyLoop);

                                try {
                                    outputStream.writeUTF(keyLoop + ";" + object.getClass().getName() + ";" + object.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if(database.getDatabaseValues().containsKey(key)) {
                            Object object = database.getField(key);
                            outputStream.writeUTF(databaseName);
                            outputStream.writeUTF(key);
                            outputStream.writeUTF(object.getClass().getName());
                            outputStream.writeUTF(object.toString());
                        } else {
                            outputStream.writeUTF("Error");
                            outputStream.writeUTF("No such field \"" + key + "\"!");
                        }
                    } else {

                        outputStream.writeUTF("Error");
                        outputStream.writeUTF("No such database \"" + databaseName + "\"!");

                    }
                    Optional<ServerInfo> serverOp = BungeeCord.getInstance().getServers().values().stream().filter(server -> server.getAddress().getPort() == port).findFirst();

                    serverOp.ifPresent(serverInfo -> serverInfo.sendData("Atlas_Data_Incoming", stream.toByteArray()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
