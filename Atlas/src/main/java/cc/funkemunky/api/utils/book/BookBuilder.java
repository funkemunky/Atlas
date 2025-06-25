package cc.funkemunky.api.utils.book;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutCustomPayload;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutSetSlotPacket;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.compiler.msg.ChatBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookBuilder {
    private ItemStack stack;
    private List<BaseComponent[]> pages = new ArrayList<>();
    private String author = "Default Author", title = "Default Title";

    public BookBuilder() {
        this.stack = XMaterial.WRITTEN_BOOK.parseItem();
    }

    public BookBuilder addPage(ChatBuilder builder) {
        pages.add(builder.build());
        return this;
    }

    public BookBuilder setPage(int index, ChatBuilder builder) {
        pages.set(index, builder.build());

        return this;
    }

    public BookBuilder setTitle(String title) {
        this.title = title;

        return this;
    }

    public BookBuilder setAuthor(String author) {
        this.author = author;

        return this;
    }

    public BookBuilder update() {
        BookMeta meta = (BookMeta) stack.getItemMeta();

        meta.setAuthor(author);
        meta.setTitle(title);
        meta.setPages(pages.stream().map(BaseComponent::toLegacyText).collect(Collectors.toList()));

        return this;
    }

    public ItemStack getBook() {
        return stack;
    }

    public BookBuilder sendBook(Player player) {
        int oldSlot = player.getInventory().getHeldItemSlot();
        ItemStack oldItem = player.getInventory().getItem(oldSlot);
        oldSlot+= 36;

        WrappedOutSetSlotPacket setSlot = new WrappedOutSetSlotPacket(0, oldSlot, stack);
        WrappedOutCustomPayload payload = new WrappedOutCustomPayload("MC|BOpen", new byte[]{0,0,0,0});

        TinyProtocolHandler.sendPacket(player, setSlot);
        TinyProtocolHandler.sendPacket(player, payload);
        setSlot.setItem(oldItem);
        setSlot.updateObject();
        TinyProtocolHandler.sendPacket(player, setSlot);

        return this;
    }

    public BookBuilder reset() {
        pages.clear();
        return this;
    }


}
