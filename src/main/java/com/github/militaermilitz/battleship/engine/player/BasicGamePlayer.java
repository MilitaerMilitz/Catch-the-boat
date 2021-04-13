package com.github.militaermilitz.battleship.engine.player;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.engine.area.EnemyGameArea;
import com.github.militaermilitz.battleship.engine.area.OwnGameArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * @author Alexander Ley
 * @version 1.1
 * This Class handles all basic operations for all game players.
 */
public abstract class BasicGamePlayer {
    protected final EnemyGameArea enGameAr;
    protected final OwnGameArea ownGameAr;
    protected final BattleshipGame game;
    protected final boolean isFront;
    protected boolean isReady = false;

    /**
     * @param game The game the player is playing in.
     */
    public BasicGamePlayer(@NotNull BattleshipGame game, @NotNull EnemyGameArea enGameAr, @NotNull OwnGameArea ownGameAr, boolean isFront) {
        this.enGameAr = enGameAr;
        this.ownGameAr = ownGameAr;
        this.game = game;
        this.isFront = isFront;
    }

    //Getter
    public @NotNull EnemyGameArea getEnGameAr() {
        return enGameAr;
    }

    public @NotNull OwnGameArea getOwnGameAr() {
        return ownGameAr;
    }

    protected @NotNull BattleshipGame getGame() {
        return game;
    }

    public boolean isFront() {
        return isFront;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    /**
     * Tries to perform a boat placing.
     * @return Returns if it was successful.
     */
    public abstract boolean placeBoat();

    /**
     * Shoot enemy boats.
     * @return Returns if field is "shootable".
     */
    public abstract boolean shootEnemyBoats();

    /**
     * Renders an enemy attack at position (x, y).
     */
    public void renderEnemyAttack(int x, int y){
        final Location loc = getOwnGameAr().getLocationFromCoords(x, y).add(new Vector(0.5, 4, 0.5));

        final World world = loc.getWorld();
        assert world != null;

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), ""
                + "summon fireball " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " "
                + "{NoGravity:1b,ExplosionPower:0,life:0,direction:[0.0,-0.25,0.0],power:[0.0,-0.25,0.0],Item:{id:\"minecraft:dragon_egg\",Count:1b}}");

    }

    /**
     * Renders all areas.
     */
    public void renderAreas(){
        enGameAr.render(null);
        ownGameAr.render(null);
    }

    /**
     * Clears all areas.
     */
    public void clear(){
        enGameAr.clear();
        ownGameAr.clear();
    }

    /**
     * @return Returns if a player has won.
     */
    public boolean hasWon(){
        return Objects.requireNonNull(game.getEnemy(this)).ownGameAr.isAreaEmpty();
    }
}
