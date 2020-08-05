/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProtocolVersion {
    V1_7(4, 4, "v1_7_R3"),
    V1_7_10(5, 5, "v1_7_R4"),
    V1_8(45, 6, "v1_8_R1"),
    V1_8_5(47, 47, "v1_8_R2"),
    V1_8_9(47, 47, "v1_8_R3"),
    V1_9(107, 48, "v1_9_R1"),
    V1_9_1(108, 108, null),
    V1_9_2(109, 109, "v1_9_R2"),
    V1_9_4(110, 110, "v1_9_R2"),
    V1_10(210, 201, "v1_10_R1"),
    V1_11(316, 301, "v1_11_R1"),
    V1_12(335, 317, "v1_12_R1"),
    V1_12_1(338, 336, null),
    V1_12_2(340, 339, "v1_12_R1"),
    V1_13(393, 341, "v1_13_R1"),
    V1_13_1(401, 394, "v1_13_R2"),
    V1_13_2(404, 402, "v1_13_R2"),
    V1_14(477, 441, "v1_14_R1"),
    V1_14_1(480, 478, "v1_14_R1"),
    v1_14_2(485, 481, "v1_14_R1"),
    v1_14_3(490, 486, "v1_14_R1"),
    v1_14_4(498, 491,"v1_14_R1"),
    v1_15(573, 550, "v1_15_R1"),
    v1_15_1(575, 574, "v1_15_R1"),
    v1_15_2(578, 576, "v1_15_R1"),
    v1_16(735, 701, null),
    v1_16_1(736, 736, "v1_16_R1"),
    v1_16_2(744, 738, null),
    UNKNOWN(-1, -1, "UNKNOWN");

    @Getter
    private static ProtocolVersion gameVersion = fetchGameVersion();
    private final int version, snapShotStart;
    @Getter
    private static boolean paper;
    private final String serverVersion;

    private static ProtocolVersion fetchGameVersion() {
        for (ProtocolVersion version : values()) {
            if (version.getServerVersion() != null && version.getServerVersion().equals(Reflection.VERSION))
                return version;
        }
        return UNKNOWN;
    }

    public static ProtocolVersion getVersion(int versionId) {
        ProtocolVersion version = UNKNOWN;
        //We want to go backwards to prioritize the latest revision since some versions have the same protocol ID.
        for (int i = values().length ; i > 0 ; i--) {
            ProtocolVersion current = values()[i];

            if(current.getVersion() <= versionId && current.getSnapShotStart() >= versionId) {
                version = current;
                break;
            }
        }
        return version;
    }

    public boolean isBelow(ProtocolVersion version) {
        return this.getVersion() < version.getVersion();
    }

    public boolean isOrBelow(ProtocolVersion version) {
        return this.getVersion() <= version.getVersion();
    }

    public boolean isAbove(ProtocolVersion version) {
        return this.getVersion() > version.getVersion();
    }

    public boolean isOrAbove(ProtocolVersion version) {
        return this.getVersion() >= version.getVersion();
    }

    static {
        try {
            Class.forName("org.github.paperspigot.PaperSpigotConfig");
            paper = true;
        } catch(Exception e) {
            paper = false;
        }
    }
}
