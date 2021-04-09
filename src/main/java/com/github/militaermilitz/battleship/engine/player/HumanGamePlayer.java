package com.github.militaermilitz.battleship.engine.player;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.engine.ItemGameBoatStack;
import com.github.militaermilitz.battleship.engine.area.EnemyGameArea;
import com.github.militaermilitz.battleship.engine.area.OwnGameArea;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.util.HomogenTuple;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Ley
 * @version 1.0
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

    public Player getPlayer() {
        return player;
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

        final HomogenTuple<Integer> coords = ownGameAr.getCoordFromPlayer(player);
        if (coords == null) return false;

        return ownGameAr.placeBoat(length, coords.getKey(), coords.getValue(), Direction.getFromPlayer(player), isFront);
    }

    /**
     * Tests if the @param player quals the playing player.
     */
    public boolean equalsPlayer(Player player){
        return player.getName().equals(this.player.getName());
    }
}
