package cc.funkemunky.velocity;

import cc.funkemunky.velocity.listener.PluginListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.Getter;

import java.util.logging.Logger;

@Plugin(id = "atlasvelocity", name = "AtlasVelocity", description = "The Atlas Velocity addon.",
        version = "${project.version}", authors = "funkemunky")
@Getter
public class AtlasVelocity {

    private final ProxyServer server;
    private final Logger logger;

    private static final String atlasOut = "atlas:in", atlasIn = "atlas:out";
    public static AtlasVelocity INSTANCE;

    private MinecraftChannelIdentifier incoming, outgoing;

    @Inject
    public AtlasVelocity(ProxyServer server, Logger logger) {
        INSTANCE = this;
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        System.out.println("Worked");
        incoming = MinecraftChannelIdentifier.create("atlasvelocity", atlasIn);
        outgoing = MinecraftChannelIdentifier.create("atlasvelocity", atlasOut);

        server.getEventManager().register(this, new PluginListener());
    }

    public void registerChannels() {
        server.getChannelRegistrar().register(incoming, outgoing);
    }

    public void unregisterChannels() {
        server.getChannelRegistrar().unregister(incoming, outgoing);
    }
}
