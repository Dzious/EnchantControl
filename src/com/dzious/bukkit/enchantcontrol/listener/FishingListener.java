package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class FishingListener implements Listener {
    private final EnchantControl plugin;

    public FishingListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFishTreasure(PlayerFishEvent e)
    {
        if (e.getCaught() == null || !(e.getCaught() instanceof Item)) {
            if (e.getCaught() == null)
                plugin.getLogManager().logDebugConsole("Nothing caught");
            else
                plugin.getLogManager().logDebugConsole("caught thing was not an enchantable item");
            return;
        }

        Item item = (Item)(e.getCaught());
        if (item.getItemStack().getItemMeta() == null ||
            (item.getItemStack().getItemMeta().getEnchants().isEmpty() &&
            (!(item.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta) ||
            ((EnchantmentStorageMeta)(item.getItemStack().getItemMeta())).getStoredEnchants().isEmpty()))) {
            plugin.getLogManager().logDebugConsole("Item Caught Is not Enchanted");
            return;
        }

        plugin.getLogManager().logDebugConsole("Caught : " + ((Item)e.getCaught()).getItemStack());
        
        Map<Enchantment, Integer> enchantments;
        if (!item.getItemStack().getItemMeta().getEnchants().isEmpty()) {
            enchantments = new HashMap<>(item.getItemStack().getItemMeta().getEnchants());
        } else {
            enchantments = new HashMap<>(((EnchantmentStorageMeta)item.getItemStack().getItemMeta()).getStoredEnchants());
        }

        ItemMeta meta = item.getItemStack().getItemMeta();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (!item.getItemStack().getItemMeta().getEnchants().isEmpty() && meta.getEnchants().containsKey(enchantment.getKey())) {
                meta.removeEnchant(enchantment.getKey());
            } else if (((EnchantmentStorageMeta)meta).getStoredEnchants().containsKey(enchantment.getKey())) {
                ((EnchantmentStorageMeta)meta).removeStoredEnchant(enchantment.getKey());
            }
        }

        Map<Enchantment, Integer> finalEnchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                plugin.getLogManager().logDebugConsole("Removed : " + ChatColor.GREEN + enchantment.getKey().getKey());
                Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(item.getItemStack(), enchantments.keySet().toArray());
                Integer enchantmentLevel = enchantment.getValue();

                if (newEnchantment != null) {
                    if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                        enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
                        finalEnchantments.put(newEnchantment, enchantmentLevel);
                }
            } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                plugin.getLogManager().logDebugConsole("Replaced : " + ChatColor.GREEN + enchantment.getKey().getKey() + ChatColor.WHITE + ". Level was : " + ChatColor.GREEN + enchantment.getValue() + ChatColor.WHITE + " and is now : " + ChatColor.GREEN);
                finalEnchantments.put(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
            } else {
                finalEnchantments.put(enchantment.getKey(),enchantment.getValue());
            }
        }

        plugin.getLogManager().logDebugConsole("Enchantments (End) : " + finalEnchantments.toString());

        if (finalEnchantments.isEmpty() && item.getItemStack().getType() == Material.ENCHANTED_BOOK) {
            ((Item)(e.getCaught())).setItemStack(new ItemStack(Material.BOOK));
        } else {
            for (Map.Entry<Enchantment, Integer> enchantment : finalEnchantments.entrySet()) {
                plugin.getLogManager().logDebugConsole("Enchantment (Loop) : " + finalEnchantments.toString());
                if (!item.getItemStack().getItemMeta().getEnchants().isEmpty()) {
                    meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
                } else {
                    ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                }
            }
            ((Item)(e.getCaught())).getItemStack().setItemMeta(meta);
            if (!item.getItemStack().getItemMeta().getEnchants().isEmpty()) {
                plugin.getLogManager().logDebugConsole("Enchantments (Meta) : " + ((Item)(e.getCaught())).getItemStack().getItemMeta().getEnchants().toString());
            } else {
                plugin.getLogManager().logDebugConsole("Stored Enchantments (Meta) : " + ((EnchantmentStorageMeta)((Item)(e.getCaught())).getItemStack().getItemMeta()).getStoredEnchants().toString());
            }
        }
    }
}