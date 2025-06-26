package cc.funkemunky.api.utils.menu;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.menu.button.Button;
import cc.funkemunky.api.utils.menu.button.ClickAction;
import cc.funkemunky.api.utils.menu.button.UpdatingButton;
import cc.funkemunky.api.utils.menu.type.BukkitInventoryHolder;
import cc.funkemunky.api.utils.menu.type.impl.ChestMenu;
import cc.funkemunky.api.utils.menu.type.impl.ValueMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 2/21/2018
 */
@Init
public class MenuListener implements Listener {

    public static Map<AnvilInventory, ValueMenu> anvils = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    private void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        final InventoryView inventoryView = event.getView();
        final Inventory inventory = inventoryView.getTopInventory();

        if (inventory.getHolder() instanceof BukkitInventoryHolder && ((Player) event.getWhoClicked()).isOnline()) {
            Menu menu = ((BukkitInventoryHolder) inventory.getHolder()).getMenu();

            event.setCancelled(true);

            if (menu != null) {
                final ItemStack stack = event.getCurrentItem();
                if ((stack == null || stack.getType() == XMaterial.AIR.parseMaterial()))
                    return;

                int slot = event.getSlot();
                if (slot >= 0 && slot <= menu.getMenuDimension().getSize()) {

                    Optional<Button> buttonOptional = menu.getButtonByIndex(slot);

                    buttonOptional.ifPresent(button -> {

                        if (button.getConsumer() == null) { // Allows for Buttons to not have an action.
                            return;
                        }
                        button.getConsumer().accept((Player) event.getWhoClicked(),
                                new ClickAction.InformationPair(button, event.getClick(), menu));

                        if (!button.isMoveable()) {
                            event.setResult(Event.Result.DENY);
                            event.setCancelled(true);
                        }
                    });
                }
            }
        }
        if(!event.getAction().equals(InventoryAction.NOTHING)
                && inventory instanceof AnvilInventory
                && anvils.containsKey(inventory)) {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        final InventoryView inventoryView = event.getView();
        final Inventory inventory = inventoryView.getTopInventory();

        if (inventory.getHolder() instanceof BukkitInventoryHolder) {
            Menu menu = ((BukkitInventoryHolder) inventory.getHolder()).getMenu();

            if (menu != null) {
                menu.handleClose((Player) event.getPlayer());
                if(menu instanceof ChestMenu) {
                    ChestMenu cmenu = (ChestMenu) menu;

                    for (int i = 0; i < cmenu.contents.length; i++) {
                        Button button = cmenu.contents[i];

                        if(button instanceof UpdatingButton) {
                            ((UpdatingButton)button).cancelUpdate();
                        }
                    }
                }

                menu.getParent().ifPresent(buttons -> new BukkitRunnable() {
                    public void run() {
                        if (event.getPlayer().getOpenInventory() == null
                                || (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder()
                                instanceof BukkitInventoryHolder))) {
                            buttons.showMenu((Player) event.getPlayer());
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Atlas.getInstance(), 2L, 0L));
            }
        }
        if(inventory instanceof AnvilInventory && anvils.containsKey(inventory)) {
            AnvilInventory anvil = (AnvilInventory) inventory;

            WrappedMethod method = new WrappedClass(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_11)
                    ? AnvilInventory.class : Inventory.class)
                    .getMethod(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_11)
                                    ? "getRenameText" : "getName");
            anvils.get(anvil).consumer.accept(event.getPlayer(), Color.translate(method.invoke(anvil)));
            anvils.remove(anvil);
        }
    }
}
