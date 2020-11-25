package com.dzious.bukkit.enchantcontrol.listener;

import java.util.HashMap;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class InventoryListener implements Listener {
    final EnchantControl plugin;

    public InventoryListener(EnchantControl plugin) 
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerOpenInventory(InventoryOpenEvent e)
    {
        ItemStack[] items = removeEnchants(e.getView().getTopInventory().getContents());
        e.getView().getTopInventory().setContents(items);
        items = removeEnchants(e.getView().getBottomInventory().getContents());
        e.getView().getBottomInventory().setContents(items);
        return;
    }

    ItemStack[] removeEnchants(ItemStack[] items) 
    {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || 
                items[i].getType() == Material.SHULKER_BOX || 
                items[i].getItemMeta() == null || 
                (items[i].getEnchantments().isEmpty() &&
                (!(items[i].getItemMeta() instanceof EnchantmentStorageMeta) ||
                ((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants().isEmpty()))) {
                continue;
            }

            plugin.getLogManager().logDebugConsole("Item[" + i + "] type : " +  items[i].getType());

            Map<Enchantment, Integer> enchantments;
            if (!items[i].getItemMeta().getEnchants().isEmpty()) {
                enchantments = new HashMap<>(items[i].getItemMeta().getEnchants()); ;
            } else {
                enchantments = new HashMap<>(((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants());
            }

            plugin.getLogManager().logDebugConsole("Enchantments (Start) : " + enchantments.toString());

            ItemMeta meta = items[i].getItemMeta();
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                if (!items[i].getItemMeta().getEnchants().isEmpty() && meta.getEnchants().containsKey(enchantment.getKey())) {
                    meta.removeEnchant(enchantment.getKey());
                } else if (((EnchantmentStorageMeta)meta).getStoredEnchants().containsKey(enchantment.getKey())) {
                    ((EnchantmentStorageMeta)meta).removeStoredEnchant(enchantment.getKey());
                }
            }

            Map<Enchantment, Integer> finalEnchantments = new HashMap<>();
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                    plugin.getLogManager().logDebugConsole("Removed : " + ChatColor.GREEN + enchantment.getKey().getKey());
                    continue;
                } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    plugin.getLogManager().logDebugConsole("Replaced : " + ChatColor.GREEN + enchantment.getKey().getKey() + ChatColor.WHITE + ". Level was : " + ChatColor.GREEN + enchantment.getValue() + ChatColor.WHITE + " and is now : " + ChatColor.GREEN);
                    finalEnchantments.put(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
                } else {
                    finalEnchantments.put(enchantment.getKey(),enchantment.getValue());
                }
            }

            plugin.getLogManager().logDebugConsole("Enchantments (End) : " + finalEnchantments.toString());

            if (finalEnchantments.isEmpty() && items[i].getType() == Material.ENCHANTED_BOOK) {
                items[i] = new ItemStack(Material.BOOK);
            } else {
                for (Map.Entry<Enchantment, Integer> enchantment : finalEnchantments.entrySet()) {
                    plugin.getLogManager().logDebugConsole("Enchantment (Loop) : " + finalEnchantments.toString());
                    if (!items[i].getItemMeta().getEnchants().isEmpty()) {
                        meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
                    } else {
                        ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                    }
                }
                items[i].setItemMeta(meta);
                if (!items[i].getItemMeta().getEnchants().isEmpty()) {
                    plugin.getLogManager().logDebugConsole("Enchantments (Meta) : " + items[i].getItemMeta().getEnchants().toString());
                } else {
                    plugin.getLogManager().logDebugConsole("Stored Enchantments (Meta) : " + ((EnchantmentStorageMeta)(items[i].getItemMeta())).getStoredEnchants().toString());
                }
            }
        }
        return (items);
    }
}