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
 * This class takes the highlighting task to handle it on a different Thread.
 */
public class HighlightRunnable extends Tickable implements Runnable{

    //All Information needed for highlighting
    private final Location loc;
    private final Location goalLoc;
    private final Direction dir;

    //Counter
    private int i = 0;

    /**
     *  Creates new highlighting task.
     */
    public HighlightRunnable(Location loc, Direction dir, Vector dimensions) {
        this.loc = loc;
        this.dir = dir;

        //Calculates goalLocation relative to direction.
        goalLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        goalLoc.add(dir.getRelVecX().multiply(dimensions.getBlockX()));
        goalLoc.add(dir.getRelVecY().multiply(dimensions.getBlockY()));
        goalLoc.add(dir.getRelVecZ().multiply(dimensions.getBlockZ()));
    }

    /**
     *  Creates new highlighting task.
     */
    public HighlightRunnable(boolean isSmall, Location loc, Location goalLoc, Direction dir) {
        this.loc = loc;
        this.dir = dir;
        this.goalLoc = goalLoc;
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
        final World world = loc.getWorld();
        assert world != null;

        //Relative for loop -> Particle spawn
        for (double x = loc.getBlockX(); dir.getRelXTestPredicate(goalLoc).test(x); x = dir.incrementInRelX().apply(x)){
            for (double y = loc.getBlockY(); dir.getRelYTestPredicate(goalLoc).test(y); y = dir.incrementInRelY().apply(y)){
                for (double z = loc.getBlockZ(); dir.getRelZTestPredicate(goalLoc).test(z); z = dir.incrementInRelZ().apply(z)){
                    world.spawnParticle(Particle.CLOUD, x + 0.5, y + 0.5, z + 0.5, 1, 0, 0,0 , 0);
                }
            }
        }
        i++;

        //Stops Highlight Task
        if (i > 20) stop();
    }
}
