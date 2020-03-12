package cc.funkemunky.api.utils.menu;

import cc.funkemunky.api.utils.menu.button.Button;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 3/28/2018
 */
public interface Menu extends Iterable<Button> {

    /**
     * Returns the Dimensions of the menu (R, C)
     *
     * @return The menu's row and column count
     */
    MenuDimension getMenuDimension();

    /**
     * Allows for a 'parent' menu
     * (e.g. You have a GUI based punishment history system
     * and the main menu shows: Bans, Mutes, and Warnings.
     * When you choose the option you open a new GUI that lists the information,
     * then on close it re-opens the list menu)
     *
     * @return the {@link Optional< Menu >}
     */
    Optional<Menu> getParent();

    /**
     * Add a {@link Button} to the menu with no specified index
     *
     * @param button The button to be added
     */
    void addItem(Button button);

    /**
     * Sets an {@link Button} in the menu with a specified index
     *
     * @param index  The location of the button to be added
     * @param button The button to be added
     */
    void setItem(int index, Button button);

    /**
     * Fills the entire menu with the specified {@link Button}
     * This is backed by {@link #fillRange(int, int, Button)}
     *
     * @param button The button to use for the procedure
     */
    void fill(Button button);

    /**
     * Fills the menu from a index to the ending index with a {@link Button}
     *
     * @param startingIndex The index to start the procedure
     * @param endingIndex   The index at which the procedure terminates
     * @param button        The button to be used in the procedure
     */
    void fillRange(int startingIndex, int endingIndex, Button button);

    /**
     * Gets the first empty slot in the inventory
     *
     * @return the first empty slot or else -1
     */
    int getFirstEmptySlot();

    /**
     * Checks to see if the index is within the bounds of the {@link MenuDimension}
     *
     * @param index The index to check with
     * @throws IndexOutOfBoundsException If the index is out of bounds this is thrown
     */

    public void checkBounds(int index) throws IndexOutOfBoundsException;

    /**
     * Gets the {@link Button} at the specified index
     *
     * @param index Location to get the button from
     * @return A {@link Optional} that may or may not contain the requested button
     */
    Optional<Button> getButtonByIndex(int index);

    /**
     * Method used to construct the inventory in conjunction with {@link BukkitInventoryHolder}
     *
     * @param initial Is the first build of the menu which needs Bukkit inventory stuff done.
     */
    void buildInventory(boolean initial);

    /**
     * Open's the menu for the specified {@link Player}
     *
     * @param player The player to open the menu for
     */
    void showMenu(Player player);

    /**
     * Closes the menu for the specified {@link Player}
     * Note: This method should call the {@link CloseHandler} if it is present
     *
     * @param player The player to close the menu for
     */
    void close(Player player);

    /**
     * Setter method for the {@link CloseHandler}
     *
     * @param handler The handler to set with
     */
    void setCloseHandler(CloseHandler handler);

    /**
     * Calls the {@link CloseHandler}
     * Note: Should only be called from the {@link #close(Player)} method
     *
     * @param player The player to provide to the backing {@link BiConsumer}
     */
    void handleClose(Player player);

    /**
     * A blank interface that extends {@link BiConsumer} for our usage.
     */
    interface CloseHandler extends BiConsumer<Player, Menu> {
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    class MenuDimension {
        private final int rows, columns;

        public int getSize() {
            return rows * columns;
        }
    }
}
