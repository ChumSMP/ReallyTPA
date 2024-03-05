package com.mellurboo.chumtpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Chumtpa extends JavaPlugin implements Listener {
    private Map<UUID, UUID> teleportRequests = new HashMap<>();  // this stores all of the requests

    public String playerNotFound; // the player for the request couldn't be found
    public String recipientPrompt; // shown when you receive a tpa request
    public String senderPrompt; // shown when a tpa request was sent correctly
    public String noRequests; // shown when /tpaccept is run when no requests are active
    public String requestAcceptedSender; // shown to the sender when a request is accepted
    public String recipientAccepted; // shown to the recipient when they accept a request
    public String playerNotOnline; // shown when someone makes a tpa request to a player who's offline
    public String senderDeniedTPA;
    public String recipientDeniedTPA;
    public String notEnoughExp;
    public String outstandingRequest;
    public String tpaClosed;
    public String tpaCanceledRecipient;
    public String adminClosedAllRequests;
    public String configReloaded;

    public int levelCost;
    public boolean clearexpbar;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getCommand("tpa").setExecutor(new TeleportHandler(this));
        getCommand("tpaccept").setExecutor(new TeleportHandler(this));
        getCommand("tpdeny").setExecutor(new TeleportHandler(this));
        getCommand("tpreload").setExecutor(new TeleportHandler(this));
        getCommand("tpcancel").setExecutor(new TeleportHandler(this));
        getCommand("tpacloseall").setExecutor(new TeleportHandler(this));
    }


    public void loadConfig(){
        FileConfiguration config = getConfig();
        playerNotFound = config.getString("PlayerNotFound", "§cPlayer Not Found or they're not online");
        recipientPrompt = config.getString("recipientPrompt", "§e Has requested to teleport to you, use /tpaccept or /tpdeny");
        senderPrompt = config.getString("senderPrompt", "§eTeleport request sent to ");
        noRequests = config.getString("noRequests", "§cYou have no pending request(s)");
        requestAcceptedSender = config.getString("requestAcceptedSender", "§e Has accepted your TP request");
        recipientAccepted = config.getString("recipientAccepted", "§e Teleport Request Accepted");
        playerNotOnline = config.getString("playerNotOnline", "§c the player who sent this request is no longer online!");
        senderDeniedTPA = config.getString("senderDeniedTPA", "§c Has denied your teleport request");
        recipientDeniedTPA = config.getString("recipientDeniedTPA", "§cTeleport request denied");
        notEnoughExp = config.getString("senderNotEnoughExp", "§cYou do not have enough exp to complete this request, cost: ");
        outstandingRequest = config.getString("exsistingRequest", "§cYou both already have a pending request, do /tpcancel before making a new request");
        tpaClosed = config.getString("closedAllRequests", "§cYour TPA requests have been successfully closed");
        tpaCanceledRecipient = config.getString("recipientCancel", "§cA teleport request was closed by: ");
        adminClosedAllRequests = config.getString("adminClosedAllRequests", "§cAll teleport requests were closed by an Admin");
        configReloaded = config.getString("configReloaded", "§eChumTPA config reloaded!");

        levelCost = config.getInt("levelCost",27);
        clearexpbar = config.getBoolean("clearexpbar", true);
    }

    // ... (other methods)

    public Map<UUID, UUID> getTeleportRequests() {
        return teleportRequests;
    }
}