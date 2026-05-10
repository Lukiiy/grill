package me.lukiiy.grill;

import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Oppe implements Listener {
    private final MobGoals goalsManager = Bukkit.getServer().getMobGoals();
    private final Set<UUID> entityItemPersistent = new HashSet<>();

    @EventHandler
    public void spawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            Entity entity = e.getEntity();

            if (entity instanceof Dolphin dolphin) goalsManager.removeGoal(dolphin, VanillaGoal.DOLPHIN_SWIM_TO_TREASURE);
            if (entity instanceof Cat cat) goalsManager.removeGoal(cat, VanillaGoal.CAT_SIT_ON_BLOCK);

            return;
        }

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            Mob entity = (Mob) e.getEntity();

            entity.setCollidable(false);
            entity.setCanPickupItems(false);
            entity.setPersistent(false);
            entity.setRemoveWhenFarAway(true);

            entity.getScheduler().runDelayed(Grill.getInstance(), (t) -> {
                if (!entity.isValid()) return;

                boolean active = false;
                EntityType type = entity.getType();

                for (Entity nearby : entity.getNearbyEntities(8, 4, 8)) {
                    if (!(nearby instanceof LivingEntity living) || nearby == entity || nearby.getType() == type || !entity.hasLineOfSight(living)) continue;

                    active = true;
                    break;
                }

                entity.setAI(active);
                entity.setAware(active);
                entity.setJumping(active);
            }, null, 1L);
        }
    }

    @EventHandler
    public void itemPersist(EntityPickupItemEvent e) {
        LivingEntity entity = e.getEntity();

        if (entity.getSpawnCategory() != SpawnCategory.MONSTER) return;

        entity.getScheduler().runDelayed(Grill.getInstance(), (t) -> {
            if (entity.isPersistent()) entityItemPersistent.add(entity.getUniqueId());
        }, null, 2L);
    }

    @EventHandler
    public void mobDespawnAttempt(EntityRemoveEvent e) {
        if (!(e.getEntity() instanceof LivingEntity living) || !entityItemPersistent.contains(living.getUniqueId())) return;

        entityItemPersistent.remove(living.getUniqueId());

        if (living.getEquipment() == null) return;

        living.setHealth(0);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(Grill.getInstance(), () -> adjustRenderDist(e.getPlayer()), 10L);
    }

    @EventHandler
    public void worldChange(PlayerChangedWorldEvent e) {
        Bukkit.getScheduler().runTaskLater(Grill.getInstance(), () -> adjustRenderDist(e.getPlayer()), 10L);
    }

    private void adjustRenderDist(Player p) {
        World w = p.getWorld();

        int offset = switch (w.getEnvironment()) {
            case NETHER -> 2;
            case THE_END -> 1;
            default -> 0;
        };

        p.setSendViewDistance(Math.max(4, Math.min(w.getViewDistance(), p.getClientViewDistance()) - offset));
        p.setSimulationDistance(Math.max(3, Math.min(w.getSimulationDistance(), p.getSimulationDistance()) - offset));
    }
}
