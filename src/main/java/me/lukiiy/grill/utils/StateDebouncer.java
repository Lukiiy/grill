package me.lukiiy.grill.utils;

import io.papermc.paper.util.Tick;
import me.lukiiy.grill.Grill;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class StateDebouncer<K, S> {
    private final long delay;
    private final ConcurrentHashMap<K, Entry<S>> entries = new ConcurrentHashMap<>();

    public StateDebouncer(Duration delay) {
        this.delay = Tick.tick().fromDuration(delay);
    }

    public void submit(K key, S state, Runnable task) {
        Entry<S> entry = entries.compute(key, (_, old) -> {
            if (old == null) old = new Entry<>();

            old.state = state;
            old.task = task;
            old.version++;

            return old;
        });

        long snapshot = entry.version;

        Bukkit.getGlobalRegionScheduler().runDelayed(Grill.getInstance(), _ -> flush(key, state, snapshot), delay);
    }

    private void flush(K key, S state, long snapshot) {
        Entry<S> entry = entries.get(key);

        if (entry == null || !Objects.equals(entry.state, state) || entry.version != snapshot || !entries.remove(key, entry)) return;

        Bukkit.getAsyncScheduler().runNow(Grill.getInstance(), _ -> {
            try {
                entry.task.run();
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

    private static final class Entry<S> {
        private S state;
        private Runnable task;
        private long version;
    }
}