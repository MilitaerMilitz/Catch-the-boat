package com.github.militaermilitz.chestGui.slots;

import com.github.militaermilitz.chestGui.ChestGuiSlot;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Alexander Ley
 * @version 1.0
 * This class Overrides the onAction Method of ChestGuiSlot to exit the game.
 */
public class GameExitSlot extends ChestGuiSlot {

    /**
     * @param stack Stack of the Slot
     * @param position Position in Container
     * @param name Name of the ItemStack
     */
    public GameExitSlot(ItemStack stack, int position, @Nullable String name) {
        super(stack, position, name);
    }

    @Override
    public void onAction(Player player, Location location) {
        Objects.requireNonNull(parseChestGui(location)).parseGame().stop();
    }
}
