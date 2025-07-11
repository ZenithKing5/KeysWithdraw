package com.kingrbxd.keywithdraw.managers;

import com.kingrbxd.keywithdraw.KeyWithdrawPlugin;
import com.kingrbxd.keywithdraw.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConfigManager {

    private final KeyWithdrawPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(KeyWithdrawPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getPrefix() {
        return ColorUtils.colorize(config.getString("prefix", "&8[&eKeyWithdraw&8] "));
    }

    public void sendMessage(Player player, String path) {
        player.sendMessage(getPrefix() + ColorUtils.colorize(config.getString("messages." + path, "Message not found: " + path)));
    }

    public void sendMessage(Player player, String path, String... placeholders) {
        String message = config.getString("messages." + path, "Message not found: " + path);

        // Replace placeholders
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }

        player.sendMessage(getPrefix() + ColorUtils.colorize(message));
    }

    public String getMessage(String path) {
        return ColorUtils.colorize(config.getString("messages." + path, "Message not found: " + path));
    }

    public String getMessage(String path, String... placeholders) {
        String message = config.getString("messages." + path, "Message not found: " + path);

        // Replace placeholders
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }

        return ColorUtils.colorize(message);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}