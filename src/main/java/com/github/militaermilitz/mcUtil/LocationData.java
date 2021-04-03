package com.github.militaermilitz.mcUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Class is used to store data of Location which can stored with gson (e.g. world cannot saved with gson),
 */
public class LocationData implements IFileConstructor<Location>{
    //All needed Data to define a Location (Can saved with gson)
    private final String world;
    private final double x, y, z;
    private final float yaw, pith;

    /**
     * @param location Location where the needed data is extracted.
     */
    public LocationData(Location location){
        this.world = Objects.requireNonNull(location.getWorld()).getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pith = location.getPitch();
    }

    /**
     * This Class will be recursively saved by gson. Therefore is no saveToFile implementation needed.
     */
    @Override
    public void saveToFile() {

    }

    /**
     * @return Loads Location with extracted data.
     */
    @Override
    public Location load() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pith);
    }
}
