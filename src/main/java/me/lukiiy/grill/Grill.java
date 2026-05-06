package me.lukiiy.grill;

import org.bukkit.plugin.java.JavaPlugin;

public final class Grill extends JavaPlugin {
    public DiscordHook discordHook;

    @Override
    public void onEnable() {
        setupConfig();

        getServer().getPluginManager().registerEvents(new Listen(), this);

        discordHook = new DiscordHook(getConfig().getString("dcwebhook.id"), getConfig().getString("dcwebhook.token"));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Grill getInstance() {
        return JavaPlugin.getPlugin(Grill.class);
    }

    public void setupConfig() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public DiscordHook getDiscordHook() {
        return discordHook;
    }
}
