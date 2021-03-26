package com.github.militaermilitz;

import com.github.militaermilitz.command.BattleshipCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * Plugin Entry Class. This class register needed Staff.
 *
 */
public final class CatchTheBoat extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register Commands
        getCommand("battleship").setExecutor(new BattleshipCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
