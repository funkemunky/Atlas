package cc.funkemunky.bungee.listeners;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream data = new ObjectOutputStream(byteStream);
            data.writeUTF("mods");
            data.writeObject(event.getPlayer().getModList());

            event.getPlayer().sendData("atlasIn", byteStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
