package cc.funkemunky.api.utils.menu.type;

import cc.funkemunky.api.utils.menu.Menu;
import lombok.NonNull;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 3/28/2018
 */
public class BukkitInventoryHolder implements InventoryHolder {

    private Menu menu;
    private Inventory inventory;

    public BukkitInventoryHolder(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(@NonNull Menu menu) {
        this.menu = menu;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(@NonNull Inventory inventory) {
        this.inventory = inventory;
    }
}
