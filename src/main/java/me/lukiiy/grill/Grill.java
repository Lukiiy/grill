package me.lukiiy.grill;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.lukiiy.grill.commands.Main;
import me.lukiiy.grill.commands.Playtime;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Grill extends JavaPlugin {
    private DiscordHook discordHook;

    @Override
    public void onEnable() {
        setupConfig();

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new Listen(), this);
        pluginManager.registerEvents(new Oppe(), this);

        setupDiscordWebhook();

        if (discordHook != null) discordHook.sendMessage(getConfig().getString("dcWebhook.start"));

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            event.registrar().register("grill", "Recarrega Grill", new Main());
            event.registrar().register("playtime", "Mostra seu tempo de jogatina", new Playtime());
        });
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

    public void reloadConfig() {
        super.reloadConfig();

        setupDiscordWebhook();
    }

    private void setupDiscordWebhook() {
        try {
            discordHook = new DiscordHook(getConfig().getString("dcWebhook.id"), getConfig().getString("dcWebhook.token"));
        } catch (Exception e) {
            discordHook = null;

            getLogger().warning("Discord webhook disabled: " + e.getMessage());
        }
    }

    public DiscordHook getDiscordHook() {
        return discordHook;
    }
}
