package com.github.militaermilitz.mcUtil;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author Alexander Ley
 * @version 1.0
 * This class offers the block Placing action in a runnable.
 */
public class BlockPlacingRunnable implements Runnable{

    private final Location location;
    private final Material material;

    public BlockPlacingRunnable(Location location, Material material) {
        if (location.getWorld() == null) throw new IllegalArgumentException("Location have no world.");
        this.location = location;
        this.material = material;
    }

    @Override
    public void run() {
        location.getBlock().setType(material);
    }
}
