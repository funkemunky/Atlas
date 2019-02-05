package cc.funkemunky.api.event.system;

import lombok.Getter;

public enum EnumPriority {
    LOWEST(0), LOW(1), NORMAL(2), HIGH(3), HIGHEST(4);

    @Getter
    private int priority;

    EnumPriority(int priority) {
        this.priority = priority;
    }
}
