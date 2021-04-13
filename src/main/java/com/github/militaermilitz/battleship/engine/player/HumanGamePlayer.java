package com.github.militaermilitz.battleship.engine.player;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.engine.ItemGameBoatStack;
import com.github.militaermilitz.battleship.engine.area.EnemyGameArea;
import com.github.militaermilitz.battleship.engine.area.OwnGameArea;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.util.HomogenTuple;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Ley
 * @version 1.3
 * This Class handles all basic operations for all human players.
 */
public class HumanGamePlayer extends BasicGamePlayer{

    private final Player player;
    private final GameMode preMode;

    /**
     *
     * @param game The game the player is playing in.
     * @param player The player instance playing the game.
     * @param preMode The Gamemode the player have before playing the game to give it back later.
     */
    public HumanGamePlayer(@NotNull BattleshipGame game, @NotNull Player player,
                           @Nullable GameMode preMode, @NotNull EnemyGameArea enGameAr,
                                                @NotNull OwnGameArea ownGameAr, boolean isFront) {

        super(game, enGameAr, ownGameAr, isFront);
        this.player = player;
        this.preMode = (preMode == null) ? GameMode.SURVIVAL : preMode;
    }

    /**
     * Getter for player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player isolated -> change ownGameAr render mode.
     */
    public void setIsolated(){
        this.ownGameAr.setIsolated();
    }

    /**
     * Clears all areas and gives the player the gamemode back.
     */
    @Override
    public void clear() {
        super.clear();
        player.setGameMode(preMode);
        player.getInventory().removeItem(game.getStageType().getBoatInventory());
    }

    /**
     * Tries to perform a boat placing.
     * @return Returns if it was successful.
     */
    @Override
    public boolean placeBoat(){
        final ItemStack stack = player.getInventory().getItemInMainHand();

        final Integer length = ItemGameBoatStack.getLength(stack);
        if (length == null) return false;

        final HomogenTuple<Integer> coords = ownGameAr.getCoordsFromPlayer(player);
        if (coords == null) return false;

        return ownGameAr.placeBoat(length, coords.getKey(), coords.getValue(), Direction.getFromPlayer(player), isFront);
    }

    /**
     * Shoot enemy boats.
     * @return Returns if field is "shootable".
     */
    @Override
    public boolean shootEnemyBoats() {
        final HomogenTuple<Integer> coords = getEnGameAr().getCoordsFromPlayer(player);

        if (coords == null) return false;
        final int x = coords.getKey(), y = coords.getValue();

        final BasicGamePlayer enemy = game.getEnemy(this);
        assert enemy != null;

        final boolean hit = enemy.ownGameAr.enemyAttack(x, y);

        if (enGameAr.isAlreadyShoot(x, y)) return false;
        enemy.renderEnemyAttack(x, y);

        if (hit){
            player.sendTitle("", ChatColor.GREEN + "Hit", 20, 20, 20);
            enGameAr.setBool(x, y, true);
        }
        else{
            player.sendTitle("", ChatColor.BLUE + "Water", 20, 20, 20);
            enGameAr.setBool(x, y, false);
            game.swapMovePlayer();
        }

        return true;
    }

    /**
     * Tests if the @param player equals the playing player.
     */
    public boolean equalsPlayer(Player player){
        return player.getName().equals(this.player.getName());
    }
}
