package cc.funkemunky.api.utils.handlers;

import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerSizeHandlerLegacy implements PlayerSizeHandler {

    @Override
    public double height(Player player) {
        return 1.8;
    }

    @Override
    public double width(Player player) {
        return 0.6;
    }

    @Override
    public boolean isGliding(Player player) {
        return false;
    }

    public SimpleCollisionBox bounds(Player player) {
        Location l = player.getLocation();
        return new SimpleCollisionBox().offset(l.getX(), l.getY(), l.getZ()).expand(.3,0,.3).expandMax(0,1.8,0);
    }
    public SimpleCollisionBox bounds(Player player, double x, double y, double z) {
        return new SimpleCollisionBox().offset(x,y,z).expand(.3,0,.3).expandMax(0,1.8,0);
    }

}
