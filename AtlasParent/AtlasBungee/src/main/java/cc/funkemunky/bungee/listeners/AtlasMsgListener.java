package cc.funkemunky.bungee.listeners;

import cc.funkemunky.bungee.utils.asm.Init;
import com.sun.corba.se.impl.ior.ObjectAdapterIdNumber;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import sun.plugin2.message.PluginMessage;

import java.io.*;
import java.util.Optional;

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
                DataInputStream inputStream = new DataInputStream(bis);

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
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
