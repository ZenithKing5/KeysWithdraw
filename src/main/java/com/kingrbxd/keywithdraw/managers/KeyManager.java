package com.kingrbxd.keywithdraw.managers;

import com.kingrbxd.keywithdraw.KeyWithdrawPlugin;
import com.kingrbxd.keywithdraw.utils.ItemUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyManager {

    private final KeyWithdrawPlugin plugin;
    private final Map<String, File> keyFiles;

    public KeyManager(KeyWithdrawPlugin plugin) {
        this.plugin = plugin;
        this.keyFiles = new HashMap<>();
    }

    public void loadAllKeys() {
        keyFiles.clear();
        File keysFolder = new File(plugin.getDataFolder(), "keys");

        if (!keysFolder.exists()) {
            keysFolder.mkdir();
            return;
        }

        File[] files = keysFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files != null) {
            for (File file : files) {
                String keyName = file.getName().replace(".yml", "");
                keyFiles.put(keyName.toLowerCase(), file);
            }
        }

        plugin.getLogger().info("Loaded " + keyFiles.size() + " key types.");
    }

    public boolean createKey(String keyName) {
        if (keyFiles.containsKey(keyName.toLowerCase())) {
            return false;
        }

        File keyFile = new File(plugin.getDataFolder() + "/keys", keyName.toLowerCase() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        // Set default values from config
        config.set("name", plugin.getConfig().getString("default-key-settings.name").replace("%name%", keyName));
        config.set("material", plugin.getConfig().getString("default-key-settings.material"));
        config.set("lore", plugin.getConfig().getStringList("default-key-settings.lore"));
        config.set("glow", plugin.getConfig().getBoolean("default-key-settings.glow"));

        try {
            config.save(keyFile);
            keyFiles.put(keyName.toLowerCase(), keyFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create key file for " + keyName + ": " + e.getMessage());
            return false;
        }
    }

    public boolean deleteKey(String keyName) {
        File keyFile = keyFiles.get(keyName.toLowerCase());

        if (keyFile == null || !keyFile.exists()) {
            return false;
        }

        if (keyFile.delete()) {
            keyFiles.remove(keyName.toLowerCase());
            return true;
        }

        return false;
    }

    public boolean keyExists(String keyName) {
        return keyFiles.containsKey(keyName.toLowerCase());
    }

    public List<String> getAllKeyNames() {
        return new ArrayList<>(keyFiles.keySet());
    }

    public int getPlayerKeyCount(Player player, String keyName) {
        String placeholder = "%excellentcrates_keys_" + keyName + "%";
        String result = PlaceholderAPI.setPlaceholders(player, placeholder);

        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Failed to parse key count for " + player.getName() + ", keyName: " + keyName);
            return 0;
        }
    }

    public boolean withdrawKeys(Player player, String keyName, int amount) {
        int currentKeys = getPlayerKeyCount(player, keyName);

        if (currentKeys < amount) {
            return false;
        }

        // Use the take command format from config
        String takeCommand = plugin.getConfig().getString("crates.take-command", "crates key take %player% %key% %amount%");
        takeCommand = takeCommand
                .replace("%player%", player.getName())
                .replace("%key%", keyName)
                .replace("%amount%", String.valueOf(amount));

        // Execute the command
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("Executing command: " + takeCommand);
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), takeCommand);

        // Create individual vouchers (one per key) instead of a single voucher
        boolean inventoryFull = false;
        for (int i = 0; i < amount; i++) {
            // Create a single key voucher
            ItemStack voucher = createVoucher(keyName, 1);

            // Check if inventory is full
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), voucher);
                inventoryFull = true;
            } else {
                player.getInventory().addItem(voucher);
            }
        }

        // Notify if inventory became full
        if (inventoryFull) {
            plugin.getConfigManager().sendMessage(player, "inventory-full");
        }

        return true;
    }

    public ItemStack createVoucher(String keyName, int amount) {
        File keyFile = keyFiles.get(keyName.toLowerCase());

        if (keyFile == null || !keyFile.exists()) {
            return new ItemStack(Material.PAPER);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(keyFile);

        String displayName = config.getString("name", "&e&l" + keyName + " Key").replace("%name%", keyName);

        // Get material with fallback
        Material material;
        try {
            material = Material.getMaterial(config.getString("material", "TRIPWIRE_HOOK"));
        } catch (Exception e) {
            material = Material.TRIPWIRE_HOOK;
        }

        if (material == null) {
            material = Material.TRIPWIRE_HOOK;
        }

        // Get lore from key config
        List<String> lore = config.getStringList("lore");
        boolean glow = config.getBoolean("glow", true);

        // Create the voucher with simplified lore (no amount/key info)
        ItemStack voucher = ItemUtils.createItem(material, displayName, lore, glow);

        // Add NBT data to store key information
        return ItemUtils.setNBTData(voucher, "KeyWithdrawVoucher", keyName.toLowerCase(), amount);
    }

    public boolean redeemVoucher(Player player, ItemStack voucher) {
        if (!ItemUtils.hasNBTData(voucher, "KeyWithdrawVoucher")) {
            return false;
        }

        String keyName = ItemUtils.getNBTString(voucher, "KeyWithdrawVoucher");
        int amount = ItemUtils.getNBTInt(voucher, keyName.toLowerCase());

        if (amount <= 0 || !keyExists(keyName)) {
            return false;
        }

        // Use the give command format from config
        String giveCommand = plugin.getConfig().getString("crates.give-command", "crates key give %player% %key% %amount%");
        giveCommand = giveCommand
                .replace("%player%", player.getName())
                .replace("%key%", keyName)
                .replace("%amount%", String.valueOf(amount));

        // Execute the command
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("Executing command: " + giveCommand);
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), giveCommand);

        // Send message
        plugin.getConfigManager().sendMessage(player, "key-received",
                "%amount%", String.valueOf(amount),
                "%key_name%", keyName);

        return true;
    }
}