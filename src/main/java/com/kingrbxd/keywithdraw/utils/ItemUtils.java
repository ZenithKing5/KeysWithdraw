package com.kingrbxd.keywithdraw.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    private static JavaPlugin plugin;

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
    }

    public static ItemStack createItem(Material material, String displayName, List<String> lore, boolean glow) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Set display name
            if (displayName != null && !displayName.isEmpty()) {
                meta.setDisplayName(ColorUtils.colorize(displayName));
            }

            // Set lore
            if (lore != null && !lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(ColorUtils.colorize(line));
                }
                meta.setLore(coloredLore);
            }

            // Add glow effect
            if (glow) {
                meta.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft("unbreaking")), 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack setNBTData(ItemStack item, String key, String subKey, int value) {
        if (item == null || plugin == null) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Store the main key to identify this as a voucher
        container.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, subKey);

        // Store the amount for this specific key
        container.set(new NamespacedKey(plugin, subKey), PersistentDataType.INTEGER, value);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean hasNBTData(ItemStack item, String key) {
        if (item == null || plugin == null) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    public static String getNBTString(ItemStack item, String key) {
        if (item == null || plugin == null) {
            return "";
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return "";
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(new NamespacedKey(plugin, key), PersistentDataType.STRING, "");
    }

    public static int getNBTInt(ItemStack item, String key) {
        if (item == null || plugin == null) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, 0);
    }
}