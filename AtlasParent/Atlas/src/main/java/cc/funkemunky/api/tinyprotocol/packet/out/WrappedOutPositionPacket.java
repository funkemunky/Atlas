/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumTeleportFlag;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class WrappedOutPositionPacket extends NMSObject {
    private static final String packet = Server.POSITION;

    private static WrappedClass packetClass = Reflections.getNMSClass(packet);
    private static WrappedField fieldFlags;

    // Fields
    private static FieldAccessor<Double> fieldX = fetchField(packet, double.class, 0);
    private static FieldAccessor<Double> fieldY = fetchField(packet, double.class, 1);
    private static FieldAccessor<Double> fieldZ = fetchField(packet, double.class, 2);
    private static FieldAccessor<Float> fieldYaw = fetchField(packet, float.class, 0);
    private static FieldAccessor<Float> fieldPitch = fetchField(packet, float.class, 1);

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;
    private Set<EnumPlayerTeleportFlags> flags = new HashSet<>();

    public WrappedOutPositionPacket(Object packet, Player player) {
        super(packet, player);
    }

    public WrappedOutPositionPacket(Location location, int teleportAwait, @Nullable  WrappedEnumTeleportFlag... flags) {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_9)) {
            setPacket(packet, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), flags != null ? Arrays.stream(flags).map(WrappedEnumTeleportFlag::getObject).collect(Collectors.toSet()) : new HashSet<>(), teleportAwait);
        } else if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
            setPacket(packet, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), flags != null ? Arrays.stream(flags).map(WrappedEnumTeleportFlag::getObject).collect(Collectors.toSet()) : new HashSet<>());
        } else setPacket(packet, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), flags != null);
    }

    public WrappedOutPositionPacket(Location location, @Nullable WrappedEnumTeleportFlag... flags) {
        this(location, 0, flags);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
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
                flags.add(EnumPlayerTeleportFlags.X_ROT);
            }
            if((flagByte & 0x10) == 0x10) {
                flags.add(EnumPlayerTeleportFlags.Y_ROT);
            }
        } else {
            Set<Enum> vflags = fetch(fieldFlags);

            for (Enum vflag : vflags) {
                flags.add(EnumPlayerTeleportFlags.valueOf(vflag.name()));
            }
        }
    }

    @Override
    public void updateObject() {

    }

    private List<Integer> toOrdinal(Set<Enum> enums) {
        List<Integer> ordinals = new ArrayList<>();
        enums.forEach(e -> ordinals.add(e.ordinal()));
        return ordinals;
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldFlags = packetClass.getFieldByType(byte.class, 0);
        } else {
            fieldFlags = packetClass.getFieldByType(Set.class, 0);
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
