package me.lukiiy.grill;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.*;

public class CoolCompass implements Listener {
    private final Map<UUID, Session> sessions = new HashMap<>();

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        Action a = e.getAction();
        if (a != Action.RIGHT_CLICK_BLOCK) return;

        Player p = e.getPlayer();
        if (!compassHeld(p)) return;

        e.setCancelled(true);

        Session session = sessions.get(p.getUniqueId());

        if (session == null) open(p); else session.cycle(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void slotChange(PlayerItemHeldEvent e) {
        e.getPlayer().getScheduler().run(Grill.getInstance(), _ -> check(e.getPlayer()), null);
    }

    @EventHandler
    public void itemSwap(PlayerSwapHandItemsEvent e) {
        e.getPlayer().getScheduler().run(Grill.getInstance(), _ -> check(e.getPlayer()), null);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        close(e.getPlayer());
    }

    private void open(Player p) {
        CompassMode[] mode = { CompassMode.SPAWN };

        p.setCompassTarget(resolve(p, mode[0]));

        ScheduledTask task = p.getScheduler().runAtFixedRate(Grill.getInstance(), _ -> {
            if (!compassHeld(p)) {
                close(p);
                return;
            }

            Session session = sessions.get(p.getUniqueId());
            if (session == null) return;

            p.setCompassTarget(resolve(p, session.mode[0]));
            p.sendActionBar(bar(p, session.mode[0]));
        }, null, 1L, 20L);

        sessions.put(p.getUniqueId(), new Session(task, mode));
        p.sendActionBar(bar(p, mode[0]));
    }

    private void close(Player p) {
        Session session = sessions.remove(p.getUniqueId());
        if (session == null) return;

        session.task.cancel();
        p.sendActionBar(Component.empty());
    }

    private void check(Player p) {
        if (sessions.containsKey(p.getUniqueId()) && !compassHeld(p)) close(p);
    }

    private boolean compassHeld(Player p) {
        return p.getInventory().getItemInMainHand().getType() == Material.COMPASS || p.getInventory().getItemInOffHand().getType() == Material.COMPASS;
    }

    private static Location resolve(Player p, CompassMode mode) {
        return switch (mode) {
            case SPAWN -> {
                Location b = p.getRespawnLocation();

                yield b != null ? b : p.getWorld().getSpawnLocation();
            }

            case WORLD_SPAWN -> p.getWorld().getSpawnLocation();
        };
    }

    private static Component bar(Player p, CompassMode mode) {
        Location from = p.getLocation();
        Location to = resolve(p, mode);

        double dx = from.getX() - to.getX();
        double dz = from.getZ() - to.getZ();

        return Component.text(mode.label + " - " + (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2)) + " blocos");
    }

    private record Session(ScheduledTask task, CompassMode[] mode) {

        void cycle(Player p) {
                mode[0] = (mode[0] == CompassMode.SPAWN) ? CompassMode.WORLD_SPAWN : CompassMode.SPAWN;

                p.setCompassTarget(resolve(p, mode[0]));
                p.sendActionBar(bar(p, mode[0]));
            }
        }

    private enum CompassMode {
        SPAWN("Seu spawn"),
        WORLD_SPAWN("Spawn mundial");

        final String label;

        CompassMode(String l) {
            label = l;
        }
    }
}
