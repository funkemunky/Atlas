package cc.funkemunky.bungee.listeners;

import cc.funkemunky.bungee.data.user.User;
import cc.funkemunky.bungee.utils.asm.Init;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

@Init
public class AtlasMsgListener implements Listener {

    public AtlasMsgListener() {
        BungeeCord.getInstance().registerChannel("atlas:out");
    }

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        if(event.getTag().equalsIgnoreCase("atlas:out")) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(event.getData());
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
                                info.sendData("atlas:in", event.getData());
                            }
                        } else {
                            Optional<ServerInfo> infoOptional = BungeeCord.getInstance().getServers().values()
                                    .stream().filter(val -> val.getName().equalsIgnoreCase(server)).findFirst();

                            infoOptional.ifPresent(serverInfo -> serverInfo.sendData("atlas:in", event.getData()));
                        }

                        break;
                    }
                    /*case "ping": {
                        String name = inputStream.readUTF();
                        long time = inputStream.readLong();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream output = new ObjectOutputStream(baos);

                        output.writeUTF("ping");
                        output.writeUTF("ALL");
                        output.writeLong(time);
                        output.writeLong(System.currentTimeMillis() - time);

                        if(name.equalsIgnoreCase("ALL")) {
                            BungeeCord.getInstance().getServers()
                                    .forEach((sname, info) -> info.sendData("atlas:in", baos.toByteArray()));
                        } else {
                            ServerInfo field;

                            if((field = BungeeCord.getInstance().getServerInfo(name)) != null) {
                                field.sendData("atlas:in", baos.toByteArray());
                            }
                        }
                        break;
                    }*/
                    case "version": {

                        UUID uuid = (UUID) inputStream.readObject();

                        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                        ObjectOutputStream dataOut = new ObjectOutputStream(bOut);

                        dataOut.writeUTF("version");

                        User user = User.getUser(uuid);

                        dataOut.writeBoolean(true);
                        dataOut.writeInt(user.version);
                        dataOut.writeObject(uuid);

                        BungeeCord.getInstance().getServers()
                                .forEach((name, info) -> info.sendData("atlas:in", bOut.toByteArray()));
                        break;
                    }
                    case "mods": {
                        UUID uuid = (UUID) inputStream.readObject();
                        User user = User.getUser(uuid);

                        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                        ObjectOutputStream dataOut = new ObjectOutputStream(bOut);

                        dataOut.writeUTF("mods");
                        dataOut.writeObject(uuid);
                        dataOut.writeObject(user.modData != null ? user.modData.getModsMap() : "");

                        BungeeCord.getInstance().getServers()
                                .forEach((name, info) -> info.sendData("atlas:in", bOut.toByteArray()));
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
