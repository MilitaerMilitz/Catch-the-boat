package com.github.militaermilitz.chestGui.slots;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.engine.player.HumanGamePlayer;
import com.github.militaermilitz.chestGui.ChestGuiSlot;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Alexander Ley
 * @version 1.0
 * This class Overrides the onAction Method of ChestGuiSlot to clear the game area.
 */
public class StageClearSlot extends ChestGuiSlot {

    /**
     * @param stack Stack of the Slot
     * @param position Position in Container
     * @param name Name of the ItemStack
     */
    public StageClearSlot(ItemStack stack, int position, @Nullable String name) {
        super(stack, position, name);
    }

    @Override
    public void onAction(Player player, Location location) {
        final BattleshipGame game = Objects.requireNonNull(parseChestGui(location)).parseGame();

        game.getPlayers().forEach(basicGamePlayer -> {
            if (basicGamePlayer instanceof HumanGamePlayer && ((HumanGamePlayer) basicGamePlayer).equalsPlayer(player)){
                basicGamePlayer.clear();
                player.getInventory().addItem(game.getStageType().getBoatInventory());
                player.setGameMode(GameMode.ADVENTURE);
            }
        });
    }
}
