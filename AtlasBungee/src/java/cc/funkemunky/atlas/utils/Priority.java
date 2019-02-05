package cc.funkemunky.atlas.utils;

import lombok.Getter;

@Getter
public enum Priority {
    LOWEST(5), LOW(4), NORMAL(3), HIGH(2), HIGHEST(1);

    int priority;

    Priority(int priority) {
        this.priority = priority;
    }
}
