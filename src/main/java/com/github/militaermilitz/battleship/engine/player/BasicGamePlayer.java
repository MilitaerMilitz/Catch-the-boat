package com.github.militaermilitz.battleship.engine.player;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.engine.area.EnemyGameArea;
import com.github.militaermilitz.battleship.engine.area.OwnGameArea;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Class handles all basic operations for all game players.
 */
public abstract class BasicGamePlayer {
    protected final EnemyGameArea enGameAr;
    protected final OwnGameArea ownGameAr;
    protected final BattleshipGame game;
    protected final boolean isFront;

    /**
     * @param game The game the player is playing in.
     */
    public BasicGamePlayer(@NotNull BattleshipGame game, @NotNull EnemyGameArea enGameAr, @NotNull OwnGameArea ownGameAr, boolean isFront) {
        this.enGameAr = enGameAr;
        this.ownGameAr = ownGameAr;
        this.game = game;
        this.isFront = isFront;
    }

    public EnemyGameArea getEnGameAr() {
        return enGameAr;
    }

    public OwnGameArea getOwnGameAr() {
        return ownGameAr;
    }

    protected BattleshipGame getGame() {
        return game;
    }

    public boolean isFront() {
        return isFront;
    }

    /**
     * Tries to perform a boat placing.
     * @return Returns if it was successful.
     */
    public abstract boolean placeBoat();

    /**
     * Renders all areas.
     */
    public void renderAreas(){
        enGameAr.render(game.getPlugin(), null);
        ownGameAr.render(game.getPlugin(), null);
    }

    /**
     * Clears all areas.
     */
    public void clear(){
        enGameAr.clear(game.getPlugin());
        ownGameAr.clear(game.getPlugin());
    }
}
