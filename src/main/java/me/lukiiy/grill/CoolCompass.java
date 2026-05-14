package me.lukiiy.grill;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.lukiiy.wayTrick.WayTrick;
import net.kyori.adventure.text.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.geysermc.geyser.api.GeyserApi;

import java.util.*;

public class CoolCompass implements Listener {
    private final Map<UUID, Mode> modes = new HashMap<>();
    private final Map<UUID, WayTrick> locators = new HashMap<>();
    private final Map<UUID, ScheduledTask> tasks = new HashMap<>();

    enum Mode {
        SPAWN,
        WORLD
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void heldChange(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        if (isCompass(player.getInventory().getItem(e.getNewSlot()))) activate(player); else deactivate(player);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        deactivate(player);
        modes.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void interaction(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!isCompass(e.getItem())) return;

        e.setUseInteractedBlock(Event.Result.DENY);

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Mode next = modes.getOrDefault(uuid, Mode.SPAWN) == Mode.SPAWN ? Mode.WORLD : Mode.SPAWN;

        modes.put(uuid, next);
        updateTarget(player, next);
    }

    private void activate(Player player) {
        UUID uuid = player.getUniqueId();
        if (locators.containsKey(uuid)) return;

        WayTrick locator = new WayTrick();

        locator.addViewer(player);
        locators.put(uuid, locator);

        ScheduledTask task = player.getScheduler().runAtFixedRate(Grill.getInstance(), _ -> locator.updateAll(), () -> deactivate(player), 1L, 1L);

        tasks.put(uuid, task);
        updateTarget(player, modes.getOrDefault(uuid, Mode.SPAWN));
    }

    private void deactivate(Player player) {
        UUID uuid = player.getUniqueId();
        WayTrick locator = locators.remove(uuid);
        if (locator == null) return;

        locator.removeViewer(player);

        ScheduledTask task = tasks.remove(uuid);
        if (task != null) task.cancel();
    }

    private void updateTarget(Player player, Mode mode) {
        Location target = switch (mode) {
            case SPAWN -> player.getRespawnLocation();
            case WORLD -> player.getWorld().getEnvironment() == World.Environment.NORMAL ? player.getWorld().getSpawnLocation() : null;
        };

        if (target == null) {
            player.sendActionBar(Component.text("Localização não encontrada."));
            return;
        }

        player.setCompassTarget(target);

        WayTrick locator = locators.get(player.getUniqueId());
        if (locator == null) return;

        Waypoint.Icon icon = new Waypoint.Icon();

        icon.color = Optional.of(0xFFF);
        icon.style = WaypointStyleAssets.BOWTIE;
        locator.trackTarget(player, icon, (_, _) -> new Vec3(target.getX(), target.getY(), target.getZ()));

        String label = mode == Mode.SPAWN ? "Spawn" : "Spawn do Mundo";
        String coords = GeyserApi.api().isBedrockPlayer(player.getUniqueId()) ? "" : "   " + target.getBlockX() + " " + target.getBlockY() + " " + target.getBlockZ();

        player.sendActionBar(Component.text(label + coords));
    }

    private static boolean isCompass(ItemStack item) {
        return item != null && item.getType() == Material.COMPASS;
    }
}
