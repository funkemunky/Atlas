package cc.funkemunky.api.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.*;

public class PotionBuilder {

    private String name;
    private List<String> description;
    private final Map<PotionEffect, Boolean> potionEffects;
    private ItemFlag[] itemFlags;
    private final PotionType type;
    private int amount = 1;

    public PotionBuilder(PotionType potionType) {
        this.potionEffects = new HashMap<>();
        this.type = potionType;
    }

    public PotionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PotionBuilder setDescription(String... lines) {
        this.description = new ArrayList<>(Arrays.asList(lines));
        return this;
    }

    public PotionBuilder addEffect(PotionEffect potionEffect, boolean overwrite) {
        potionEffects.put(potionEffect, overwrite);
        return this;
    }

    public PotionBuilder addEffect(PotionEffect potionEffect) {
        potionEffects.put(potionEffect, true);
        return this;
    }

    public PotionBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public PotionBuilder addEffects(HashMap<PotionEffect, Boolean> potionEffects) {
        this.potionEffects.putAll(potionEffects);
        return this;
    }

    public PotionBuilder addItemFlags(ItemFlag... itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public ItemStack build() {


        ItemStack potionItem = new ItemStack(Material.POTION);
        new Potion(type).apply(potionItem);

        potionItem.setAmount(amount);

        PotionMeta pM = (PotionMeta) potionItem.getItemMeta();
        if (name != null) pM.setDisplayName(name);
        if (description != null) pM.setLore(description);
        for (Map.Entry<PotionEffect, Boolean> effect : potionEffects.entrySet()) {
            pM.addCustomEffect(effect.getKey(), effect.getValue());
        }
        if (itemFlags != null) pM.addItemFlags(itemFlags);
        potionItem.setItemMeta(pM);


        return potionItem;
    }

    public static PotionBuilder of(PotionType type) {
        return new PotionBuilder(type);
    }
}