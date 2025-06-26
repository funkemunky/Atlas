package cc.funkemunky.api.utils.menu.preset;

import cc.funkemunky.api.utils.ItemBuilder;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.menu.button.Button;
import cc.funkemunky.api.utils.menu.preset.button.FillerButton;
import cc.funkemunky.api.utils.menu.type.impl.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class ConfirmationMenu extends ChestMenu {

    public ConfirmationMenu(String message, BiConsumer<Player, Boolean> function) {
        super(message, 1);
        fill(new FillerButton());
        setItem(2, new Button(false, new ItemBuilder(XMaterial.LIME_DYE.parseMaterial()).durability(10)
                .name(ChatColor.GREEN + "Accept").build(), ((player, buttonClickTypeInformationPair) -> {
            function.accept(player, true);
            setCloseHandler(null);
            close(player);
        })));
        setItem(6, new Button(false, new ItemBuilder(XMaterial.RED_DYE.parseMaterial())
                .durability(1).name(ChatColor.RED + "Deny").build(), (((player, buttonClickTypeInformationPair) -> {
            function.accept(player, false);
            close(player);
        }))));
        setCloseHandler((player, buttons) -> function.accept(player, false));
    }
}
