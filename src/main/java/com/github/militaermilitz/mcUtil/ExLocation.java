package com.github.militaermilitz.mcUtil;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Ley
 * @version 1.0
 * This class contains extra functionality for Loacations
 */
public class ExLocation extends Location {

    /**
     * Normal Loacation Constructor.
     */
    public ExLocation(@Nullable World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    /**
     * Normal Loacation Constructor.
     */
    public ExLocation(@Nullable World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    /**
     * Copies Location to create a new location.
     */
    public ExLocation(Location location){
        this(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Creates a Location using a vector.
     */
    public ExLocation(@Nullable World world, Vector vector){
        this(world, vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     *  Creates a unique String which is used by HashMaps to find chestGuis and game instance by Location.
     */
    public static String getUniqueString(Location location){
        assert location.getWorld() != null;
        return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
    }

    /**
     * Align Location to x, y and z (Floors double coordinates).
     */
    public void align(){
        this.setX(this.getBlockX());
        this.setY(this.getBlockY());
        this.setZ(this.getBlockZ());
    }
}
