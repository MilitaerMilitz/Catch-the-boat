package com.github.militaermilitz.chestGui;

import com.github.militaermilitz.mcUtil.ExLocation;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Ley
 * @version 1.1
 * This class represents the basic class of a ChestGuiSlot
 */
public abstract class ChestGuiSlot {

    //Slot Data
    private ItemStack stack;
    private int position;
    private final String name;

     /**
     * @param stack Stack of the Slot
     * @param position Position in Container
     * @param name Name of the ItemStack
     */
    public ChestGuiSlot(ItemStack stack, int position, @Nullable String name) {
        this.stack = stack;
        this.position = position;
        this.name = name;

        //Apply name to slot.
        if (name != null){
            final NBTItem nbtItem = new NBTItem(stack, true);
            nbtItem.addCompound("display");
            nbtItem.getCompound("display").setString("Name", "{\"text\":\"" + name +"\"}");
        }
    }

    //Getter und Setter
    ItemStack getStack() {
        return stack;
    }
    int getPosition() {
        return position;
    }
    String getName() {
        return name;
    }
    void setStack(ItemStack stack) {
        this.stack = stack;
    }
    void setPosition(int position) {
        this.position = position;
    }

    /**
     * Method activated if slot is clicked.
     */
    public abstract void onAction(Player player, Location location);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChestGuiSlot)) return false;

        ChestGuiSlot that = (ChestGuiSlot) o;

        if (position != that.position) return false;
        if (!stack.equals(that.stack)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = stack.hashCode();
        result = 31 * result + position;
        result = 31 * result + name.hashCode();
        return result;
    }

    /**
     * Parse chestGui from Slot.
     * @param location Needs Location.
     * @return Return null if no ChestGui is found.
     */
    protected @Nullable ChestGui parseChestGui(Location location){
        return ChestGui.CHEST_GUI.get(ExLocation.getUniqueString(location));
    }
}
