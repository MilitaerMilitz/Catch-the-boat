package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.util.Tickable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This Class handles all action around the game.
 *
 */
public class BattleshipGame extends Tickable {

    //All information a battleship game needs
    private final boolean isSmall;
    private final Location location;
    private final Direction direction;

    private final BattleshipGameBuilder gameBuilder;

    /**
     * This class creates a new game und tries to build the stage.
     * @param isSmall Defines if game is in small or big version.
     * @param location Defines the location of the game.
     * @throws NotEnoughSpaceException if game cannot build.
     */
    public BattleshipGame(boolean isSmall, Location location) throws NotEnoughSpaceException {
        this.isSmall = isSmall;

        final World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("Location is not suitable: " + location);

        //Filter Ocean biome
        final Biome biome = world.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        final boolean isOcean = Arrays.stream(Biome.values())
                .filter(LambdaBiome -> LambdaBiome.getKey().getKey().contains("ocean"))
                .collect(Collectors.toList())
        .contains(biome);

        //Special treatment for ocean biomes
        if (location.getBlockY() == 63 && isOcean){
            this.location = location.subtract(new Vector(0, 3, 0));
        }
        else{
            this.location = location;
        }

        this.direction = Direction.getFromLocation(location);

        //Build Stage
        this.gameBuilder = new BattleshipGameBuilder(this);
        gameBuilder.buildGame();
    }

    /**
     * Game Loop
     */
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
