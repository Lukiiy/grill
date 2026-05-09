package me.lukiiy.grill;

import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;

public class Oppe implements Listener {
    private final MobGoals goalsManager = Bukkit.getServer().getMobGoals();
    private final Set<LivingEntity> entityItemPersistent = new HashSet<>();

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
            entity.setJumping(false);
        }
    }

    @EventHandler
    public void itemPersist(EntityPickupItemEvent e) {
        LivingEntity entity = e.getEntity();

        if (entity.getSpawnCategory() != SpawnCategory.MONSTER) return;

        Bukkit.getScheduler().runTaskLater(Grill.getInstance(), () -> {
            if (entity.isPersistent()) entityItemPersistent.add(entity);
        }, 2L);
    }

    @EventHandler
    public void mobDespawnAttempt(EntityRemoveFromWorldEvent e) {
        if (!(e.getEntity() instanceof LivingEntity living) || !entityItemPersistent.contains(living) || living.getEquipment() == null) return;

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
        int dist = Math.min(p.getWorld().getViewDistance(), p.getClientViewDistance());
        if (dist < 4) return;

        if (p.getWorld().getEnvironment() == World.Environment.NETHER) dist -= 2;
        if (p.getWorld().getEnvironment() == World.Environment.THE_END) dist -= 1;

        p.setSendViewDistance(dist);
    }
}
