package cc.funkemunky.api.utils.messages;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ModifierType {
    CLICKABLE("clickEvent"), HOVER("hoverEvent"), HOVER_ITEM("hoverEvent");

    public final String value;
}
