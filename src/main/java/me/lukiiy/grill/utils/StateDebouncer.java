package me.lukiiy.grill.utils;

import io.papermc.paper.util.Tick;
import me.lukiiy.grill.Grill;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class StateDebouncer<K, S> {
    private final long delay;
    private final ConcurrentHashMap<K, Entry<S>> entries = new ConcurrentHashMap<>();

    public StateDebouncer(Duration delay) {
        this.delay = Tick.tick().fromDuration(delay);
    }

    public void submit(K key, S state, Runnable task) {
        Entry<S> entry = new Entry<>(state, task);

        entries.put(key, entry);

        Bukkit.getGlobalRegionScheduler().runDelayed(Grill.getInstance(), _ -> flush(key, entry), delay);
    }

    private void flush(K key, Entry<S> expected) {
        if (!entries.remove(key, expected)) return;

        Bukkit.getAsyncScheduler().runNow(Grill.getInstance(), _ -> {
            try {
                expected.task.run();
            } catch (Exception ex) {
                Grill.getInstance().getLogger().warning(ex.getMessage());
            }
        });
    }

    public void cancel(K key) {
        entries.remove(key);
    }

    public void clear() {
        entries.clear();
    }

    private record Entry<S>(S state, Runnable task) {}
}