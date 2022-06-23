/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Deprecated
public class WrappedOutPositionPacket extends NMSObject {
    private static final String packet = Server.POSITION;

    private static WrappedClass packetClass = Reflections.getNMSClass(packet);
    private static WrappedField fieldFlags;
    private static final WrappedClass enumTeleportFlag;

    // Fields
    private static FieldAccessor<Double> fieldX = fetchField(packet, double.class, 0);
    private static FieldAccessor<Double> fieldY = fetchField(packet, double.class, 1);
    private static FieldAccessor<Double> fieldZ = fetchField(packet, double.class, 2);
    private static FieldAccessor<Float> fieldYaw = fetchField(packet, float.class, 0);
    private static FieldAccessor<Float> fieldPitch = fetchField(packet, float.class, 1);
    private static WrappedField fieldTeleportAwait;

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;
    private int teleportAwait = 0;
    private Set<EnumPlayerTeleportFlags> flags;

    public WrappedOutPositionPacket() {
        this.flags = new HashSet<>();
    }

    public WrappedOutPositionPacket(Object packet, Player player) {
        super(packet, player);
    }

    public WrappedOutPositionPacket(Location location, int teleportAwait, EnumPlayerTeleportFlags... flags) {
        setObject(packetClass.getConstructor().newInstance());
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.teleportAwait = teleportAwait;
        this.flags = Arrays.stream(flags).collect(Collectors.toSet());

        updateObject();
    }

    public WrappedOutPositionPacket(Location location, EnumPlayerTeleportFlags... flags) {
        this(location, 0, flags);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(flags == null) this.flags = new HashSet<>();
        x = fetch(fieldX);
        y = fetch(fieldY);
        z = fetch(fieldZ);
        yaw = fetch(fieldYaw);
        pitch = fetch(fieldPitch);

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            byte flagByte = fetch(fieldFlags);

            if((flagByte & 0x01) == 0x01) {
                flags.add(EnumPlayerTeleportFlags.X);
            }
            if((flagByte & 0x02) == 0x02) {
                flags.add(EnumPlayerTeleportFlags.Y);
            }
            if((flagByte & 0x04) == 0x04) {
                flags.add(EnumPlayerTeleportFlags.Z);
            }
            if((flagByte & 0x08) == 0x08) {
                flags.add(EnumPlayerTeleportFlags.Y_ROT);
            }
            if((flagByte & 0x10) == 0x10) {
                flags.add(EnumPlayerTeleportFlags.X_ROT);
            }
        } else {
            Set<Enum> vflags = fetch(fieldFlags);

            for (Enum vflag : vflags) {
                flags
                        .add(
                                EnumPlayerTeleportFlags
                                .valueOf(
                                        vflag.name()));
            }

            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                teleportAwait = fetch(fieldTeleportAwait);
            }
        }
    }

    @Override
    public void updateObject() {
        set(fieldX, x);
        set(fieldY, y);
        set(fieldZ, z);
        set(fieldYaw, yaw);
        set(fieldPitch, pitch);

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            set(fieldFlags, flags.stream().map(f -> enumTeleportFlag.getEnum(f.name()))
                    .collect(Collectors.toSet()));

            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                set(fieldTeleportAwait, teleportAwait);
            }
        } else {
            byte flagByte = 0;

            for (EnumPlayerTeleportFlags flag : flags) {
                switch(flag) {
                    case X:
                        flagByte|= 0x01;
                        break;
                    case Y:
                        flagByte|= 0x02;
                        break;
                    case Z:
                        flagByte|= 0x04;
                        break;
                    case Y_ROT:
                        flagByte|= 0x08;
                        break;
                    case X_ROT:
                        flagByte|= 0x10;
                        break;
                }
            }
            set(fieldFlags, flagByte);
        }
    }

    private List<Integer> toOrdinal(Set<Enum> enums) {
        List<Integer> ordinals = new ArrayList<>();
        enums.forEach(e -> ordinals.add(e.ordinal()));
        return ordinals;
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldFlags = packetClass.getFieldByType(byte.class, 0);
            enumTeleportFlag = null;
        } else {
            enumTeleportFlag = Reflections.getNMSClass("PacketPlayOutPosition$EnumPlayerTeleportFlags");
            fieldFlags = packetClass.getFieldByType(Set.class, 0);

            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                fieldTeleportAwait = fetchField(packetClass, int.class, 0);
            }
        }
    }

    public enum EnumPlayerTeleportFlags {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private int f;

        private EnumPlayerTeleportFlags(int var3) {
            this.f = var3;
        }

        private int a() {
            return 1 << this.f;
        }

        private boolean b(int var1) {
            return (var1 & this.a()) == this.a();
        }

        public static Set<EnumPlayerTeleportFlags> a(int var0) {
            EnumSet var1 = EnumSet.noneOf(EnumPlayerTeleportFlags.class);
            EnumPlayerTeleportFlags[] var2 = values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               EnumPlayerTeleportFlags var5 = var2[var4];
                if (var5.b(var0)) {
                    var1.add(var5);
                }
            }

            return var1;
        }

        public static int a(Set<EnumPlayerTeleportFlags> var0) {
            int var1 = 0;

            EnumPlayerTeleportFlags var3;
            for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 |= var3.a()) {
                var3 = (EnumPlayerTeleportFlags)var2.next();
            }

            return var1;
        }
    }
}
