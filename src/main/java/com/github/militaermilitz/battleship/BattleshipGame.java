package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.StageType;
import com.github.militaermilitz.util.Tickable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author Alexander Ley
 * @version 1.1
 *
 * This Class handles all action around the game and includes the game loop.
 *
 */
public class BattleshipGame extends Tickable {

    //All information a battleship game needs
    private final StageType stageType;
    private final Location location;
    private final Location goalLocation;
    private final Direction direction;

    private final Plugin plugin;

    /**
     * This class creates a new game und tries to build the stage.
     * @param stageType Defines if game is in small or big version.
     * @param location Defines the location of the game.
     * @throws NotEnoughSpaceException if game cannot build.
     */
    public BattleshipGame(@NotNull StageType stageType, @NotNull Location location, @NotNull Player player, @NotNull Plugin plugin) throws NotEnoughSpaceException, URISyntaxException {
        this.stageType = stageType;
        this.plugin = plugin;

        final World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("Location is not suitable: " + location);

        this.location = location.subtract(new Vector(0, 3, 0));

        this.direction = Direction.getFromLocation(location);

        //Calculate second corner. (Location is first corner)
        goalLocation = new Location(this.location.getWorld(), this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());
        goalLocation.add(direction.getRelVecX().multiply(stageType.getDimensions().getBlockX()));
        goalLocation.add(direction.getRelVecY().multiply(stageType.getDimensions().getBlockY()));
        goalLocation.add(direction.getRelVecZ().multiply(stageType.getDimensions().getBlockZ()));

        //Build Stage
        BattleshipGameBuilder gameBuilder = new BattleshipGameBuilder(this);
        gameBuilder.buildGame(player);
    }

    /**
     * Game Loop
     */
    @Override
    public void tick() {

    }

    StageType getStageType() {
        return stageType;
    }

    Location getLocation() {
        return location;
    }

    Direction getDirection() {
        return direction;
    }

    Location getGoalLocation() {
        return goalLocation;
    }

    Plugin getPlugin() {
        return plugin;
    }
}
