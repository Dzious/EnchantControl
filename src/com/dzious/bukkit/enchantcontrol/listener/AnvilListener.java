package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

public class AnvilListener implements Listener {
    private final EnchantControl plugin;

    public AnvilListener (EnchantControl plugin) {
        this.plugin = plugin;
    }


    // TODO Manage equipment upgrade over minecraft vanilla limit
    @EventHandler
    public void onFusionPreparation(PrepareAnvilEvent e) {

        if (e.getResult() == null || (e.getResult().getEnchantments().isEmpty() == true && (e.getResult().getItemMeta() == null || (e.getResult().getItemMeta() instanceof EnchantmentStorageMeta) == false || ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants().isEmpty() == true))) {
            return;
        }

        plugin.getLogManager().logInfo("Type : " +  e.getResult().getType());

        if (e.getResult().getEnchantments().isEmpty() == false) {
            plugin.getLogManager().logInfo("Enchantments : " +  e.getResult().getEnchantments());
            for (Map.Entry<Enchantment, Integer> enchantment : e.getResult().getEnchantments().entrySet()) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                    plugin.getLogManager().logInfo("Enchantments : Remove");
                    e.getResult().removeEnchantment(enchantment.getKey());
                } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    plugin.getLogManager().logInfo("Enchantments : Replace");
                    e.getResult().removeEnchantment(enchantment.getKey());
                    e.getResult().addEnchantment(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())); // TODO may have to reset item (e.setResult(ItemStack))
                }
            }
        } else {
            plugin.getLogManager().logInfo("Stored Enchantments : " +  ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants());
            for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants().entrySet()) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                    plugin.getLogManager().logInfo("Stored Enchantments : Remove");
                    EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)e.getResult().getItemMeta());
                    meta.removeStoredEnchant(enchantment.getKey());
                    e.getResult().setItemMeta(meta);
                } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    plugin.getLogManager().logInfo("Stored Enchantments : Replace");
                    EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)e.getResult().getItemMeta());
                    meta.removeStoredEnchant(enchantment.getKey());
                    meta.addStoredEnchant(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), true); // TODO may have to reset item (e.setResult(ItemStack))
                    e.getResult().setItemMeta(meta);
                }
            }
            if (e.getResult().getType() == Material.ENCHANTED_BOOK && ((EnchantmentStorageMeta)(e.getResult().getItemMeta())).getStoredEnchants().isEmpty() == true) {
                e.setResult(new ItemStack(Material.BOOK));
            }
            // ToDo Add changing no data book to simple book
            plugin.getLogManager().logInfo("Stored Enchantments : " +  ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants());
        }
    }
}