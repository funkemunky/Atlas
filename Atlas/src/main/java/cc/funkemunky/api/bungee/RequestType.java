package cc.funkemunky.api.bungee;

import lombok.Getter;

public enum RequestType {
    OBJECT("object"), DATABASE("database");

    @Getter
    String typeName;

    RequestType(String typeName) {
        this.typeName = typeName;
    }
}
