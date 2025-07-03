package cc.funkemunky.api.handlers.protocolsupport;

import cc.funkemunky.api.handlers.protocolsupport.impl.NoAPI;
import cc.funkemunky.api.handlers.protocolsupport.impl.ProtocolSupport;
import cc.funkemunky.api.handlers.protocolsupport.impl.ViaVersionAPI;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.Instance;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@Init
public class ProtocolAPI {

    public Map<String, Integer> protocolVersionByIP = new HashMap<>();

    public static Protocol INSTANCE;
    @Instance
    public static ProtocolAPI classInstance;

    public ProtocolAPI() {
        if(Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
            INSTANCE = new ViaVersionAPI();
        } else if(Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")) {
            INSTANCE = new ProtocolSupport();
        } else INSTANCE = new NoAPI();
    }
}
