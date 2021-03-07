package com.dzious.bukkit.enchantcontrol.listener;

import java.util.HashMap;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;

public class AnvilListener implements Listener {
    private final EnchantControl plugin;
    boolean bypassMinecraftMaxLevel = false;
    boolean ignoreConflict = false;

    public AnvilListener (EnchantControl plugin) {
        this.plugin = plugin;
        if (plugin.getConfigManager().doPathExist("bypass_levels"))
            bypassMinecraftMaxLevel = plugin.getConfigManager().getBooleanFromPath("bypass_levels");
        if (plugin.getConfigManager().doPathExist("ignore_conflict"))
            ignoreConflict = plugin.getConfigManager().getBooleanFromPath("ignore_conflict");
        plugin.getLogManager().logDebugConsole("bypassMinecraftMaxLevel : " + bypassMinecraftMaxLevel);
        plugin.getLogManager().logDebugConsole("ignoreConflict : " + ignoreConflict);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onGetResult(InventoryClickEvent e) {
        if (!(e.getClickedInventory() instanceof AnvilInventory) ||	e.getSlot() != 2 || e.isLeftClick() == false || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (e.isShiftClick() && e.getWhoClicked().getInventory().firstEmpty() == -1) {
            return;
        }

        Inventory playerInv = e.getWhoClicked().getInventory();

        if (e.isShiftClick()) {
            playerInv.setItem(playerInv.firstEmpty(), e.getCurrentItem());
        } else {
            e.getWhoClicked().setItemOnCursor(e.getCurrentItem());
        }
        // plugin.getLogManager().logDebugConsole("REPLACED!");
        e.getClickedInventory().clear();
        e.setResult(Result.DENY);
        e.setCancelled(true);
        return;
    }

    @EventHandler
    public void onFusionPreparation(PrepareAnvilEvent e) {
    
        if (!canContinue(e)) {
            return;
        }

        if (e.getResult() == null && ignoreConflict && e.getInventory().getItem(0) != null && e.getInventory().getItem(1) != null &&
            Enchantment.VANISHING_CURSE.getItemTarget().includes(e.getInventory().getItem(0).getType()) && 
            (e.getInventory().getItem(1).getType() ==  e.getInventory().getItem(0).getType() || 
            e.getInventory().getItem(1).getType() == Material.ENCHANTED_BOOK))
                e.setResult(e.getInventory().getItem(0).clone());

        Map<Enchantment, Integer> baseEnchantments = null;
        if (!e.getInventory().getItem(0).getItemMeta().getEnchants().isEmpty() || (e.getResult().getItemMeta() instanceof EnchantmentStorageMeta && ((EnchantmentStorageMeta)e.getInventory().getItem(0).getItemMeta()).getStoredEnchants().isEmpty())) {
            if (!e.getInventory().getItem(0).getItemMeta().getEnchants().isEmpty()) {
                baseEnchantments = new HashMap<>(e.getInventory().getItem(0).getItemMeta().getEnchants()); ;
            } else {
                baseEnchantments = new HashMap<>(((EnchantmentStorageMeta)e.getInventory().getItem(0).getItemMeta()).getStoredEnchants());
            }
        }

        Map<Enchantment, Integer> enchantments;
        if (bypassMinecraftMaxLevel || ignoreConflict) {
            enchantments = computeFusion(e.getInventory().getContents());
        } else if (!e.getResult().getItemMeta().getEnchants().isEmpty()) {
            enchantments = new HashMap<>(e.getResult().getItemMeta().getEnchants()); ;
        } else {
            enchantments = new HashMap<>(((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants());
        }

        plugin.getLogManager().logDebugConsole("Enchantments (Start) : " + enchantments.toString());

        ItemMeta meta = e.getResult().getItemMeta();
        if (!e.getResult().getItemMeta().getEnchants().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> enchantment : meta.getEnchants().entrySet())
                meta.removeEnchant(enchantment.getKey());
        } else if (meta instanceof EnchantmentStorageMeta && !((EnchantmentStorageMeta)meta).getStoredEnchants().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta)meta).getStoredEnchants().entrySet())
                ((EnchantmentStorageMeta)meta).removeStoredEnchant(enchantment.getKey());
        }

        Map<Enchantment, Integer> finalEnchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                plugin.getLogManager().logDebugConsole("Removed : " + ChatColor.GREEN + enchantment.getKey().getKey());
                continue;
            } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                PersistentDataContainer container = e.getResult().getItemMeta().getPersistentDataContainer();
                if (container.has(plugin.getNamespacedKey(), PersistentDataType.STRING) &&
                    container.get(plugin.getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("event") &&
                    baseEnchantments != null &&
                    baseEnchantments.containsKey(enchantment.getKey()) &&
                    baseEnchantments.get(enchantment.getKey()) == enchantment.getValue()) {
                        plugin.getLogManager().logDebugConsole("Skipped : " + ChatColor.GREEN + enchantment.getKey().getKey() + ChatColor.WHITE + ".");
                        finalEnchantments.put(enchantment.getKey(),enchantment.getValue());
                        continue;
                }
                plugin.getLogManager().logDebugConsole("Replaced : " + ChatColor.GREEN + enchantment.getKey().getKey() + ChatColor.WHITE + ". " + 
                    "Level was : " + ChatColor.GREEN + enchantment.getValue() + 
                    ChatColor.WHITE + " and is now : " + ChatColor.GREEN + plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
                finalEnchantments.put(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
            } else {
                finalEnchantments.put(enchantment.getKey(),enchantment.getValue());
            }
        }

        plugin.getLogManager().logDebugConsole("Enchantments (End) : " + finalEnchantments.toString());

        if (finalEnchantments.isEmpty()) {
            e.setResult(null);
        } else {
            for (Map.Entry<Enchantment, Integer> enchantment : finalEnchantments.entrySet()) {
                plugin.getLogManager().logDebugConsole("Enchantment (Loop) : " + finalEnchantments.toString());
                if (!e.getResult().getItemMeta().getEnchants().isEmpty()) {
                    meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
                } else {
                    ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                }
            }
            e.getResult().setItemMeta(meta);
            if (!e.getResult().getItemMeta().getEnchants().isEmpty()) {
                plugin.getLogManager().logDebugConsole("Enchantments (Meta) : " + e.getResult().getItemMeta().getEnchants().toString());
            } else {
                plugin.getLogManager().logDebugConsole("Stored Enchantments (Meta) : " + ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants().toString());
            }
        }
    }

    private Map<Enchantment, Integer> computeFusion(ItemStack[] items) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        Map<Enchantment, Integer> lhsEnchantments = null;
        Map<Enchantment, Integer> rhsEnchantments = null;
        boolean isFusionPossible = false;

        for (int i = 0; i < 2; i++) {
            if (items[i] == null) {
                return (null);
            }
            if (items[i].getItemMeta() != null && 
                (!items[i].getItemMeta().getEnchants().isEmpty() || 
                (items[i].getItemMeta() instanceof EnchantmentStorageMeta &&
                !((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants().isEmpty()))) {
                isFusionPossible = true;
            }
        }
        if (!isFusionPossible)
            return (null);
        
        if (!items[0].getItemMeta().getEnchants().isEmpty()) {
            lhsEnchantments = new HashMap<>(items[0].getItemMeta().getEnchants()); ;
        } else if (items[0].getItemMeta() instanceof EnchantmentStorageMeta &&
            !((EnchantmentStorageMeta)items[0].getItemMeta()).getStoredEnchants().isEmpty()) {
            lhsEnchantments = new HashMap<>(((EnchantmentStorageMeta)items[0].getItemMeta()).getStoredEnchants());
        } else {
            lhsEnchantments = new HashMap<>();
        }

        if (!items[1].getItemMeta().getEnchants().isEmpty()) {
            rhsEnchantments = new HashMap<>(items[1].getItemMeta().getEnchants()); ;
        } else if (items[1].getItemMeta() instanceof EnchantmentStorageMeta &&
            !((EnchantmentStorageMeta)items[1].getItemMeta()).getStoredEnchants().isEmpty()) {
            rhsEnchantments = new HashMap<>(((EnchantmentStorageMeta)items[1].getItemMeta()).getStoredEnchants());
        } else {
            rhsEnchantments = new HashMap<>();
        }

        for (Map.Entry<Enchantment, Integer> enchantment : lhsEnchantments.entrySet()) {
            if (rhsEnchantments.containsKey(enchantment.getKey())) {
                if (enchantment.getValue() == rhsEnchantments.get(enchantment.getKey()) &&
                    enchantment.getValue() < plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    enchantments.put(enchantment.getKey(), enchantment.getValue() + 1);
                } else if (enchantment.getValue() < rhsEnchantments.get(enchantment.getKey())) {
                    enchantments.put(enchantment.getKey(), rhsEnchantments.get(enchantment.getKey()));
                } else {
                    enchantments.put(enchantment.getKey(), enchantment.getValue());
                }
            } else {
                enchantments.put(enchantment.getKey(), enchantment.getValue());
            }
        }

        boolean conflicts = false;
        for (Map.Entry<Enchantment, Integer> enchantment : rhsEnchantments.entrySet()) {
            if ((!enchantments.containsKey(enchantment.getKey())) && enchantment.getKey().canEnchantItem(items[0])) {
                if (!ignoreConflict) {
                    conflicts = false;
                    for (Map.Entry<Enchantment, Integer> current : enchantments.entrySet()) {
                        if (current.getKey().conflictsWith(enchantment.getKey())) {
                            conflicts = true;
                            break;
                        }
                    }
                    if (conflicts == false) {
                        enchantments.put(enchantment.getKey(), enchantment.getValue());
                    }
                } else {
                    enchantments.put(enchantment.getKey(), enchantment.getValue());
                }
            }
        }
        plugin.getLogManager().logDebugConsole("Enchants : " + enchantments);
        return (enchantments);
    }

    private boolean canContinue(PrepareAnvilEvent e)
    {
        if (e.getResult() == null) {
            if (ignoreConflict && e.getInventory().getItem(0) != null && e.getInventory().getItem(1) != null &&
                Enchantment.VANISHING_CURSE.getItemTarget().includes(e.getInventory().getItem(0).getType()) && 
                (e.getInventory().getItem(1).getType() ==  e.getInventory().getItem(0).getType() || 
                e.getInventory().getItem(1).getType() == Material.ENCHANTED_BOOK))
                    return (true);
            else
               return (false);
            
        }

        if (e.getResult().getItemMeta() == null)
            return (false);

        if (e.getResult().getEnchantments().isEmpty()) {
            if (!(e.getResult().getItemMeta() instanceof EnchantmentStorageMeta))
                return (false);
            if (((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants().isEmpty()) 
               return (false);
        }
        return true;
    }
}