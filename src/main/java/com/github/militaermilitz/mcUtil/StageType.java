package com.github.militaermilitz.mcUtil;

import com.github.militaermilitz.battleship.engine.ItemGameBoatStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Alexander Ley
 * @version 1.2
 * This enum handles the different types of the stage.
 */
public enum StageType {
    BIG (Structure.Presets.STAGE_BIG,
          new Vector(12, 16, 43),
          new Vector(4, 6, 1), new Vector(7, 6, 41),
         new ItemGameBoatStack(5), new ItemGameBoatStack(4), new ItemGameBoatStack(4),
         new ItemGameBoatStack(3), new ItemGameBoatStack(3), new ItemGameBoatStack(3),
         new ItemGameBoatStack(2), new ItemGameBoatStack(2),
         new ItemGameBoatStack(2), new ItemGameBoatStack(2)
    ),

    SMALL (Structure.Presets.STAGE_SMALL,
           new Vector(9, 10, 31),
           new Vector(4, 6, 1), new Vector(4, 6, 29),
           new ItemGameBoatStack(3), new ItemGameBoatStack(2), new ItemGameBoatStack(2)
    );

    private final Structure.Presets structurePreset;
    private final Vector dimensions;

    //Direction Vectors pointing from Game origin to front/back Gui
    private final Vector frontGuiPos;
    private final Vector backGuiPos;

    private final ItemStack[] boatInventory;

    /**
     * Every StageType have a structure and a vector defining how big the structure is.
     */
    StageType(Structure.Presets presets, Vector dimensions, Vector frontGuiPos, Vector backGuiPos, @NotNull ItemStack... boatInventory){
        this.structurePreset = presets;
        this.dimensions = dimensions;
        this.frontGuiPos = frontGuiPos;
        this.backGuiPos = backGuiPos;
        this.boatInventory = boatInventory;
    }

    /**
     * Parse StageType from String.
     * @return Returns null if type is not a StageType.
     */
    public static @Nullable StageType getFromString(String type){
        return  (type.equalsIgnoreCase("small")) ? SMALL :
                (type.equalsIgnoreCase("big")) ? BIG : null;
    }

    //Getter
    public Vector getDimensions(){
        return new Vector(dimensions.getBlockX(), dimensions.getBlockY(), dimensions.getBlockZ());
    }

    public Structure.Presets getStructurePreset() {
        return structurePreset;
    }

    public Vector getFrontGuiPos() {
        return frontGuiPos;
    }

    public Vector getBackGuiPos() {
        return backGuiPos;
    }

    public ItemStack[] getBoatInventory() {
        return boatInventory.clone();
    }
}
