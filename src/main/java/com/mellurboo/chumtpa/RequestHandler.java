package com.mellurboo.chumtpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class RequestHandler {
    private final Chumtpa plugin;

    public RequestHandler(Chumtpa plugin) {
        this.plugin = plugin;
    }

    // Teleport player method
    public void teleportPlayer(Player sender, Player target) {
        sender.teleport(target.getLocation());
        removeRequest(sender, target);
    }

    // Method to handle accepting a teleport request
    public void acceptRequest(Player senderPlayer, Player requester, int levelCost, boolean clearexpbar) {
        if (requester.isOp()){
            teleportPlayer(requester, senderPlayer);
            requester.sendMessage("§e" + senderPlayer.getName() + " Has accepted your TP request");
            senderPlayer.sendMessage("§e Teleport Request Accepted");
        }

        if (requester.getLevel() >= levelCost){
            requester.setLevel(requester.getLevel() - levelCost);

            if (clearexpbar) {requester.setExp(0);}

            teleportPlayer(requester, senderPlayer);
            requester.sendMessage("§e" + senderPlayer.getName() + " Has accepted your TP request");
            senderPlayer.sendMessage("§e Teleport Request Accepted");
        } else {
            senderPlayer.sendMessage("§cYou do not have enough exp to complete this request, cost: " + levelCost);
        }

        removeRequest(senderPlayer, requester);
    }

    // Method to handle denying a teleport request
    public void denyRequest(Player senderPlayer, Player recipient) {
        recipient.sendMessage("§c" + senderPlayer.getName() + " Has denied your teleport request");
        senderPlayer.sendMessage("§cTeleport request denied");
        removeRequest(senderPlayer, recipient);
    }

    // Method to handle canceling teleport requests
    public void cancelRequest(Player senderPlayer, Player targetPlayer) {
        if (targetPlayer != null && targetPlayer.isOnline()) {
            targetPlayer.sendMessage("§cA teleport request was closed by: " + senderPlayer.getName());
        }

        removeRequest(senderPlayer, targetPlayer);
        senderPlayer.sendMessage("§cYour TPA requests have been successfully closed");
    }

    // Method to remove the teleport request from the map
    private void removeRequest(Player sender, Player target) {
        Map<UUID, UUID> teleportRequests = plugin.getTeleportRequests();
        teleportRequests.entrySet().removeIf(entry ->
                entry.getKey().equals(sender.getUniqueId()) || entry.getValue().equals(sender.getUniqueId())
                        || entry.getKey().equals(target.getUniqueId()) || entry.getValue().equals(target.getUniqueId())
        );
    }
}

