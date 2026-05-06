package me.lukiiy.grill;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Listen implements Listener {
    @EventHandler
    public void respawn(PlayerPostRespawnEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLastDeathLocation();
        if (loc == null) return;

        String dim;
        switch (p.getLastDeathLocation().getWorld().getEnvironment()) {
            case NORMAL -> dim = "Overworld";
            case NETHER -> dim = "Nether";
            case THE_END -> dim = "End";
            default -> dim = "Custom";
        }

        Component msg = Component.text("Você morreu em " + loc.blockX() + " " + loc.blockY() + " " + loc.blockZ() + " no " + dim).color(TextColor.color(0xff527a));

        p.sendMessage(msg);
    }
}
