package com.kingrbxd.keywithdraw.listeners;

import com.kingrbxd.keywithdraw.KeyWithdrawPlugin;
import com.kingrbxd.keywithdraw.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VoucherListener implements Listener {

    private final KeyWithdrawPlugin plugin;

    public VoucherListener(KeyWithdrawPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (ItemUtils.hasNBTData(item, "KeyWithdrawVoucher")) {
            event.setCancelled(true);

            // Store item details before attempting to redeem
            ItemStack voucherCopy = item.clone();

            // Remove one voucher from hand
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }

            // Log redemption attempt if debug mode is on
            if (plugin.getConfig().getBoolean("debug", false)) {
                plugin.getLogger().info("Player " + player.getName() + " is redeeming a voucher");
            }

            // Redeem the voucher
            if (!plugin.getKeyManager().redeemVoucher(player, voucherCopy)) {
                // If redemption failed, return the voucher
                if (plugin.getConfig().getBoolean("debug", false)) {
                    plugin.getLogger().warning("Voucher redemption failed for " + player.getName());
                }

                // Try to give the item back
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), voucherCopy.asQuantity(1));
                    player.sendMessage(plugin.getConfigManager().getPrefix() + "§cVoucher redemption failed. A voucher was dropped on the ground.");
                } else {
                    player.getInventory().addItem(voucherCopy.asQuantity(1));
                    player.sendMessage(plugin.getConfigManager().getPrefix() + "§cVoucher redemption failed. The voucher has been returned to your inventory.");
                }
            }
        }
    }
}