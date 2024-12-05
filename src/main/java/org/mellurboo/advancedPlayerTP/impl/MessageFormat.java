package org.mellurboo.advancedPlayerTP.impl;

import org.bukkit.entity.Player;
import org.mellurboo.advancedPlayerTP.AdvancedPlayerTP;

public class MessageFormat {
    private final AdvancedPlayerTP plugin;
    public MessageFormat(AdvancedPlayerTP plugin){
        this.plugin = plugin;
    }

    /// send a message that is formatted by replacing one static value "%value%" with its
    /// variable. then sends it to the player in one call
    public void sendFormattedMessage(String message, String replace, String finalValue, Player player){
        String formattedmessage = message.replace(replace, finalValue);
        player.sendMessage(formattedmessage);
    }

    /// a macro for replacing %name% with playername
    public void insertPlayerName(String message, Player player, Player target){
        String playername = player.getName();
        String formattedmessage = message.replace("%name%", playername);
        if (target != null){
            player.sendMessage(formattedmessage);
        }
    }

    /// a macro for the recipient name to be replaced with a player name
    public void insertRecipientName(String message, Player player, Player target){
        String playername = player.getName();
        String formattedmessage = message.replace("%recipient%", playername);
        if (target != null){
            target.sendMessage(formattedmessage);
        }
    }
}
