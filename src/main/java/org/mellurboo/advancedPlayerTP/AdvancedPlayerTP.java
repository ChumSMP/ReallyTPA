package org.mellurboo.advancedPlayerTP;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mellurboo.advancedPlayerTP.commands.tpa;
import org.mellurboo.advancedPlayerTP.commands.tpaTabComplete;
import org.mellurboo.advancedPlayerTP.impl.MessageFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AdvancedPlayerTP extends JavaPlugin implements Listener {
    // Store all the player requests here so all classes in 'impl' can access it
    // without any issue, I found putting it here is weird, but it's cleaner elsewhere
    private Map<UUID, UUID> teleportRequests = new HashMap<>();

    public boolean tpaEnabled = true;
    public MessageFormat messageFormat;

    public String
            Recipient_TPAPrompt,
            Recipient_YouAccepted,
            Recipient_YouDenied,
            Recipient_Canceled,
            Recipient_SenderNotOnline,
            Sender_TPAPrompt,
            Sender_RecipientAccepted,
            Sender_RecipientDenied,
            Sender_RecipientNotOnline,
            Sender_RequestCanceled,
            Sender_LowExp,
            Sender_TPToSelf,
            ExistingRequest,
            noTPARequests,
            PlayerClosedAllRequests,
            TPADisabled,
            AdminClosedAllRequests,
            PluginConfigReloaded,
            BadCommandUsage,
            TPAStatus;

    public int levelCost;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        registerComponents();

        getCommand("tpa").setExecutor(new tpa(this));
        getCommand("tpa").setTabCompleter(new tpaTabComplete());
    }

    public void loadConfig(){
        FileConfiguration config = getConfig();

        Recipient_TPAPrompt = config.getString("Recipient_TPAPrompt");
        Sender_TPAPrompt = config.getString("Sender_TPAPrompt");
        Recipient_SenderNotOnline = getConfig().getString("Recipient_SenderNotOnline");
        noTPARequests = config.getString("Sender_NoTPARequests");
        Sender_RecipientAccepted = config.getString("Sender_RecipientAccepted");
        Recipient_YouAccepted = config.getString("Recipient_YouAccepted");
        Sender_RecipientNotOnline = config.getString("Sender_RecipientNotOnline");
        Sender_RecipientDenied = config.getString("Sender_RecipientDenied");
        Recipient_YouDenied = config.getString("Recipient_YouDenied");
        Sender_LowExp = config.getString("Sender_LowExp");
        ExistingRequest = config.getString("Sender_ExistingRequest");
        Recipient_Canceled = config.getString("Recipient_Canceled");
        AdminClosedAllRequests = config.getString("AdminClosedAllRequests");
        PluginConfigReloaded = config.getString("ConfigReloaded");
        Sender_RequestCanceled = config.getString("Sender_RequestCanceled");
        BadCommandUsage = config.getString("BadCommandUsage");
        Sender_TPToSelf = config.getString("Sender_TPToSelf");
        TPADisabled = config.getString("TPADisabled");
        TPAStatus = config.getString("TPAStatus");

        levelCost = config.getInt("levelCost");
    }

    private void registerComponents(){
        messageFormat = new MessageFormat(this);
    }

    public Map<UUID, UUID> getTeleportRequests() {
        return teleportRequests;
    }
}