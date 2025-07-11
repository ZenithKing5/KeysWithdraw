package com.kingrbxd.keywithdraw.commands;

import com.kingrbxd.keywithdraw.KeyWithdrawPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WithdrawKeysTabCompleter implements TabCompleter {

    private final KeyWithdrawPlugin plugin;
    private final List<String> adminSubCommands = Arrays.asList("create", "delete", "reload");

    public WithdrawKeysTabCompleter(KeyWithdrawPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("withdrawkeys") || alias.equalsIgnoreCase("wk")) {
            if (!sender.hasPermission("keywithdraw.use")) {
                return completions;
            }

            if (args.length == 1) {
                // Suggest key names
                List<String> keys = plugin.getKeyManager().getAllKeyNames();
                return filterCompletions(keys, args[0]);
            } else if (args.length == 2) {
                // Suggest some common amounts
                return filterCompletions(Arrays.asList("1", "5", "10", "64"), args[1]);
            }
        } else if (command.getName().equalsIgnoreCase("adminwithdrawkeys") || alias.equalsIgnoreCase("awk")) {
            if (!sender.hasPermission("keywithdraw.admin")) {
                return completions;
            }

            if (args.length == 1) {
                // Suggest subcommands
                return filterCompletions(adminSubCommands, args[0]);
            } else if (args.length == 2) {
                String subCommand = args[0].toLowerCase();

                if (subCommand.equals("delete")) {
                    // Suggest existing keys for deletion
                    List<String> keys = plugin.getKeyManager().getAllKeyNames();
                    return filterCompletions(keys, args[1]);
                }
            }
        }

        return completions;
    }

    private List<String> filterCompletions(List<String> completions, String arg) {
        if (arg.isEmpty()) {
            return completions;
        }

        String lowerArg = arg.toLowerCase();
        return completions.stream()
                .filter(completion -> completion.toLowerCase().startsWith(lowerArg))
                .collect(Collectors.toList());
    }
}