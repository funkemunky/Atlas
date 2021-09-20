package cc.funkemunky.api.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class KLocation {
    public double x, y, z;
    public float yaw, pitch;
    public long timeStamp;

    public KLocation(double x, double y, double z, float yaw, float pitch, long timeStamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timeStamp = timeStamp;
    }

    public KLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timeStamp = System.currentTimeMillis();
    }

    public KLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.timeStamp = System.currentTimeMillis();
    }

    public KLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.timeStamp = System.currentTimeMillis();
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public KLocation clone() {
        return new KLocation(x, y, z, yaw, pitch, timeStamp);
    }
}
