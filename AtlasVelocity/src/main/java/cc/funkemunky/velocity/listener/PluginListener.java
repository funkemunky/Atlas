package cc.funkemunky.velocity.listener;

import cc.funkemunky.velocity.AtlasVelocity;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;

import java.io.*;

public class PluginListener {

    @Subscribe
    public void onEvent(PluginMessageEvent event) {
        if(event.getIdentifier().equals(AtlasVelocity.INSTANCE.getIncoming())) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(event.getData());
                ObjectInputStream inputStream = new ObjectInputStream(bis);

                String type = inputStream.readUTF();

                switch(type) {
                    case "heartbeat": {
                        if(inputStream.available() > 1)
                            switch(inputStream.readUTF()) {
                                case "reloadChannels": {
                                    AtlasVelocity.INSTANCE.getServer().getChannelRegistrar()
                                            .unregister(AtlasVelocity.INSTANCE.getIncoming(),
                                                    AtlasVelocity.INSTANCE.getOutgoing());
                                    AtlasVelocity.INSTANCE.getServer().getChannelRegistrar()
                                            .register(AtlasVelocity.INSTANCE.getIncoming(),
                                                    AtlasVelocity.INSTANCE.getOutgoing());
                                    break;
                                }
                            }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);

                        oos.writeUTF("heartbeat");
                        oos.close();

                        event.getTarget().sendPluginMessage(AtlasVelocity.INSTANCE.getOutgoing(), baos.toByteArray());
                        break;
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
