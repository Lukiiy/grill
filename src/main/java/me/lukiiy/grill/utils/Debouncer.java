package me.lukiiy.grill.utils;

import io.papermc.paper.util.Tick;
import me.lukiiy.grill.Grill;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class Debouncer<K> {
    private final long delay = Tick.tick().fromDuration(Duration.ofMinutes(1));
    private final ConcurrentHashMap<K, Entry> entries = new ConcurrentHashMap<>();

    public void submit(K key, Runnable task) {
        Entry entry = entries.compute(key, (k, old) -> {
            if (old == null) old = new Entry();

            old.task = task;
            old.version++;

            return old;
        });

        long snapshot = entry.version;

        Bukkit.getServer().getGlobalRegionScheduler().runDelayed(Grill.getInstance(), _ -> flush(key, snapshot), delay);
    }

    private void flush(K key, long snapshot) {
        Entry entry = entries.get(key);
        if (entry == null || entry.version != snapshot) return; // stale

        entries.remove(key);

        Runnable task = entry.task;
        if (task == null) return;

        Bukkit.getServer().getAsyncScheduler().runNow(Grill.getInstance(), scheduled -> {
            try {
                task.run();
            } catch (Exception ex) {
                Grill.getInstance().getLogger().warning("Debounced task failed for key '" + key + "': " + ex.getMessage());
            }
        });
    }

    public void cancel(K key) {
        entries.remove(key);
    }

    public void clear() {
        entries.clear();
    }

    private static final class Entry {
        private Runnable task;
        private long version;
    }
}