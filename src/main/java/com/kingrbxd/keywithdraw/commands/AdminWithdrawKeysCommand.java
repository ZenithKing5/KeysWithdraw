package com.kingrbxd.keywithdraw.commands;

import com.kingrbxd.keywithdraw.KeyWithdrawPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminWithdrawKeysCommand implements CommandExecutor {

    private final KeyWithdrawPlugin plugin;

    public AdminWithdrawKeysCommand(KeyWithdrawPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("keywithdraw.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cUsage: /" + label + " <create|delete|reload> [keyname]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cUsage: /" + label + " create <keyname>");
                    return true;
                }

                String createKeyName = args[1];

                if (plugin.getKeyManager().keyExists(createKeyName)) {
                    if (sender instanceof Player) {
                        plugin.getConfigManager().sendMessage((Player) sender, "admin.key-exists", "%key_name%", createKeyName);
                    } else {
                        sender.sendMessage(plugin.getConfigManager().getMessage("admin.key-exists", "%key_name%", createKeyName));
                    }
                    return true;
                }

                if (plugin.getKeyManager().createKey(createKeyName)) {
                    if (sender instanceof Player) {
                        plugin.getConfigManager().sendMessage((Player) sender, "admin.key-created", "%key_name%", createKeyName);
                    } else {
                        sender.sendMessage(plugin.getConfigManager().getMessage("admin.key-created", "%key_name%", createKeyName));
                    }
                }
                break;

            case "delete":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cUsage: /" + label + " delete <keyname>");
                    return true;
                }

                String deleteKeyName = args[1];

                if (!plugin.getKeyManager().keyExists(deleteKeyName)) {
                    if (sender instanceof Player) {
                        plugin.getConfigManager().sendMessage((Player) sender, "admin.key-not-exists", "%key_name%", deleteKeyName);
                    } else {
                        sender.sendMessage(plugin.getConfigManager().getMessage("admin.key-not-exists", "%key_name%", deleteKeyName));
                    }
                    return true;
                }

                if (plugin.getKeyManager().deleteKey(deleteKeyName)) {
                    if (sender instanceof Player) {
                        plugin.getConfigManager().sendMessage((Player) sender, "admin.key-deleted", "%key_name%", deleteKeyName);
                    } else {
                        sender.sendMessage(plugin.getConfigManager().getMessage("admin.key-deleted", "%key_name%", deleteKeyName));
                    }
                }
                break;

            case "reload":
                plugin.getConfigManager().loadConfig();
                plugin.getKeyManager().loadAllKeys();

                if (sender instanceof Player) {
                    plugin.getConfigManager().sendMessage((Player) sender, "admin.reloaded");
                } else {
                    sender.sendMessage(plugin.getConfigManager().getMessage("admin.reloaded"));
                }
                break;

            default:
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cUnknown command. Use /" + label + " <create|delete|reload> [keyname]");
                break;
        }

        return true;
    }
}