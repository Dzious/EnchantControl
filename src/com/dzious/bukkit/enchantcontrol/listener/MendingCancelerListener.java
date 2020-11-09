package com.dzious.bukkit.enchantcontrol.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;

class MendingCancelerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    void onMendingRepair(PlayerItemMendEvent e) {
        e.getPlayer().giveExp(e.getExperienceOrb().getExperience());
        e.setCancelled(true);
    }
}