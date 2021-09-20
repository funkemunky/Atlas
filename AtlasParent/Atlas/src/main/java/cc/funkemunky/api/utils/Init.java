package cc.funkemunky.api.utils;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
    Priority priority() default Priority.NORMAL;
    boolean commands() default false;
    String[] requirePlugins() default {};
    RequireType requireType() default RequireType.ALL;
    ProtocolVersion requireProtocolVersion() default ProtocolVersion.V1_7;

    enum RequireType {
        ALL,
        ONE
    }
}