package com.github.militaermilitz.mcUtil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Class is the Bukkit equivalent for an object with a scheduled task timer.
 */
public abstract class BukkitTickable {
    protected BukkitTask task;
    private final Plugin plugin;

    protected BukkitTickable(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts the Timer
     * @param delay Start delay.
     * @param period Timer period.
     */
    public void start(long delay, long period){
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, delay, period);
    }

    /**
     * Tick Loop
     */
    public abstract void tick();

    /**
     * Stops the Timer and makes it ready for next time.
     */
    public void stop() {
        try {
            task.cancel();
        }
        catch (Exception ignored) { }
    }
}
