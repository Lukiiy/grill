package me.lukiiy.grill;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.lukiiy.grill.commands.Main;
import me.lukiiy.grill.commands.Playtime;
import me.lukiiy.grill.commands.WaypointColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Grill extends JavaPlugin {
    private DiscordHook discordHook;

    public static final Component COMMAND_ERR_NONPLAYER = Component.text("Apenas jogadores podem utilizar esse comando!").color(NamedTextColor.RED);

    @Override
    public void onEnable() {
        setupConfig();

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new Listen(), this);
        pluginManager.registerEvents(new Oppe(), this);
        pluginManager.registerEvents(new VillagerStuff(), this);

        setupDiscordWebhook();

        if (discordHook != null) discordHook.sendMessage(getConfig().getString("dcWebhook.start"));

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, (e) -> {
            e.registrar().register("grill", "Recarrega Grill", new Main());
            e.registrar().register("playtime", "Mostra seu tempo de jogatina", new Playtime());
            e.registrar().register("pointcolor", "Muda a cor do seu Waypoint!", new WaypointColor());
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
