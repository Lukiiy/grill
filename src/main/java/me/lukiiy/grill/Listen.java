package me.lukiiy.grill;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listen implements Listener {
    @EventHandler
    public void join(PlayerJoinEvent e) {
        DiscordHook hook = Grill.getInstance().getDiscordHook();

        if (hook != null) hook.sendMessage(Grill.getInstance().getConfig().getString("dcWebhook.join", "").replace("%p", PlainTextComponentSerializer.plainText().serialize(e.getPlayer().displayName())));
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        DiscordHook hook = Grill.getInstance().getDiscordHook();

        if (hook != null) hook.sendMessage(Grill.getInstance().getConfig().getString("dcWebhook.leave", "").replace("%p", PlainTextComponentSerializer.plainText().serialize(e.getPlayer().displayName())));
    }

    @EventHandler
    public void respawn(PlayerPostRespawnEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLastDeathLocation();
        if (loc == null) return;

        String dim;
        switch (p.getLastDeathLocation().getWorld().getEnvironment()) {
            case NORMAL -> dim = "Overworld";
            case NETHER -> dim = "Nether";
            case THE_END -> dim = "The End";
            default -> dim = "Custom";
        }

        String msg = Grill.getInstance().getConfig().getString("deathRecordMsg", "").replace("%x", loc.blockX() + "").replace("%y", loc.blockY() + "").replace("%z", loc.blockZ() + "").replace("%d", dim);

        p.sendMessage(MiniMessage.miniMessage().deserialize(msg));
    }
}
