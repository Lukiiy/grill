package me.lukiiy.grill;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.EnumSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EntityVariants implements Listener {
    private final Random random = ThreadLocalRandom.current();
    private static final EnumSet<CreatureSpawnEvent.SpawnReason> IGNORED_REASONS = EnumSet.of(CreatureSpawnEvent.SpawnReason.CURED, CreatureSpawnEvent.SpawnReason.BREEDING, CreatureSpawnEvent.SpawnReason.CUSTOM, CreatureSpawnEvent.SpawnReason.COMMAND, CreatureSpawnEvent.SpawnReason.SPAWNER); // TODO: Command?


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (IGNORED_REASONS.contains(event.getSpawnReason())) return;
    }
}
