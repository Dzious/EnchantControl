package com.dzious.bukkit.enchantcontrol.utils;

public class Utils {
    private Utils() {}

    public static String concatCommand(String label, String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(label);
        for (String arg : args)
            sb.append(arg);
        return (sb.toString());
    }
}
