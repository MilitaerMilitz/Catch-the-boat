package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This class handles all action to build the stage.
 *
 */
public class BattleshipGameBuilder {

    private final BattleshipGame battleshipGame;


    public static final Vector DIMENSIONS_SMALL = new Vector(9, 9, 28);
    public static final Vector DIMENSIONS_BIG = new Vector(12, 15, 43);

    public BattleshipGameBuilder(BattleshipGame battleshipGame) {
        this.battleshipGame = battleshipGame;
    }

    /**
     * Highlights relative to direction the size of the game.
     * @param isSmall Define if the game is in small or big version.
     * @param location Location of the game.
     */
    public static void highlight(boolean isSmall, Location location, Direction direction){
        new Thread(new HighlightRunnable(isSmall, location, direction)).start();
    }

    /**
     * @return Returns if dimensions independent of @param isSmall
     */
    public static Vector getDimensions(boolean isSmall){
        return isSmall ? DIMENSIONS_SMALL : DIMENSIONS_BIG;
    }

    /**
     * @return Returns an instance of battleshipGame
     */
    public BattleshipGame getBattleshipGame() {
        return battleshipGame;
    }

    /**
     * @return Returns Dimensions independent of the game instance.
     */
    public Vector getDimensions(){
        return getDimensions(battleshipGame.isSmall());
    }

    /**
     * Builds a new stage.
     * @throws NotEnoughSpaceException If there is not enough space for creating.
     */
    public void buildGame() throws NotEnoughSpaceException {
        final Location location = battleshipGame.getLocation();
        if (!haveEnoughSpace()) throw new NotEnoughSpaceException("Here ist not enough space to build the game.", location);
    }

    /**
     * Help Method for build game.
     * @return Returns if there is enough space for creating a new stage.
     */
    private boolean haveEnoughSpace(){
        final Location location = battleshipGame.getLocation();

        return false;
    }
}
