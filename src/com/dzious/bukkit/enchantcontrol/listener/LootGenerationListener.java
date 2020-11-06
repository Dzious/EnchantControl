package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

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
            if (item.getEnchantments().isEmpty() == true)
                continue;
            List<Enchantment> enchantmentsList = new ArrayList<>();
            for (Map.Entry<Enchantment, Integer> enchantment :item.getEnchantments().entrySet()) {
                enchantmentsList.add(enchantment.getKey());
            }
            for (Map.Entry<Enchantment, Integer> enchantment :item.getEnchantments().entrySet()) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                    Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(item, enchantmentsList);
                    Integer enchantmentLevel = enchantment.getValue();

                    item.getEnchantments().remove(enchantment.getKey());
                    if (newEnchantment != null) {
                        if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                            enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
                        item.getEnchantments().put(newEnchantment, enchantmentLevel);
                    }
                } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    item.getEnchantments().replace(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
                }
            }
            if (item.getEnchantments().isEmpty() == true) {
                e.getLoot().remove(item);
//                e.getLoot().add(new ItemStack(Material.BOOK));
            }
        }
    }
}