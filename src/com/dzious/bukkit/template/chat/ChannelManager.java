package com.dzious.bukkit.template.chat;

import com.dzious.bukkit.template.Template;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChannelManager {

    private Template plugin;
    private HashMap<String, ArrayList<Player>> channels;
    private HashMap<Player, String> talkingChannel;
    private HashMap<String, String> channelPerm;
    private List<String> availableChannels = Arrays.asList(
            "General",
            "Staff"
    );

    public ChannelManager(Template plugin) {
        channels = new HashMap<String, ArrayList<Player>>();
        for (String channel : availableChannels)
            channels.put(channel, new ArrayList<Player>());
    }

    public void onEnable() {
        List<String> configChannels = null;

        if (plugin.getConfigManager().doPathExist("chat.channels") == true)
            configChannels = plugin.getConfigManager().getListFromPath("chat.channels");
        if (configChannels != null && configChannels.isEmpty() == false)
            availableChannels = configChannels;
        for (String channel : availableChannels)
            channelPerm.put(channel, plugin.getName().toLowerCase() + ".chat." + channel.toLowerCase());
    }

    public boolean joinChannel(Player p, String channelName)  {
        ArrayList<Player> players = getPlayersInChannel(channelName);

        if (p.hasPermission(channelPerm.get(channelName)) == false) {
            p.sendMessage(ChatColor.RED + "You can't access this channel.");
        } else if (isPlayerInChannel(p, channelName) == true) {
            p.sendMessage(ChatColor.GREEN + "You're already in " + ChatColor.BLUE + channelName + ChatColor.GREEN + ".");
        } else {
            players.add(p);
            channels.put(channelName, players);
            p.sendMessage(ChatColor.GREEN + "You joined " + ChatColor.BLUE + channelName + ChatColor.GREEN + ".");

        }
        return (true);
    }

    public boolean leaveChannel(Player p, String channelName)  {
        ArrayList<Player> players = getPlayersInChannel(channelName);

        if (isPlayerInChannel(p, channelName) == true)
            p.sendMessage(ChatColor.GREEN + "You're not in " + ChatColor.BLUE + channelName + ChatColor.GREEN + ".");
        else {
            players.remove(p);
            channels.put(channelName, players);
            p.sendMessage(ChatColor.GREEN + "You left " + ChatColor.BLUE + channelName + ChatColor.GREEN + ".");

        }
        return (true);
    }

    public boolean setTalkingChannel(Player p, String channelName) {
        if (isPlayerInChannel(p, channelName) == true) {
            talkingChannel.put(p, channelName);
            p.sendMessage(ChatColor.GREEN + "You're now talking in " + ChatColor.BLUE + channelName + ChatColor.GREEN + ".");
        } else
            p.sendMessage(ChatColor.RED + "You need to join the channel before being able to talk in it.");
        return (true);
    }

    public String getTalkingChannel(Player p) {
        return (talkingChannel.get(p));
    }

    public ArrayList<Player> getPlayersInChannel(String channelName)  {
        return (channels.get(channelName));
    }

    public boolean isPlayerInChannel(Player player, String channelName) {
        return (channels.get(channelName).contains(player));
    }

    public List<String> getAvailableChannels() {
        return (availableChannels);
    }

    public void onPlayerLeave(Player p) {
        for (String channel : availableChannels)
            if (isPlayerInChannel(p, channel) == true) {
                leaveChannel(p, channel);
                talkingChannel.remove(p);
            }
    }

}
