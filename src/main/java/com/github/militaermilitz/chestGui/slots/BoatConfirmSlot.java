package com.github.militaermilitz.chestGui.slots;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.engine.ItemGameBoatStack;
import com.github.militaermilitz.battleship.engine.player.HumanGamePlayer;
import com.github.militaermilitz.chestGui.ChestGuiSlot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Alexander Ley
 * @version 1.1
 * This class Overrides the onAction Method of ChestGuiSlot to confirm boat positions.
 */
public class BoatConfirmSlot  extends ChestGuiSlot {
    /**
     * @param stack Stack of the Slot
     * @param position Position in Container
     * @param name Name of the ItemStack
     */
    public BoatConfirmSlot(ItemStack stack, int position, @Nullable String name) {
        super(stack, position, name);
    }

    @Override
    public void onAction(Player player, Location location) {
        //If not all boats are placed.
        if (ItemGameBoatStack.isGBinInv(player.getInventory())){
            player.sendMessage(ChatColor.RED + "Cannot confirm because not all boats are placed.");
            return;
        }

        final BattleshipGame game = Objects.requireNonNull(parseChestGui(location)).parseGame();
        assert game != null;

        final HumanGamePlayer gamePlayer = game.getPlayingPlayer(player);

        if (gamePlayer == null){
            player.sendMessage(ChatColor.RED + "You are not playing a game. Game have to be stopped.");
            game.stop();
            return;
        }

        gamePlayer.setReady(true);
        game.isolatePlayer(gamePlayer);
    }
}
