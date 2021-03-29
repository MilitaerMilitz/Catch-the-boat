package com.github.militaermilitz.battleship;

import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.util.Tickable;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This class takes the highlighting task to handle it on a different Thread.
 *
 */
public class HighlightRunnable extends Tickable implements Runnable{

    //All Information needed for highlighting
    private final Location location;
    private final Location goalLocation;
    private final Direction direction;

    //Counter
    private int i = 0;

    /**
     *  Creates new highlighting task.
     */
    public HighlightRunnable(Location location, Direction direction, Vector dimensions) {
        this.location = location;
        this.direction = direction;

        //Calculates goalLocation relative to direction.
        goalLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        goalLocation.add(direction.getRelVecX().multiply(dimensions.getBlockX()));
        goalLocation.add(direction.getRelVecY().multiply(dimensions.getBlockY()));
        goalLocation.add(direction.getRelVecZ().multiply(dimensions.getBlockZ()));

        System.out.println(goalLocation);
    }

    /**
     *  Creates new highlighting task.
     */
    public HighlightRunnable(boolean isSmall, Location location, Location goalLocation, Direction direction) {
        this.location = location;
        this.direction = direction;
        this.goalLocation = goalLocation;
    }

    /**
     * Starts the highlight Timer Task.
     */
    @Override
    public void run() {
        start(10, 500);
    }

    /**
     * Highlight Task
     */
    @Override
    public void tick() {
        final World world = location.getWorld();
        assert world != null;

        //Relative for loop -> Particle spawn
        for (double x = location.getBlockX(); direction.getRelXTestPredicate(goalLocation).test(x); x = direction.incrementInRelX().apply(x)){
            for (double y = location.getBlockY(); direction.getRelYTestPredicate(goalLocation).test(y); y = direction.incrementInRelY().apply(y)){
                for (double z = location.getBlockZ(); direction.getRelZTestPredicate(goalLocation).test(z); z = direction.incrementInRelZ().apply(z)){
                    world.spawnParticle(Particle.CLOUD, x + 0.5, y + 0.5, z + 0.5, 1, 0, 0,0 , 0);
                }
            }
        }
        i++;

        //Stops Highlight Task
        if (i > 20) stop();
    }
}
