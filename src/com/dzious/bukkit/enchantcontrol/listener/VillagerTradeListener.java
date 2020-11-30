package com.dzious.bukkit.enchantcontrol.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class VillagerTradeListener implements Listener {
    private EnchantControl plugin;

    public VillagerTradeListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent e)
    {
        ItemStack item = e.getRecipe().getResult();
        if (item.getItemMeta() == null ||
            (item.getItemMeta().getEnchants().isEmpty() &&
            (!(item.getItemMeta() instanceof EnchantmentStorageMeta) ||
            ((EnchantmentStorageMeta)(item.getItemMeta())).getStoredEnchants().isEmpty()))) {
            plugin.getLogManager().logDebugConsole("Trade item is not enchanted.");
            return;
        }

        plugin.getLogManager().logDebugConsole("Trade item : " + e.getRecipe().getResult());
        
        Map<Enchantment, Integer> enchantments;
        if (!item.getItemMeta().getEnchants().isEmpty()) {
            enchantments = new HashMap<>(item.getItemMeta().getEnchants());
        } else {
            enchantments = new HashMap<>(((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants());
        }

        ItemMeta meta = item.getItemMeta();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (!item.getItemMeta().getEnchants().isEmpty() && meta.getEnchants().containsKey(enchantment.getKey())) {
                meta.removeEnchant(enchantment.getKey());
            } else if (((EnchantmentStorageMeta)meta).getStoredEnchants().containsKey(enchantment.getKey())) {
                ((EnchantmentStorageMeta)meta).removeStoredEnchant(enchantment.getKey());
            }
        }

        Map<Enchantment, Integer> finalEnchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                plugin.getLogManager().logDebugConsole("Removed : " + ChatColor.GREEN + enchantment.getKey().getName());
                Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(item, new ArrayList<>(enchantments.keySet()));
                Integer enchantmentLevel = enchantment.getValue();

                if (newEnchantment != null) {
                    if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                        enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
                        finalEnchantments.put(newEnchantment, enchantmentLevel);
                }
            } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                plugin.getLogManager().logDebugConsole("Replaced : " + ChatColor.GREEN + enchantment.getKey().getName() + ChatColor.WHITE + ". Level was : " + ChatColor.GREEN + enchantment.getValue() + ChatColor.WHITE + " and is now : " + ChatColor.GREEN);
                finalEnchantments.put(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
            } else {
                finalEnchantments.put(enchantment.getKey(),enchantment.getValue());
            }
        }

        plugin.getLogManager().logDebugConsole("Enchantments (End) : " + finalEnchantments.toString());

        if (finalEnchantments.isEmpty() && item.getType() == Material.ENCHANTED_BOOK) {
            e.setCancelled(true);
            plugin.getServer().getPluginManager().callEvent(new VillagerAcquireTradeEvent(e.getEntity(), 
                new MerchantRecipe(new ItemStack(Material.BOOK), 0, 12, true)));
        } else {
            for (Map.Entry<Enchantment, Integer> enchantment : finalEnchantments.entrySet()) {
                plugin.getLogManager().logDebugConsole("Enchantment (Loop) : " + finalEnchantments.toString());
                if (!item.getItemMeta().getEnchants().isEmpty()) {
                    meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
                } else {
                    ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                }
            }
            e.getRecipe().getResult().setItemMeta(meta);
            if (!item.getItemMeta().getEnchants().isEmpty()) {
                plugin.getLogManager().logDebugConsole("Enchantments (Meta) : " + e.getRecipe().getResult().getItemMeta().getEnchants().toString());
            } else {
                plugin.getLogManager().logDebugConsole("Stored Enchantments (Meta) : " + ((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta())).getStoredEnchants().toString());
            }
        }
    }
        
        // if (e.getRecipe().getResult().getEnchantments().isEmpty() &&
        // (e.getRecipe().getResult().getItemMeta() == null |
        // !(e.getRecipe().getResult().getItemMeta() instanceof EnchantmentStorageMeta) ||
        // !((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta())).hasStoredEnchants()))
        // return;

    //     if (e.getRecipe().getResult().getEnchantments().isEmpty() == false) {
    //         for (Map.Entry<Enchantment, Integer> enchantment : e.getRecipe().getResult().getEnchantments().entrySet()) {
    //             if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
    //                 if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0)
    //                     e.getRecipe().getResult().removeEnchantment(enchantment.getKey());
    //                 else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
    //                     e.getRecipe().getResult().removeEnchantment(enchantment.getKey());
    //                     e.getRecipe().getResult().addEnchantment(enchantment.getKey(),plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
    //                 }
    //             }
    //         }
    //         if (e.getRecipe().getResult().getEnchantments().isEmpty() == true) {
    //             e.setCancelled(true);
    //             plugin.getServer().getPluginManager().callEvent(new VillagerAcquireTradeEvent(e.getEntity(), new MerchantRecipe(new ItemStack(Material.AIR), 0, 0,false,0,1)));
    //         }
    //     } else {
    //         for (Map.Entry<Enchantment, Integer> enchantment :((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta())).getStoredEnchants().entrySet()) {
    //             if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
    //                 if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
    //                     EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta()));
    //                     meta.removeStoredEnchant(enchantment.getKey());
    //                     e.getRecipe().getResult().setItemMeta(meta);
    //                 } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
    //                     EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta()));
    //                     meta.removeStoredEnchant(enchantment.getKey());
    //                     meta.addStoredEnchant(enchantment.getKey(),plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), true);
    //                     e.getRecipe().getResult().setItemMeta(meta);
    //                 }
    //             }
    //         }
    //         if (((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta())).getStoredEnchants().isEmpty() == true) {
    //             e.setCancelled(true);
    //             plugin.getServer().getPluginManager().callEvent(new VillagerAcquireTradeEvent(e.getEntity(), new MerchantRecipe(new ItemStack(Material.BOOK), 0, 12, true,1, (float)(0.05))));
    //         }
    //     }
    // }
}
