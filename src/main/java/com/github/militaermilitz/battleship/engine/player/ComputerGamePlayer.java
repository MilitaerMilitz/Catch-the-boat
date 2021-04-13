package com.github.militaermilitz.battleship.engine.player;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.engine.area.EnemyGameArea;
import com.github.militaermilitz.battleship.engine.area.OwnGameArea;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Ley
 * @version 0.0
 * This Class is not implemented yet.
 *
 * This Class handles all basic operations for all dummy computer players.
 */
public class ComputerGamePlayer extends BasicGamePlayer{

    /**
     * @param game The game the player is playing in.
     */
    public ComputerGamePlayer(@NotNull BattleshipGame game, @NotNull EnemyGameArea enGameAr, @NotNull OwnGameArea ownGameAr, boolean isFront) {
        super(game, enGameAr, ownGameAr, isFront);
    }

    /**
     * Clears all areas.
     */
    @Override
    public void clear() {

    }

    /**
     * Tries to perform a boat placing.
     * @return Returns if it was successful.
     */
    @Override
    public boolean placeBoat() {
        return false;
    }


    /**
     * Shoot enemy boats.
     * @return Returns if field is "shootable".
     */
    @Override
    public boolean shootEnemyBoats() {
        return false;
    }
}
