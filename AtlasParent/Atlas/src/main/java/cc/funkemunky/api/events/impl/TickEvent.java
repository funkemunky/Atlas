package cc.funkemunky.api.events.impl;

import cc.funkemunky.api.events.AtlasEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TickEvent extends AtlasEvent {
    private int currentTick;
}
