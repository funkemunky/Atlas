package cc.funkemunky.bungee.listeners;

import cc.funkemunky.bungee.AtlasBungee;
import cc.funkemunky.bungee.data.user.User;
import cc.funkemunky.bungee.utils.Color;
import cc.funkemunky.bungee.utils.asm.Init;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Init
public class AtlasMsgListener implements Listener {

    public AtlasMsgListener() {
        BungeeCord.getInstance().registerChannel(AtlasBungee.INSTANCE.outChannel);
        BungeeCord.getInstance().registerChannel(AtlasBungee.INSTANCE.inChannel);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeUTF("heartbeat");
            oos.close();

            ProxyServer.getInstance().getServers().values()
                    .forEach(si -> si.sendData("atlas:in", baos.toByteArray(), true));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        if(event.getTag().equalsIgnoreCase("MC|Brand")
                || event.getTag().equalsIgnoreCase("minecraft:brand")) {
            if(event.getSender() instanceof ProxiedPlayer) {
                String brand = new String(event.getData(), StandardCharsets.UTF_8);

                ProxiedPlayer player = (ProxiedPlayer) event.getSender();
                User user = User.getUser(player.getUniqueId());
                user.brand = brand;
                user.legacy = !event.getTag().contains(":");
            }
        }
        if(event.getTag().equalsIgnoreCase("atlas:out")) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(event.getData());
                ObjectInputStream inputStream = new ObjectInputStream(bis);

                String type = inputStream.readUTF();

                switch(type) {
                    case "heartbeat": {
                        if(inputStream.available() > 1)
                        switch(inputStream.readUTF()) {
                            case "reloadChannels": {
                                BungeeCord.getInstance().unregisterChannel(AtlasBungee.INSTANCE.outChannel);
                                BungeeCord.getInstance().unregisterChannel(AtlasBungee.INSTANCE.inChannel);
                                BungeeCord.getInstance().registerChannel(AtlasBungee.INSTANCE.outChannel);
                                BungeeCord.getInstance().registerChannel(AtlasBungee.INSTANCE.inChannel);
                                break;
                            }
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);

                        oos.writeUTF("heartbeat");
                        oos.close();

                        ProxyServer.getInstance().getServers().values().stream()
                                .filter(server -> server.getAddress().equals(event.getSender().getAddress()))
                                .forEach(si -> si.sendData("atlas:in", baos.toByteArray()));
                        break;
                    }
                    case "sendObjects": {
                        String serverField = (String)inputStream.readObject();

                        if(!serverField.contains("_")) {
                            return;
                        }
                        String server = serverField.split("_")[0];

                        if(server.equalsIgnoreCase("all")) {
                            for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
                                info.sendData("atlas:in", event.getData());
                            }
                        } else {
                            Optional<ServerInfo> infoOptional = ProxyServer.getInstance().getServers().values()
                                    .stream().filter(val -> val.getName().equalsIgnoreCase(server)).findFirst();

                            infoOptional.ifPresent(serverInfo -> serverInfo.sendData("atlas:in", event.getData(), true));
                        }

                        break;
                    }
                    case "broadcastMsg": {
                        String message = (String) inputStream.readObject();
                        String permission = (String) inputStream.readObject();

                        if(permission.equals("")) {
                            BungeeCord.getInstance().broadcast((new TextComponent(Color.translate(message))));
                        } else {
                            BungeeCord.getInstance().getPlayers().stream()
                                    .filter(player -> player.hasPermission(permission))
                                    .forEach(player -> player.sendMessage(new TextComponent(Color.translate(message))));
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
                            ProxyServer.getInstance().getServers()
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
                        dataOut.writeUTF(user.brand);
                        dataOut.writeBoolean(user.legacy);
                        dataOut.close();

                        ProxyServer.getInstance().getServers()
                                .forEach((name, info) -> info.sendData(AtlasBungee.INSTANCE.outChannel,
                                        bOut.toByteArray(), true));
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
                        dataOut.close();

                        ProxyServer.getInstance().getServers()
                                .forEach((name, info) -> {
                                    info.sendData(AtlasBungee.INSTANCE.outChannel, bOut.toByteArray(), true);
                                });
                        break;
                    }
                    case "commandBungee": {
                        String command = (String) inputStream.readObject();

                        ProxyServer.getInstance().getPluginManager()
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
