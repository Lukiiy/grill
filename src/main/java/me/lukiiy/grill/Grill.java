package me.lukiiy.grill;

import org.bukkit.plugin.java.JavaPlugin;

public final class Grill extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listen(), this);
    }

    public static Grill getInstance() {
        return JavaPlugin.getPlugin(Grill.class);
    }
}
