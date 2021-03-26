package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;

import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.util.Tickable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BattleshipGame extends Tickable {

    private final boolean isSmall;
    private final Location location;
    private final Direction direction;

    public BattleshipGame(boolean isSmall, Location location) throws NotEnoughSpaceException{
        final World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("Location is not suitable: " + location);

        final Biome biome = world.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        final boolean isOcean = Arrays.stream(Biome.values())
                .filter(LambdaBiome -> LambdaBiome.getKey().getKey().contains("ocean"))
                .collect(Collectors.toList())
        .contains(biome);

        this.isSmall = isSmall;

        if (location.getBlockY() == 63 && isOcean){
            this.location = location.subtract(new Vector(0, 3, 0));
        }
        else{
            this.location = location;
        }
        this.direction = Direction.getFromLocation(location);
    }

    @Override
    public void tick() {

    }

    public boolean isSmall() {
        return isSmall;
    }

    public Location getLocation() {
        return location;
    }

    public Direction getDirection() {
        return direction;
    }
}
