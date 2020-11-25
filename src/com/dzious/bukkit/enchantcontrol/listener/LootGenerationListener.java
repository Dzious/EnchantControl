package com.dzious.bukkit.enchantcontrol.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class LootGenerationListener implements Listener {
    private EnchantControl plugin;

    public LootGenerationListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGeneration(LootGenerateEvent e)
    {
        if (e.getLoot().isEmpty())
            return;

        for (ItemStack item : e.getLoot()) {
            if (item == null ||
                item.getItemMeta() == null || 
                (item.getEnchantments().isEmpty() &&
                (!(item.getItemMeta() instanceof EnchantmentStorageMeta) ||
                ((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants().isEmpty()))) {
                continue;
            }

            plugin.getLogManager().logDebugConsole("Generated item type : " +  item.getType());

            Map<Enchantment, Integer> enchantments;
            if (!item.getItemMeta().getEnchants().isEmpty()) {
                enchantments = new HashMap<>(item.getItemMeta().getEnchants()); ;
            } else {
                enchantments = new HashMap<>(((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants());
            }

            plugin.getLogManager().logDebugConsole("Enchantments (Start) : " + enchantments.toString());

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
                    plugin.getLogManager().logDebugConsole("Removed : " + ChatColor.GREEN + enchantment.getKey().getKey());
                    Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(item, new ArrayList<>(enchantments.keySet()));
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

            if (finalEnchantments.isEmpty() && item.getType() == Material.ENCHANTED_BOOK) {
                item = new ItemStack(Material.BOOK);
            } else {
                for (Map.Entry<Enchantment, Integer> enchantment : finalEnchantments.entrySet()) {
                    plugin.getLogManager().logDebugConsole("Enchantment (Loop) : " + finalEnchantments.toString());
                    if (!item.getItemMeta().getEnchants().isEmpty()) {
                        meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
                    } else {
                        ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                    }
                }
                item.setItemMeta(meta);
                if (!item.getItemMeta().getEnchants().isEmpty()) {
                    plugin.getLogManager().logDebugConsole("Enchantments (Meta) : " + item.getItemMeta().getEnchants().toString());
                } else {
                    plugin.getLogManager().logDebugConsole("Stored Enchantments (Meta) : " + ((EnchantmentStorageMeta)(item.getItemMeta())).getStoredEnchants().toString());
                }
            }
        }
    }

    //     if (e.getLoot().isEmpty())
    //         return;
    //     for (ItemStack item : e.getLoot()) {
    //         plugin.getLogManager().logDebugConsole(item.toString());
    //         if (item.getEnchantments().isEmpty() &&
    //             (item.getItemMeta() == null ||
    //             !(item.getItemMeta() instanceof EnchantmentStorageMeta) ||
    //             !((EnchantmentStorageMeta)(item.getItemMeta())).hasStoredEnchants()))
    //             continue;

    //         List<Enchantment> enchantmentsList = new ArrayList<>();

    //         if (!item.getEnchantments().isEmpty()) {
    //             for (Map.Entry<Enchantment, Integer> enchantment :item.getEnchantments().entrySet()) {
    //                 enchantmentsList.add(enchantment.getKey());
    //             }
    //             for (Map.Entry<Enchantment, Integer> enchantment :item.getEnchantments().entrySet()) {
    //                 if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
    //                     Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(item, enchantmentsList);
    //                     Integer enchantmentLevel = enchantment.getValue();

    //                     item.removeEnchantment(enchantment.getKey());
    //                     if (newEnchantment != null) {
    //                         if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
    //                             enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
    //                         item.addEnchantment(newEnchantment, enchantmentLevel);
    //                     }
    //                 } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
    //                     item.removeEnchantment(enchantment.getKey());
    //                     item.addEnchantment(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
    //                 }
    //             }
    //         } else {
    //             for (Map.Entry<Enchantment, Integer> enchantment :((EnchantmentStorageMeta)(item.getItemMeta())).getStoredEnchants().entrySet()) {
    //                 enchantmentsList.add(enchantment.getKey());
    //             }
    //             for (Map.Entry<Enchantment, Integer> enchantment :((EnchantmentStorageMeta)(item.getItemMeta())).getStoredEnchants().entrySet()) {
    //                 if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
    //                     EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)(item.getItemMeta()));
    //                     Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(item, enchantmentsList);
    //                     Integer enchantmentLevel = enchantment.getValue();

    //                     meta.removeStoredEnchant(enchantment.getKey());
    //                     if (newEnchantment != null) {
    //                         if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
    //                             enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
    //                         meta.addStoredEnchant(newEnchantment, enchantmentLevel, false);
    //                     }
    //                     item.setItemMeta(meta);
    //                 } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
    //                     EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)(item.getItemMeta()));
    //                     meta.removeStoredEnchant(enchantment.getKey());
    //                     meta.addStoredEnchant(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), false);
    //                     item.setItemMeta(meta);
    //                 }
    //                 if (item.getType() == Material.ENCHANTED_BOOK && ((EnchantmentStorageMeta)(item.getItemMeta())).getStoredEnchants().isEmpty()) {
    //                     e.getLoot().remove(item);
    //                     e.getLoot().add(new ItemStack(Material.BOOK));

    //                 }
    //             }

    //         }
    //     }
    // }
}