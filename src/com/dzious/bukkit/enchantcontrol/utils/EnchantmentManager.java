package com.dzious.bukkit.enchantcontrol.utils;

import java.util.HashMap;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantmentManager {

    private EnchantControl plugin;

    private static Map<String, Integer> enchantments = new HashMap<String, Integer>() {{
        // Armor
        put("protection", Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel());
        put("projectile_protection", Enchantment.PROTECTION_PROJECTILE.getMaxLevel());
        put("fire_protection", Enchantment.PROTECTION_FIRE.getMaxLevel());
        put("blast_protection", Enchantment.PROTECTION_EXPLOSIONS.getMaxLevel());
        put("aqua_affinity", Enchantment.WATER_WORKER.getMaxLevel());
        put("respiration", Enchantment.OXYGEN.getMaxLevel());
        put("thorns", Enchantment.THORNS.getMaxLevel());
        put("feather_falling", Enchantment.PROTECTION_FALL.getMaxLevel());
        put("depth_strider", Enchantment.DEPTH_STRIDER.getMaxLevel());
        put("frost_walker", Enchantment.FROST_WALKER.getMaxLevel());
        put("soul_speed", Enchantment.SOUL_SPEED.getMaxLevel());
        
        // Sword
        put("sharpness", Enchantment.DAMAGE_ALL.getMaxLevel());
        put("smite", Enchantment.DAMAGE_UNDEAD.getMaxLevel()); 
        put("bane_of_arthropods", Enchantment.DAMAGE_ARTHROPODS.getMaxLevel());
        put("knockback", Enchantment.KNOCKBACK.getMaxLevel());
        put("fire_aspect", Enchantment.FIRE_ASPECT.getMaxLevel()); 
        put("looting", Enchantment.LOOT_BONUS_MOBS.getMaxLevel()); 
        put("sweeping", Enchantment.SWEEPING_EDGE.getMaxLevel()); 
        
        // Bow
        put("power",  Enchantment.ARROW_DAMAGE.getMaxLevel());
        put("punch", Enchantment.ARROW_KNOCKBACK.getMaxLevel());
        put("flame", Enchantment.ARROW_FIRE.getMaxLevel());
        put("infinity", Enchantment.ARROW_INFINITE.getMaxLevel());

        // Crossbow
        put("multishot",  Enchantment.MULTISHOT.getMaxLevel());
        put("quick_charge", Enchantment.QUICK_CHARGE.getMaxLevel());
        put("piercing", Enchantment.PIERCING.getMaxLevel());

        // Trident
        put("loyalty", Enchantment.LOYALTY.getMaxLevel());
        put("impaling", Enchantment.IMPALING.getMaxLevel());
        put("riptide", Enchantment.RIPTIDE.getMaxLevel());
        put("channeling", Enchantment.CHANNELING.getMaxLevel());

        // Tools
        put("efficiency",  Enchantment.DIG_SPEED.getMaxLevel());
        put("silk_touch",  Enchantment.SILK_TOUCH.getMaxLevel());
        put("fortune",  Enchantment.LOOT_BONUS_BLOCKS.getMaxLevel());

        // Fishing Rod
        put("luck_of_the_sea",  Enchantment.LUCK.getMaxLevel());
        put("lure",  Enchantment.LURE.getMaxLevel());
        
        // Every Items
        put("unbreaking",  Enchantment.DURABILITY.getMaxLevel());
        put("mending",   Enchantment.MENDING.getMaxLevel());

        // Curse
        put("vanishing_curse", Enchantment.VANISHING_CURSE.getMaxLevel()); 
        put("binding_curse",  Enchantment.BINDING_CURSE.getMaxLevel());
    }};

    public EnchantmentManager(EnchantControl plugin) {
        this.plugin = plugin;
        Map<String, Integer> configEnchantments = new HashMap<String, Integer>();

        plugin.getLogManager().logDebugConsole(enchantments.toString());

        if (this.plugin.getConfigManager().doPathExist("enchantments") == true) {
            configEnchantments = this.plugin.getConfigManager().loadEnchantments();
        }
        for (Map.Entry<String, Integer> config : configEnchantments.entrySet()) {
            for (Map.Entry<String, Integer> enchantment : enchantments.entrySet()) {
                if (config.getKey().compareTo(enchantment.getKey()) == 0) {
                    plugin.getLogManager().logDebugConsole("Replaced " + enchantment.getKey() + ". Old value was " + enchantment.getValue() + ", new value is " + config.getValue() + ".");
                    enchantments.replace(enchantment.getKey(), config.getValue());
                    break;
                }
            }
        }

        plugin.getLogManager().logDebugConsole(enchantments.toString());
    }

    public  void removeEnchantment(ItemStack stack) {

    }
}
