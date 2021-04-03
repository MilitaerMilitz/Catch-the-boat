package com.github.militaermilitz.mcUtil;

import com.github.militaermilitz.util.Tuple;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author Alexander Ley
 * @version 1.1
 * This enum handles the different types of the stage.
 */
public enum StageType {
    BIG (new Structure(new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_big_1.nbt"), new Vector(0, 0, 0)),
                            new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_big_2.nbt"), new Vector(0, 0, 22))),
          new Vector(12, 15, 43),
          new Vector(4, 6, 1), new Vector(7, 6, 41)
    ),

    SMALL (new Structure(new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_small.nbt"), new Vector(0, 0, 0))),
              new Vector(9, 9, 31),
              new Vector(4, 6, 1), new Vector(4, 6, 29)
    );

    private final Structure structure;
    private final Vector dimensions;

    //Direction Vectors pointing from Game origin to front/back Gui
    private final Vector frontGuiPos;
    private final Vector backGuiPos;

    /**
     * Every StageType have a structure and a vector defining how big the structure is.
     */
    StageType(Structure structure, Vector dimensions, Vector frontGuiPos, Vector backGuiPos){
        this.structure = structure;
        this.dimensions = dimensions;
        this.frontGuiPos = frontGuiPos;
        this.backGuiPos = backGuiPos;
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
    public Structure getStructure() {
        return structure;
    }
    public Vector getFrontGuiPos() {
        return frontGuiPos;
    }
    public Vector getBackGuiPos() {
        return backGuiPos;
    }
}
