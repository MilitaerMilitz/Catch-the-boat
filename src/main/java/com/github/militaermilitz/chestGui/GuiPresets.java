package com.github.militaermilitz.chestGui;

import com.github.militaermilitz.chestGui.slots.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 * @author Alexander Ley
 * @version 1.1
 * This class contains presets for gui using by ChestGui Constructor.
 */
public enum GuiPresets {
    START_GUI(
            "" + ChatColor.GOLD + ChatColor.BOLD + "Battleship Game Menu",
            new StartSlot(new ItemStack(Material.OAK_BOAT), 12, ChatColor.GREEN + "Start game"),
            new DestroySlot(new ItemStack(Material.BARRIER), 14, ChatColor.RED + "Remove Stage")
    ),
    BOAT_CONFIRM_GUI(
            "" + ChatColor.GOLD + ChatColor.BOLD + "Battleship Game Menu",
            new BoatConfirmSlot(new ItemStack(Material.GREEN_CONCRETE), 12, ChatColor.GREEN + "Confirm Boat positions"),
            new StageClearSlot(new ItemStack(Material.OAK_BOAT), 13, ChatColor.GOLD + "Clear Stage"),
            new GameExitSlot(new ItemStack(Material.BARRIER), 14, ChatColor.RED + "Exit game")
    );

    private final String name;
    private final ChestGuiSlot[] slots;

    /**
     * @param name Name of the Gui which is displayed in container..
     * @param slots List of slots.
     */
    GuiPresets(String name, ChestGuiSlot... slots){
        this.name = name;
        this.slots = slots;
    }

    //Getter
    String getName() {
        return name;
    }
    ChestGuiSlot[] getSlots() {
        return slots;
    }
}
