package cc.funkemunky.bungee.listeners;

import cc.funkemunky.bungee.utils.asm.Init;
import cc.funkemunky.bungee.utils.reflection.types.WrappedClass;
import cc.funkemunky.bungee.utils.reflection.types.WrappedMethod;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Init
public class AtlasMsgListener implements Listener {

    public AtlasMsgListener() {
        BungeeCord.getInstance().registerChannel("atlasOut");
    }

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        if(event.getTag().equalsIgnoreCase("atlasOut")) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(event.getData());
                System.out.println("length: " + event.getData().length);
                ObjectInputStream inputStream = new ObjectInputStream(bis);

                String type = inputStream.readUTF();

                switch(type) {
                    case "sendObjects": {
                        String serverField = inputStream.readUTF();

                        if(!serverField.contains("_")) {
                            return;
                        }
                        String server = serverField.split("_")[0];

                        if(server.equalsIgnoreCase("all")) {
                            for (ServerInfo info : BungeeCord.getInstance().getServers().values()) {
                                info.sendData("atlasIn", event.getData());
                            }
                        } else {
                            Optional<ServerInfo> infoOptional = BungeeCord.getInstance().getServers().values()
                                    .stream().filter(val -> val.getName().equalsIgnoreCase(server)).findFirst();

                            infoOptional.ifPresent(serverInfo -> serverInfo.sendData("atlasIn", event.getData()));
                        }

                        break;
                    }
                    case "version": {

                        UUID uuid = (UUID) inputStream.readObject();
                        ProxiedPlayer player = BungeeCord.getInstance().getPlayer(uuid);

                        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                        ObjectOutputStream dataOut = new ObjectOutputStream(bOut);

                        dataOut.writeUTF("version");

                        if(player == null) {
                            dataOut.writeBoolean(false);
                            dataOut.writeInt(-1);
                            dataOut.writeObject(uuid);
                        } else {
                            int version = -1;
                            try {
                                Class<?> Via = Class.forName("us.myles.ViaVersion.api.Via");
                                Class<?> clazzViaAPI = Class.forName("us.myles.ViaVersion.api.ViaAPI");
                                Object ViaAPI = Via.getMethod("getAPI").invoke(null);
                                Method getPlayerVersion = clazzViaAPI.getMethod("getPlayerVersion", Object.class);
                                System.out.println("viaversion");
                                version = (int) getPlayerVersion.invoke(ViaAPI, player);
                            } catch(Exception e) {
                                version = player.getPendingConnection().getVersion();
                            }

                            System.out.println(player.getName() + " version: " + version);
                            dataOut.writeBoolean(true);
                            dataOut.writeInt(version);
                            dataOut.writeObject(uuid);

                        }

                        BungeeCord.getInstance().getServers()
                                .forEach((name, info) -> info.sendData("atlasIn", bOut.toByteArray()));
                        break;
                    }
                    case "commandBungee": {
                        String command = inputStream.readUTF();

                        BungeeCord.getInstance().getPluginManager()
                                .dispatchCommand(BungeeCord.getInstance().getConsole(), command);
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
