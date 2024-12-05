package org.mellurboo.advancedPlayerTP.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mellurboo.advancedPlayerTP.AdvancedPlayerTP;
import org.mellurboo.advancedPlayerTP.impl.MessageFormat;
import org.mellurboo.advancedPlayerTP.impl.RequestHandler;

import org.jetbrains.annotations.NotNull;

public class tpa implements CommandExecutor {

    private final RequestHandler requestHandler;
    private final MessageFormat messageHandler;
    private final AdvancedPlayerTP plugin;

    private Player sender, recipient;

    public tpa(AdvancedPlayerTP plugin) {
        this.plugin = plugin;
        requestHandler = new RequestHandler(plugin);
        messageHandler = new MessageFormat(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        sender = ((Player) commandSender).getPlayer();
        if (args.length == 0) {
            sender.sendMessage(plugin.BadCommandUsage);
            return true;
        }

        recipient = Bukkit.getPlayer(args[args.length - 1]);

        switch (args[0].toLowerCase()) {
            case "accept":
            case "a":
                if (args.length != 2) break;
                acceptTeleport(sender, recipient);
                break;
            case "deny":
            case "d":
                if (args.length != 2) break;
                denyTeleport(sender, recipient);
                break;
            case "cancel":
            case "c":
                if (args.length != 2) break;
                cancelTeleport(sender, recipient);
                break;
            case "clear":
            case "cl":
                clearAllRequests(sender);
                break;
            case "reload":
                reloadConfig(sender);
                break;
            case "toggle":
                toggleTPA(sender);
                break;
            default:
                if (recipient != null) requestTeleport(sender, recipient);
                break;
        }
        return true;
    }

    /// Handle Requesting a Player Teleport
    private void requestTeleport(Player sender, Player recipient){
        if (!plugin.tpaEnabled){
            sender.sendMessage(plugin.TPADisabled);
            return;
        }

        if (recipient == null) {
            sender.sendMessage(plugin.BadCommandUsage);
            return;
        }

        if (sender == recipient) {
            messageHandler.insertPlayerName(plugin.Sender_TPToSelf, sender, sender);
            return;
        }

        // check for any repeat requests
        if (requestHandler.requestAlreadyExsists(sender, recipient)) {
            messageHandler.insertRecipientName(plugin.ExistingRequest, recipient, sender);
            return;
        }

        // check the player has enough bag
        if (sender.getLevel() >= plugin.levelCost){
            plugin.getTeleportRequests().put(recipient.getUniqueId(), sender.getUniqueId());
            messageHandler.insertPlayerName(plugin.Recipient_TPAPrompt, sender, recipient);
            messageHandler.insertRecipientName(plugin.Sender_TPAPrompt, recipient, sender);
        }else {
            String levelCost = "" + plugin.levelCost;
            messageHandler.sendFormattedMessage(plugin.Sender_LowExp, "%cost%", levelCost, sender);
        }
    }

    private void acceptTeleport(Player sender, Player recipient){
        if (recipient == null || !recipient.isOnline()) {
            sender.sendMessage(plugin.Sender_RecipientNotOnline);
            return;
        }

        if (!plugin.getTeleportRequests().containsKey(sender.getUniqueId())) {
            sender.sendMessage(plugin.noTPARequests);
            return;
        }

        // Make sure the sender is still online so we can still access their location
        if (sender.isOnline()){
            requestHandler.acceptRequest(sender, recipient);
            messageHandler.insertRecipientName(plugin.Sender_RecipientAccepted, recipient, sender);
            messageHandler.insertPlayerName(plugin.Recipient_YouAccepted, sender, recipient);
        }else {
            recipient.sendMessage(plugin.Recipient_SenderNotOnline);
        }
    }

    /// This one is basically just a wrapper for chat messages lol
    private void denyTeleport(Player sender, Player recipient){
        if (recipient == null || !recipient.isOnline()) {
            sender.sendMessage(plugin.Sender_RecipientNotOnline);
            return;
        }

        requestHandler.denyRequest(sender, recipient);

        messageHandler.insertRecipientName(plugin.Sender_RecipientDenied, recipient, sender);
        messageHandler.insertPlayerName(plugin.Recipient_YouDenied, sender, recipient);
    }

    /// This one is the same function from like 5 lines ago in reverse
    /// im getting really bored writing these comments
    private void cancelTeleport(Player sender, Player recipient){
        if (recipient == null || !recipient.isOnline()) {
            sender.sendMessage(plugin.Sender_RecipientNotOnline);
            return;
        }

        requestHandler.cancelRequest(sender, recipient);
        messageHandler.insertRecipientName(plugin.Sender_RequestCanceled, recipient, sender);
        messageHandler.insertPlayerName(plugin.Recipient_Canceled, sender, recipient);
    }

    /// Allow admins to just clear all requests
    private void clearAllRequests(Player sender){
        if (sender.hasPermission("ReallyTPA.admin")){
            plugin.getTeleportRequests().clear();
            Bukkit.broadcastMessage(plugin.AdminClosedAllRequests);
        }
    }

    /// Config reloading
    private void reloadConfig(Player sender){
        if (sender.hasPermission("ReallyTPA.admin")){
            plugin.reloadConfig();
            sender.sendMessage(plugin.PluginConfigReloaded);
        }
    }

    /// Admin TPA Toggle
    private void toggleTPA(Player sender){
        if (sender.hasPermission("ReallyTPA.admin") || sender.isOp()){
            if (plugin.tpaEnabled){
                plugin.tpaEnabled = false;
                messageHandler.sendFormattedMessage(plugin.TPAStatus, "%state%", "false", sender);
                Bukkit.broadcastMessage(plugin.TPADisabled);
            }else {
                plugin.tpaEnabled = true;
                messageHandler.sendFormattedMessage(plugin.TPAStatus, "%state%", "true", sender);
            }
        }
    }
}
