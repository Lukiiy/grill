package me.lukiiy.grill;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerStuff implements Listener {
    private static final BlockFace[] HORIZONTAL = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

    @EventHandler(priority = EventPriority.LOWEST)
    public void villagerDmg(EntityDamageEvent e) { // Força Villagers para serem zumbificados mesmo sem estar na dificuldade Hard
        if (!(e.getEntity() instanceof Villager villager) || villager.getWorld().getDifficulty() == Difficulty.HARD || e.getFinalDamage() <= villager.getHealth()) return;

        e.setCancelled(true);

        villager.getScheduler().run(Grill.getInstance(), (_) -> {
            if (!villager.isDead()) villager.zombify();
        }, null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void shareBestRep(PlayerInteractEntityEvent e) { // Compartilha a melhor reputação positiva existente para quem interagiu
        if (!(e.getRightClicked() instanceof Villager villager)) return;

        Player player = e.getPlayer();
        if (villager.getReputations().isEmpty()) return;

        Reputation best = null;
        int bestScore = 0;

        for (Reputation rep : villager.getReputations().values()) {
            int score = rep.getReputation(ReputationType.MAJOR_POSITIVE) + rep.getReputation(ReputationType.MINOR_POSITIVE);

            if (best == null || score > bestScore) {
                bestScore = score;
                best = rep;
            }
        }

        if (best == null) return;

        Reputation playerRep = villager.getReputation(player.getUniqueId());

        playerRep.setReputation(ReputationType.MAJOR_POSITIVE, best.getReputation(ReputationType.MAJOR_POSITIVE));
        playerRep.setReputation(ReputationType.MINOR_POSITIVE, best.getReputation(ReputationType.MINOR_POSITIVE));
        villager.setReputation(player.getUniqueId(), playerRep);
    }

    private boolean isBoxed(Villager villager) {
        World world = villager.getWorld();

        int x = villager.getLocation().getBlockX();
        int y = villager.getLocation().getBlockY();
        int z = villager.getLocation().getBlockZ();

        for (BlockFace face : HORIZONTAL) {
            Block feet = world.getBlockAt(x + face.getModX(), y, z + face.getModZ());
            Block head = world.getBlockAt(x + face.getModX(), y + 1, z + face.getModZ());

            if (feet.isPassable() && head.isPassable()) return false;
        }

        return true;
    }
}
