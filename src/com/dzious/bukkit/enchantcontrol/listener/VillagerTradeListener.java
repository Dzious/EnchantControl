package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.Map;

public class VillagerTradeListener implements Listener {
    private EnchantControl plugin;

    public VillagerTradeListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent e)
    {
        if (e.getRecipe().getResult().getEnchantments().isEmpty())
            return;

        for (Map.Entry<Enchantment, Integer> enchantment : e.getRecipe().getResult().getEnchantments().entrySet()) {
            if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0)
                    e.getRecipe().getResult().getEnchantments().remove(enchantment.getKey());
                else
                    e.getRecipe().getResult().getEnchantments().replace(enchantment.getKey(),plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
            }
        }
        if (e.getRecipe().getResult().getEnchantments().isEmpty() == true)
            e.setCancelled(true);
            plugin.getServer().getPluginManager().callEvent(new VillagerAcquireTradeEvent(e.getEntity(), new MerchantRecipe(new ItemStack(Material.AIR), 0, 0,false,0,1)));
        return;
    }
}
