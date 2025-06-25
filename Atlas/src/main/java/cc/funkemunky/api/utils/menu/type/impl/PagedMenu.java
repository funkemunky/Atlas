package cc.funkemunky.api.utils.menu.type.impl;

import cc.funkemunky.api.utils.ItemBuilder;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.menu.Menu;
import cc.funkemunky.api.utils.menu.button.Button;
import cc.funkemunky.api.utils.menu.type.BukkitInventoryHolder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class PagedMenu implements Menu {
    @Getter
    @Setter
    String title;
    final MenuDimension dimension;
    @Setter
    private Menu parent;
    @Getter
    @Setter
    private int currentPage = 1;
    @Getter
    BukkitInventoryHolder holder;
    public List<Button> contents;
    private CloseHandler closeHandler;
    public PagedMenu(@NonNull String title, int size) {
        this.title = title.length() > 32 ? title.substring(0, 32) : title;
        if (size <= 0 || size > 6) {
            throw new IndexOutOfBoundsException("A menu can only have between 1 & 6 for a size (rows)");
        }
        this.dimension = new MenuDimension(size, 9);
        this.contents = new ArrayList<>();
    }

    @Override
    public MenuDimension getMenuDimension() {
        return dimension;
    }

    @Override
    public void addItem(Button button) {
        contents.add(button);
    }

    @Override
    public void setItem(int index, Button button) {
        contents.set(index, button);
    }

    @Override
    public void fill(Button button) {
        fillRange(0, dimension.getSize(), button);
    }

    @Override
    public void fillRange(int startingIndex, int endingIndex, Button button) {
        IntStream.range(startingIndex, endingIndex)
                .filter(i -> contents.get(i) == null || contents.get(i).getStack().getType()
                        .equals(XMaterial.AIR.parseMaterial()))
                .forEach(i -> setItem(i, button));
    }

    @Override
    public int getFirstEmptySlot() {
        return contents.size();
    }

    @Override
    public void checkBounds(int index) {

    }

    @Override
    public Optional<Button> getButtonByIndex(int index) {
        if(index >= contents.size() - 1) return Optional.empty();

        return Optional.ofNullable(contents.get(index));
    }

    @Override
    public void buildInventory(boolean initial) {
        if (initial) {
            this.holder = new BukkitInventoryHolder(this);
            holder.setInventory(Bukkit.createInventory(holder, dimension.getSize(), title));
        }
        holder.getInventory().clear();
        int size = (dimension.getRows() - 1) * dimension.getColumns();
        if(currentPage > 1) {
            val previous = new ItemBuilder(Material.BOOK).amount(currentPage - 1).name("&ePrevious Page").build();
            setItem(dimension.getSize() * currentPage - 6, new Button(false, previous,
                    (player, info) -> {
                        currentPage--;
                        buildInventory(false);
                    }));
            holder.getInventory().setItem(dimension.getSize() - 6, previous);
        }
        val next = new ItemBuilder(Material.BOOK).amount(currentPage + 1).name("&eNext Page").build();
        setItem(dimension.getSize() * currentPage - 4,  new Button(false, next, (player, info) -> {
            currentPage++;
            buildInventory(false);
        }));
        holder.getInventory().setItem(dimension.getSize() - 4, next);
        AtomicInteger index = new AtomicInteger(0);
        IntStream.range(Math.min(contents.size(), size * (currentPage - 1)),
                Math.min(contents.size(), size * currentPage))
                .forEach(i -> {
                    Button button = contents.get(i);
                    if (button != null) {
                        holder.getInventory().setItem(index.getAndIncrement(), button.getStack());
                    }
                });


    }

    @Override
    public void showMenu(Player player) {
        if (holder == null) {
            buildInventory(true);
        } else {
            buildInventory(false);
        }
        player.openInventory(holder.getInventory());
    }

    @Override
    public void close(Player player) {
        player.closeInventory();
        handleClose(player);
    }

    @Override
    public void setCloseHandler(CloseHandler handler) {
        this.closeHandler = handler;
    }

    @Override
    public void handleClose(Player player) {
        if (closeHandler != null) {
            closeHandler.accept(player, this);
        }
    }

    @Override
    public Optional<Menu> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public Iterator<Button> iterator() {
        return contents.iterator();
    }
}
