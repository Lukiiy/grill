package me.lukiiy.grill;

import org.bukkit.Difficulty;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class VillagerStuff implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void villagerDmg(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Villager villager) || villager.getWorld().getDifficulty() != Difficulty.HARD || e.getFinalDamage() <= villager.getHealth()) return;

        e.setCancelled(true);

        villager.getScheduler().run(Grill.getInstance(), (_) -> {
            if (villager.isValid() && !villager.isDead()) villager.zombify();
        }, null);
    }
}
