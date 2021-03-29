package com.github.militaermilitz.mcUtil;

import com.github.militaermilitz.util.Tuple;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This enum handles the different types of the stage.
 *
 */
public enum StageType {
    BIG (new Structure(new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_big_1.nbt"), new Vector(0, 0, 0)),
                            new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_big_2.nbt"), new Vector(0, 0, 22))),
          new Vector(12, 15, 43)
    ),

    SMALL (new Structure(new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_small.nbt"), new Vector(0, 0, 0))),
              new Vector(9, 9, 28)
    );

    private final Structure structure;
    private final Vector dimensions;

    /**
     * Every StageType have a structure and a vector defining how big the structure is.
     */
    StageType(Structure structure, Vector dimensions){
        this.structure = structure;
        this.dimensions = dimensions;
    }

    /**
     * Parse StageType from String.
     * @return Returns null if type is not a StageType.
     */
    public static @Nullable StageType getFromString(String type){
        return  (type.equalsIgnoreCase("small")) ? SMALL :
                (type.equalsIgnoreCase("big")) ? BIG : null;
    }

    public Vector getDimensions(){
        return dimensions;
    }

    public Structure getStructure() {
        return structure;
    }
}
