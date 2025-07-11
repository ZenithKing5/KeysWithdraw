package com.kingrbxd.keywithdraw;

import com.kingrbxd.keywithdraw.commands.AdminWithdrawKeysCommand;
import com.kingrbxd.keywithdraw.commands.WithdrawKeysCommand;
import com.kingrbxd.keywithdraw.commands.WithdrawKeysTabCompleter;
import com.kingrbxd.keywithdraw.listeners.VoucherListener;
import com.kingrbxd.keywithdraw.managers.ConfigManager;
import com.kingrbxd.keywithdraw.managers.KeyManager;
import com.kingrbxd.keywithdraw.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class KeyWithdrawPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private KeyManager keyManager;

    @Override
    public void onEnable() {
        // Create directories
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File keysFolder = new File(getDataFolder(), "keys");
        if (!keysFolder.exists()) {
            keysFolder.mkdir();
        }

        // Initialize managers
        configManager = new ConfigManager(this);
        keyManager = new KeyManager(this);

        // Initialize ItemUtils
        ItemUtils.init(this);

        // Check for PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().severe("PlaceholderAPI not found! This plugin requires PlaceholderAPI to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands
        getCommand("withdrawkeys").setExecutor(new WithdrawKeysCommand(this));
        getCommand("adminwithdrawkeys").setExecutor(new AdminWithdrawKeysCommand(this));

        // Register tab completers
        WithdrawKeysTabCompleter tabCompleter = new WithdrawKeysTabCompleter(this);
        getCommand("withdrawkeys").setTabCompleter(tabCompleter);
        getCommand("adminwithdrawkeys").setTabCompleter(tabCompleter);

        // Register listeners
        getServer().getPluginManager().registerEvents(new VoucherListener(this), this);

        // Load all keys
        keyManager.loadAllKeys();

        getLogger().info("KeyWithdraw has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("KeyWithdraw has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }
}