package cc.funkemunky.api.utils;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

@AllArgsConstructor
public class Setting<T> {
    public final String name;
    private T value;
    public final T[] options;
    private final Function<Player, T> getValue;
    public final BiConsumer<Player, T> onValueChange;

    public Setting(String name, Function<Player, T> getValue, BiConsumer<Player, T> onValueChange, T... options) {
        this.name = name;
        this.getValue = getValue;
        this.onValueChange = onValueChange;
        this.options = options;
    }

    public Setting(String name, BiConsumer<Player, T> onValueChange, T... options) {
        this.name = name;
        this.value = options[0];
        this.onValueChange = onValueChange;
        this.getValue = (pl) -> this.value;
        this.options = options;
    }

    public void setValue(Player player, Object value) {
        onValueChange.accept(player, (T) value);
    }

    public T getValue(Player player) {
        return getValue.apply(player);
    }
}
