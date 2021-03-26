package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BattleshipGameBuilder {

    private final BattleshipGame battleshipGame;

    public static final Vector DIMENSIONS_SMALL = new Vector(9, 9, 28);
    public static final Vector DIMENSIONS_BIG = new Vector(12, 15, 43);

    BattleshipGameBuilder(BattleshipGame battleshipGame) {
        this.battleshipGame = battleshipGame;
    }

    public static void highlight(boolean isSmall, Location location, Direction direction){
        new Thread(new HighlightRunnable(isSmall, location, direction)).start();
    }

    public static Vector getDimensions(boolean isSmall){
        return isSmall ? DIMENSIONS_SMALL : DIMENSIONS_BIG;
    }

    public Vector getDimensions(){
        return getDimensions(battleshipGame.isSmall());
    }

    BattleshipGame getBattleshipGame() {
        return battleshipGame;
    }

    boolean haveEnoughSpace(Location location){
        return false;
    }

    void buildGame(Location location) throws NotEnoughSpaceException {
        if (!haveEnoughSpace(location)) throw new NotEnoughSpaceException("Here ist not enough space to build the game.", location);
    }
}
