package cc.funkemunky.bungee.handlers;

import cc.funkemunky.bungee.utils.BungeePlayer;
import cc.funkemunky.bungee.utils.BungeeUtils;
import net.md_5.bungee.BungeeCord;

import java.util.List;
import java.util.stream.Collectors;

public class BungeePlayerHandler {

    public BungeePlayerHandler() {

    }

    private void runUpdate() {
        for (String server : BungeeCord.getInstance().getServers().keySet()) {
            List<BungeePlayer> players = getBungeePlayersOnServer(server);

            if(players.size() > 100) {
                players.parallelStream().forEach(pl -> BungeeUtils.sendPluginMessage(pl.getServer(), "playerUpdate", pl.toJson()));
            }
        }
    }

    public List<BungeePlayer> getBungeePlayersOnServer(String server) {
        return BungeeCord.getInstance().getServerInfo(server).getPlayers().stream().map(BungeePlayer::fromProxied).collect(Collectors.toList());
    }
}
