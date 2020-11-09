package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

public class VillagerTradeListener implements Listener {
    private EnchantControl plugin;

    public VillagerTradeListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent e)
    {
        if (e.getRecipe().getResult().getEnchantments().isEmpty() == true && (e.getRecipe().getResult().getItemMeta() == null || (e.getRecipe().getResult().getItemMeta() instanceof EnchantmentStorageMeta) == false || ((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta())).hasStoredEnchants() == false))
            return;

        if (e.getRecipe().getResult().getEnchantments().isEmpty() == false) {
            for (Map.Entry<Enchantment, Integer> enchantment : e.getRecipe().getResult().getEnchantments().entrySet()) {
                if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0)
                        e.getRecipe().getResult().removeEnchantment(enchantment.getKey());
                    else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                        e.getRecipe().getResult().removeEnchantment(enchantment.getKey());
                        e.getRecipe().getResult().addEnchantment(enchantment.getKey(),plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
                    }
                }
            }
            if (e.getRecipe().getResult().getEnchantments().isEmpty() == true) {
                e.setCancelled(true);
                plugin.getServer().getPluginManager().callEvent(new VillagerAcquireTradeEvent(e.getEntity(), new MerchantRecipe(new ItemStack(Material.AIR), 0, 0,false,0,1)));
            }
        } else {
            for (Map.Entry<Enchantment, Integer> enchantment :((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta())).getStoredEnchants().entrySet()) {
                if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                        EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta()));
                        meta.removeStoredEnchant(enchantment.getKey());
                        e.getRecipe().getResult().setItemMeta(meta);
                    } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                        EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta()));
                        meta.removeStoredEnchant(enchantment.getKey());
                        meta.addStoredEnchant(enchantment.getKey(),plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), true);
                        e.getRecipe().getResult().setItemMeta(meta);
                    }
                }
            }
            if (((EnchantmentStorageMeta)(e.getRecipe().getResult().getItemMeta())).getStoredEnchants().isEmpty() == true) {
                e.setCancelled(true);
                plugin.getServer().getPluginManager().callEvent(new VillagerAcquireTradeEvent(e.getEntity(), new MerchantRecipe(new ItemStack(Material.AIR), 0, 0,false,0,1)));
            }
        }
    }
}
