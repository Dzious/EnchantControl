package com.dzious.bukkit.template.chat;

import com.dzious.bukkit.template.Template;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatManager implements Listener {

    private Template plugin;

    public ChatManager(Template plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (plugin.getConfigManager().doPathExist("chat.enable") == false)
            return;
        String channel = "General";

        if (plugin.getConfigManager().doPathExist("chat.player.default") == true && plugin.getConfigManager().getStringFromPath("chat.player.default").isEmpty() == false)
            channel = plugin.getConfigManager().getStringFromPath("chat.player.default");

        plugin.getChannelManager().joinChannel(e.getPlayer(), channel);
        plugin.getChannelManager().setTalkingChannel(e.getPlayer(), channel);

        if (plugin.getConfigManager().doPathExist("chat.staff.connect_on_login") == true && plugin.getConfigManager().getBooleanFromPath("chat.staff.connect_on_login") == true)
            if (plugin.getConfigManager().doPathExist("chat.staff.channel") == true && plugin.getConfigManager().getStringFromPath("chat.staff.channel").isEmpty() == false) {
                channel = plugin.getConfigManager().getStringFromPath("chat.staff.channel");
                plugin.getChannelManager().joinChannel(e.getPlayer(), channel);
            }
    }

    @EventHandler
    public void onPlayerLeave(PlayerJoinEvent e) {
        plugin.getChannelManager().onPlayerLeave(e.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.getRecipients().clear();
        for (Player p : plugin.getChannelManager().getPlayersInChannel(plugin.getChannelManager().getTalkingChannel(e.getPlayer())))
            e.getRecipients().add(p);
    }

}
