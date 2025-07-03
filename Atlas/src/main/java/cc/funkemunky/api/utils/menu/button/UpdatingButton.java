package cc.funkemunky.api.utils.menu.button;

import cc.funkemunky.api.utils.RunUtils;
import cc.funkemunky.api.utils.menu.Menu;
import cc.funkemunky.api.utils.menu.type.impl.ChestMenu;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Supplier;

public class UpdatingButton extends Button {

    private final Supplier<ItemStack> update;
    private BukkitTask updateTask;
    private final int tick;
    @Setter
    private Menu menu;
    @Setter
    private int index;

    public UpdatingButton(boolean moveable, ClickAction consumer,
                          Supplier<ItemStack> update, int tick) {
        super(moveable, update.get(), consumer);

        this.update = update;
        this.tick = tick;
    }

    public UpdatingButton(boolean moveable, Supplier<ItemStack> update, int tick) {
        super(moveable, update.get());

        this.update = update;
        this.tick = tick;
    }

    public void startUpdate() {
        if(updateTask != null) return;

        updateTask = RunUtils.taskTimer(() -> {
            if(menu == null) {
                updateTask.cancel();
                updateTask = null;
                return;
            }

            setStack(update.get());
            menu.setItem(index, this);

            if(menu instanceof ChestMenu) {
                ((ChestMenu)menu).getHolder().getInventory().setItem(index, getStack());
            }
        }, tick, tick);
    }

    public void cancelUpdate() {
        if(updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }
}
