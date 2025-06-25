package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;

@Init
public class TPConfig {

    @ConfigSetting(path = "protocol", name = "useLegacy")
    public static boolean legacyEnabled = false;
}
