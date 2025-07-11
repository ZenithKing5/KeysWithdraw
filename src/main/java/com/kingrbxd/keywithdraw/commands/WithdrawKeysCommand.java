package com.kingrbxd.keywithdraw.commands;

import com.kingrbxd.keywithdraw.KeyWithdrawPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawKeysCommand implements CommandExecutor {

    private final KeyWithdrawPlugin plugin;

    public WithdrawKeysCommand(KeyWithdrawPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("keywithdraw.use")) {
            plugin.getConfigManager().sendMessage(player, "no-permission");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "§cUsage: /" + label + " <keyname> <amount>");
            return true;
        }

        String keyName = args[0];

        if (!plugin.getKeyManager().keyExists(keyName)) {
            plugin.getConfigManager().sendMessage(player, "invalid-key", "%key_name%", keyName);
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                plugin.getConfigManager().sendMessage(player, "invalid-amount");
                return true;
            }
        } catch (NumberFormatException e) {
            plugin.getConfigManager().sendMessage(player, "invalid-amount");
            return true;
        }

        int currentKeys = plugin.getKeyManager().getPlayerKeyCount(player, keyName);

        if (currentKeys < amount) {
            plugin.getConfigManager().sendMessage(player, "not-enough-keys",
                    "%current_keys%", String.valueOf(currentKeys),
                    "%key_name%", keyName);
            return true;
        }

        if (plugin.getKeyManager().withdrawKeys(player, keyName, amount)) {
            plugin.getConfigManager().sendMessage(player, "keys-withdrawn",
                    "%amount%", String.valueOf(amount),
                    "%key_name%", keyName);
        }

        return true;
    }
}