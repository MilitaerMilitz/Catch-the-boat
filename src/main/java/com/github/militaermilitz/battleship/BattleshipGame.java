package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;

import org.bukkit.Location;
import java.rmi.registry.LocateRegistry;

public class BattleshipGame {

    private final boolean isSmall;
    private final Location location;
    private final double direction;

    public BattleshipGame(boolean isSmall, Location location) throws NotEnoughSpaceException{
        this.isSmall = isSmall;
        this.location = location;
        this.direction = location.getYaw() % 360;
    }

    public boolean haveEnoughSpace(Location location){
        return false;
    }

    public void buildGame(Location location) throws NotEnoughSpaceException {
        if (!haveEnoughSpace(location)) throw new NotEnoughSpaceException("Here ist not enough space to build the game.", location);
    }
}
