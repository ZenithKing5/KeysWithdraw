package com.kingrbxd.keywithdraw.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /**
     * Applies color codes to a message, including hex colors
     *
     * @param message The message to colorize
     * @return The colorized message
     */
    public static String colorize(String message) {
        if (message == null) {
            return "";
        }

        // Process hex colors (format: &#RRGGBB)
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + hexColor).toString());
        }

        matcher.appendTail(buffer);
        message = buffer.toString();

        // Process standard color codes
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}