package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootGenerationListener implements Listener {
    private EnchantControl plugin;

    public LootGenerationListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGeneration(LootGenerateEvent e)
    {
        if (e.getLoot().isEmpty() == true)
            return;
        for (ItemStack item : e.getLoot()) {
            plugin.getLogManager().logDebugConsole(item.toString());
            plugin.getLogManager().logDebugConsole("(item.getItemMeta() instanceof EnchantmentStorageMeta) == false : " + ((item.getItemMeta() instanceof EnchantmentStorageMeta) == false));
            if (item.getEnchantments().isEmpty() == true && (item.getItemMeta() == null || (item.getItemMeta() instanceof EnchantmentStorageMeta) == false || ((EnchantmentStorageMeta)(item.getItemMeta())).hasStoredEnchants() == false))
                continue;

            List<Enchantment> enchantmentsList = new ArrayList<>();

            if (item.getEnchantments().isEmpty() == false) {
                for (Map.Entry<Enchantment, Integer> enchantment :item.getEnchantments().entrySet()) {
                    enchantmentsList.add(enchantment.getKey());
                }
                for (Map.Entry<Enchantment, Integer> enchantment :item.getEnchantments().entrySet()) {
                    if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                        Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(item, enchantmentsList);
                        Integer enchantmentLevel = enchantment.getValue();

                        item.removeEnchantment(enchantment.getKey());
                        if (newEnchantment != null) {
                            if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                                enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
                            item.addEnchantment(newEnchantment, enchantmentLevel);
                        }
                    } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                        item.removeEnchantment(enchantment.getKey());
                        item.addEnchantment(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
                    }
                }
            } else {
                for (Map.Entry<Enchantment, Integer> enchantment :((EnchantmentStorageMeta)(item.getItemMeta())).getStoredEnchants().entrySet()) {
                    enchantmentsList.add(enchantment.getKey());
                }
                for (Map.Entry<Enchantment, Integer> enchantment :((EnchantmentStorageMeta)(item.getItemMeta())).getStoredEnchants().entrySet()) {
                    if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                        EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)(item.getItemMeta()));
                        Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(item, enchantmentsList);
                        Integer enchantmentLevel = enchantment.getValue();

                        meta.removeStoredEnchant(enchantment.getKey());
                        if (newEnchantment != null) {
                            if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                                enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
                            meta.addStoredEnchant(newEnchantment, enchantmentLevel, false);
                        }
                        item.setItemMeta(meta);
                    } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                        EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)(item.getItemMeta()));
                        meta.removeStoredEnchant(enchantment.getKey());
                        meta.addStoredEnchant(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), false);
                        item.setItemMeta(meta);
                    }
                    if (item.getType() == Material.ENCHANTED_BOOK && ((EnchantmentStorageMeta)(item.getItemMeta())).getStoredEnchants().isEmpty() == true) {
                        e.getLoot().remove(item);
                        e.getLoot().add(new ItemStack(Material.BOOK));

                    }
                }

            }
        }
    }
}