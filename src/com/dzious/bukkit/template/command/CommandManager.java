package com.dzious.bukkit.template.command;

import com.dzious.bukkit.template.Template;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class CommandManager implements TabCompleter {

    private Template plugin;
    private static final List<String> chatTabCompletes = Arrays.asList(
            "info",
            "join",
            "leave",
            "talk"
    );

    public CommandManager(Template plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        plugin.getLogManager().logInfo("CommandManager OnEnable");
        plugin.getCommand("Template").setExecutor(new CommandTemplate(plugin));
        if (plugin.getConfigManager().doPathExist("chat.enable") == true && plugin.getConfigManager().getBooleanFromPath("chat.enable") == true)
            plugin.getCommand("Chat").setExecutor(new CommandChat(plugin));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args[0].toLowerCase()) {
            case "template":
                return (null);
            case "chat":
                if (args[1] == null)
                    return (chatTabCompletes);
                switch (args[1].toLowerCase()) {
                    case "info":
                        return (null);
                    case "join":
                        return (plugin.getChannelManager().getAvailableChannels());
                    case "leave":
                        return (plugin.getChannelManager().getAvailableChannels());
                    case "talk":
                        return (plugin.getChannelManager().getAvailableChannels());
                    default:
                        return (null);
                }
            default:
                return (null);
        }
    }

}
