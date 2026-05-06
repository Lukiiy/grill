package me.lukiiy.grill;

import org.bukkit.plugin.java.JavaPlugin;

public final class Grill extends JavaPlugin {
    private DiscordHook discordHook;

    @Override
    public void onEnable() {
        setupConfig();

        getServer().getPluginManager().registerEvents(new Listen(), this);
        setupDiscordWebhook();

        if (discordHook != null) discordHook.sendMessage(getConfig().getString("dcWebhook.start"));
    }

    @Override
    public void onDisable() {
        if (discordHook != null) discordHook.sendMessage(getConfig().getString("dcWebhook.stop"));
    }

    public static Grill getInstance() {
        return JavaPlugin.getPlugin(Grill.class);
    }

    private void setupConfig() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void setupDiscordWebhook() {
        try {
            discordHook = new DiscordHook(getConfig().getString("dcwebhook.id"), getConfig().getString("dcwebhook.token"));
        } catch (Exception e) {
            discordHook = null;

            getLogger().warning("Discord webhook disabled: " + e.getMessage());
        }
    }

    public DiscordHook getDiscordHook() {
        return discordHook;
    }
}
