package com.github.militaermilitz.battleship.engine;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Class defines how a game boat ItemStack should look like.
 */
public class ItemGameBoatStack extends ItemStack {

    /**
     * @param length Length of the boat.
     */
    public ItemGameBoatStack(int length) {
        super(Material.OAK_BOAT);
        if (length <= 0) throw new IllegalArgumentException("Length cannot be negative");

        final String name = getNameFromLength(length);

        final NBTItem nbtItem = new NBTItem(this, true);
        //Saves Length and belonging to CatchTheBoat
        nbtItem.setBoolean("CatchTheBoat", true);
        nbtItem.setInteger("Length", length);

        //Custom Item Name
        nbtItem.addCompound("display");
        nbtItem.getCompound("display").setString("Name", "{\"text\":\"" + ChatColor.GREEN + name + ", length: " + ChatColor.GOLD + length +"\"}");
    }

    /**
     * @return Returns Name based on length.
     */
    private String getNameFromLength(int length){
        switch (length){
            case 5: return "Battleship";
            case 4: return "Cruiser";
            case 3: return "Destroyer";
            case 2: return "Submarine";
            default: return "Game Boat";
        }
    }

    /**
     * @return Returns if @param stack is game boat.
     */
    public static boolean isGameBoat(@Nullable ItemStack stack){
        if (stack == null || stack.getType() == Material.AIR) return false;

        final NBTItem nbtItem = new NBTItem(stack, true);
        final Boolean bool = nbtItem.getBoolean("CatchTheBoat");
        return bool != null && bool;
    }

    /**
     * @return Returns length of the game Boat and null if it is not a game Boat.
     */
    public static @Nullable Integer getLength(@Nullable ItemStack stack){
        if (!isGameBoat(stack)) return null;

        final NBTItem nbtItem = new NBTItem(stack, true);
        return nbtItem.getInteger("Length");
    }

    /**
     * @return Returns true if a game boat is in inventory.
     */
    public static boolean isGBinInv(Inventory inventory){
        for (ItemStack stack : inventory) {
            if (isGameBoat(stack)) return true;
        }
        return false;
    }
}
