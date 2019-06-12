package cc.funkemunky.bungee.listeners;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageListener implements Listener {

    @EventHandler
    public void onListen(PluginMessageEvent event) {
        if(!event.getTag().equals("atlasdata:out")) return;

        ByteArrayInputStream input = new ByteArrayInputStream(event.getData());
        DataInputStream dataStream = new DataInputStream(input);

        try {
            String dataTag = dataStream.readUTF();

            switch(dataTag) {
                case "broadcastMessage": {
                    String message = dataStream.readUTF();
                    String permission = dataStream.readUTF();

                    if(permission.length() > 0) {
                        BungeeCord.getInstance().getPlayers().parallelStream().filter(pl -> pl.hasPermission(permission)).forEach(pl -> pl.sendMessage(new TextComponent(message)));
                    } else {
                        BungeeCord.getInstance().getPlayers().parallelStream().forEach(pl -> pl.sendMessage(new TextComponent(message)));
                    }
                    break;
                }
                case "commandBungee": {
                    String command = dataStream.readUTF();

                    BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), command);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
