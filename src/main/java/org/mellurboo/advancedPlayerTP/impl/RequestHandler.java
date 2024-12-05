package org.mellurboo.advancedPlayerTP.impl;

import org.bukkit.entity.Player;
import org.mellurboo.advancedPlayerTP.AdvancedPlayerTP;

public class RequestHandler {
    private final AdvancedPlayerTP plugin;
    public RequestHandler(AdvancedPlayerTP plugin) {
        this.plugin = plugin;
    }

    /// Handle player teleportation
    public void teleportPlayer(Player sender, Player recipient){
        sender.teleport(sender.getLocation());
        removeRequest(sender, recipient);
    }

    /// Handle Accepting Player Teleportation Requests and charging the sender
    public boolean acceptRequest(Player sender, Player recipient){
        if (!requestAlreadyExsists(sender, recipient)){
            int levelCost = plugin.levelCost;
            recipient.setLevel(recipient.getLevel() - levelCost);
            teleportPlayer(recipient, sender);
            return true;
        }else return false;
    }

    /// Handle Denying Player Teleportation requests.
    public void denyRequest(Player sender, Player recipient){
        removeRequest(sender, recipient);
    }

    /// Handle Canceling player requests by just getting the player name,
    /// doing this we just make sure the player name is correct and if so we
    /// go ahead and remove it.
    public void cancelRequest(Player sender, Player recipient){
        removeRequest(sender, recipient);
    }

    /// Handle removing a player teleport request from the
    /// Request teleport Map, we just get it from the main class
    private void removeRequest(Player sender, Player recipient){
        plugin.getTeleportRequests().entrySet().removeIf(entry ->
                entry.getKey().equals(sender.getUniqueId()) || entry.getValue().equals(sender.getUniqueId())
                    || entry.getKey().equals(recipient.getUniqueId()) || entry.getValue().equals(recipient.getUniqueId())
        );
    }

    public boolean requestAlreadyExsists(Player sender, Player recipient){
        if (plugin.getTeleportRequests().containsKey(recipient.getUniqueId()) &&
                plugin.getTeleportRequests().containsKey(sender.getUniqueId())) {
            return true;
        }else {
            return false;
        }
    }
}
