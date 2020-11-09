package com.dzious.bukkit.enchantcontrol.command;

import com.dzious.bukkit.enchantcontrol.EnchantControl;


public class CommandManager {

    private EnchantControl plugin;
//    private static final List<String> chatTabCompletes = Arrays.asList(
//            "info",
//            "complete1",
//            "complete2"
//    );

    public CommandManager(EnchantControl plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        plugin.getLogManager().logInfo("CommandManager OnEnable");
        // plugin.getCommand("Template").setExecutor(new CommandTemplate(plugin));
        plugin.getCommand("EnchantControl").setExecutor(new CommandEnchantControl(plugin));
    }

//    @Override
//    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
//        switch (args[0].toLowerCase()) {
//            case "template":
//                return (null);
//            case "chat":
//                if (args[1] == null)
//                    return (chatTabCompletes);
//                switch (args[1].toLowerCase()) {
//                    case "info":
//                        return (null);
//                    case "complete1":
//                        return (null);
//                    case "complete2":
//                        return (null);
//                    default:
//                        return (null);
//                }
//            default:
//                return (null);
//        }
//    }

}
