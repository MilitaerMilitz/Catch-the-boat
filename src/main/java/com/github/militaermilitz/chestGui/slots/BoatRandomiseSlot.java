package com.github.militaermilitz.chestGui.slots;

import com.github.militaermilitz.chestGui.ChestGuiSlot;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Ley
 * @version 0.0
 * This Class is not implemented yet.
 *
 * This class Overrides the onAction Method of ChestGuiSlot to randomise boat positions.
 */
public class BoatRandomiseSlot extends ChestGuiSlot {

    /**
     * @param stack Stack of the Slot
     * @param position Position in Container
     * @param name Name of the ItemStack
     */
    public BoatRandomiseSlot(ItemStack stack, int position, @Nullable String name) {
        super(stack, position, name);
    }

    @Override
    public void onAction(Player player, Location location) {

    }
}
