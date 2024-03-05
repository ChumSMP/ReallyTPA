package com.mellurboo.chumtpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TeleportHandler implements CommandExecutor {
    private final Chumtpa plugin;
    private final RequestHandler requestHandler;

    public TeleportHandler(Chumtpa plugin) {
        this.plugin = plugin;
        this.requestHandler = new RequestHandler(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player senderPlayer = (Player) sender;
        Map<UUID, UUID> teleportRequests = plugin.getTeleportRequests();

        if (command.getName().equalsIgnoreCase("tpa")) {
            if (args.length != 1) {
                senderPlayer.sendMessage("§cUsage: /tpa <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);

            // checks if the recipient of the request is even online
            if (target == null || !target.isOnline()) {
                senderPlayer.sendMessage(plugin.playerNotFound);
                return true;
            }

            // checks for outstanding requests
            if (teleportRequests.containsKey(senderPlayer.getUniqueId()) && (teleportRequests.containsKey(target.getUniqueId()))) {
                senderPlayer.sendMessage(plugin.outstandingRequest);
                return true;
            }

            // Check if the player meets exp requirements
            if (senderPlayer.getLevel() >= plugin.levelCost) {
                teleportRequests.put(target.getUniqueId(), senderPlayer.getUniqueId());
                target.sendMessage(senderPlayer.getName() + " " + plugin.recipientPrompt);
                senderPlayer.sendMessage(plugin.senderPrompt + target.getName());
            } else {
                sender.sendMessage(plugin.notEnoughExp + plugin.levelCost);
            }
        } else if (command.getName().equalsIgnoreCase("tpaccept")) {
            // does the sender have any outstanding requests?
            if (!teleportRequests.containsKey(senderPlayer.getUniqueId())) {
                senderPlayer.sendMessage(plugin.noRequests);
                return true;
            }

            // setting up variables for targets and sender requests
            UUID targetUUID = senderPlayer.getUniqueId();
            UUID senderUUID = teleportRequests.remove(targetUUID);
            Player requester = Bukkit.getPlayer(senderUUID);

            // if there is a valid requester and they're online, it will take away the exp.
            // TP the player and send the messages
            if (requester != null && requester.isOnline()) {
                requestHandler.acceptRequest(senderPlayer, requester, plugin.levelCost, plugin.clearexpbar);
            } else {
                senderPlayer.sendMessage(plugin.playerNotOnline);
            }
        } else if (command.getName().equalsIgnoreCase("tpdeny")) {
            // check if the requests map contains the key for the request
            if (!teleportRequests.containsKey(senderPlayer.getUniqueId())) {
                senderPlayer.sendMessage(plugin.noRequests);
                return true;
            }

            // clearing the target's UUID from the map
            UUID targetUUID = senderPlayer.getUniqueId();
            UUID senderUUID = teleportRequests.remove(targetUUID);
            Player recipient = Bukkit.getPlayer(senderUUID);

            // if the recipient is online and not null then we can send the appropriate messages
            if (recipient != null && recipient.isOnline()) {
                requestHandler.denyRequest(senderPlayer, recipient);
            } else {
                senderPlayer.sendMessage(plugin.playerNotOnline);
            }
        } else if (command.getName().equalsIgnoreCase("tpcancel")) {
            // even if they have no tpa requests open I can't be bothered writing a sentence for it
            if (!teleportRequests.containsKey(senderPlayer.getUniqueId())) {
                senderPlayer.sendMessage(plugin.tpaClosed);
                return true;
            }

            // clear the request
            UUID targetUUID = senderPlayer.getUniqueId();
            UUID senderUUID = teleportRequests.remove(targetUUID);
            Player targetPlayer = Bukkit.getPlayer(targetUUID);

            // if the player is valid and online we can tell them
            if (targetPlayer != null && targetPlayer.isOnline()) {
                requestHandler.cancelRequest(senderPlayer, targetPlayer);
            }

            // then also inform the player of the good news
            senderPlayer.sendMessage(plugin.tpaClosed);
        } else if (command.getName().equalsIgnoreCase("tpacloseall") && sender.isOp()) {
            // tell everyone about it then clear the hash map
            Bukkit.broadcastMessage(plugin.adminClosedAllRequests);
            teleportRequests.clear();
        } else if (command.getName().equalsIgnoreCase("tpreload") && sender.isOp()) {
            // reload the config then notify the admin
            plugin.loadConfig();
            senderPlayer.sendMessage(plugin.configReloaded);
        }

        return false;
    }
}
