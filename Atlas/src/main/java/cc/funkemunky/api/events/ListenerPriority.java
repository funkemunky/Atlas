package cc.funkemunky.api.events;

import lombok.Getter;

public enum ListenerPriority {
    NONE(2), LOWEST(0), LOW(1), NORMAL(2), HIGH(3), HIGHEST(4);

    @Getter
    private int priority;

    ListenerPriority(int priority) {
        this.priority = priority;
    }
}
