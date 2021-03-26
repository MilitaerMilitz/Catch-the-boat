package com.github.militaermilitz.battleship;

import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.util.Tickable;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class HighlightRunnable extends Tickable implements Runnable{

    private final Location location;
    private final Location goalLocation;
    private final Direction direction;

    private int i = 0;

    public HighlightRunnable(boolean isSmall, Location location, Direction direction) {
        this.location = location;
        this.direction = direction;

        goalLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        goalLocation.add(direction.getRelVecX().multiply(BattleshipGameBuilder.getDimensions(isSmall).getBlockX()));
        goalLocation.add(direction.getRelVecY().multiply(BattleshipGameBuilder.getDimensions(isSmall).getBlockY()));
        goalLocation.add(direction.getRelVecZ().multiply(BattleshipGameBuilder.getDimensions(isSmall).getBlockZ()));

    }

    @Override
    public void run() {
        start(10, 500);
    }

    @Override
    public void tick() {
        final World world = location.getWorld();
        assert world != null;

        System.out.println(location);
        System.out.println(goalLocation);

        for (double x = location.getBlockX(); direction.getRelXTestPredicate(goalLocation).test(x); x = direction.incrementInRelX().apply(x)){
            for (double y = location.getBlockY(); direction.getRelYTestPredicate(goalLocation).test(y); y = direction.incrementInRelY().apply(y)){
                for (double z = location.getBlockZ(); direction.getRelZTestPredicate(goalLocation).test(z); z = direction.incrementInRelZ().apply(z)){
                    world.spawnParticle(Particle.CLOUD, x + 0.5, y + 0.5, z + 0.5, 1, 0, 0,0 , 0);
                }
            }
        }

        i++;
        if (i > 20) stop();
    }
}
