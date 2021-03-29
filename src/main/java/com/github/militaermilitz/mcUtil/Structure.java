package com.github.militaermilitz.mcUtil;

import com.github.militaermilitz.util.Tuple;
import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This Class takes all information needed for an Structure.
 *
 */
public class Structure {

    //Structures can contain multiple individual structures.
    private final List<Path> structures = new ArrayList<>();
    private final List<Vector> offsets = new ArrayList<>();

    /**
     * @param pathOffsetTuple Tuple of path containing the Path (java.nio) Object and an offset Vector.
     */
    @SafeVarargs
    public Structure(@NotNull Tuple<Path, Vector>... pathOffsetTuple){
        Arrays.stream(pathOffsetTuple).forEach(pathVectorTuple -> {
            //Test if Structure File exists.
            if (!Files.exists(pathVectorTuple.getKey())) throw new IllegalArgumentException(pathVectorTuple.getKey() + " does not exists.");

            structures.add(pathVectorTuple.getKey());
            offsets.add(pathVectorTuple.getValue());
        });

        //Every structure haves an offset vector.
        assert structures.size() == offsets.size();
    }

    /**
     * Loads Structure into world using StructureBlockLib.
     */
    public void loadStructure(Plugin plugin, Location location, Direction direction){
        for (int i = 0; i < structures.size(); i++) {
            final Path path = structures.get(i);
            final Vector offset = offsets.get(i);

            location.add(direction.getRelVecX().multiply(offset.getBlockX()));
            location.add(direction.getRelVecY().multiply(offset.getBlockY()));
            location.add(direction.getRelVecZ().multiply(offset.getBlockZ()));

            StructureBlockLibApi.INSTANCE
                    .loadStructure(plugin)
                    .at(location)
                    .rotation(direction.getStructureBlockRotation())
                    .loadFromPath(path)
                    .onException(e -> plugin.getLogger().log(Level.SEVERE, "Failed to load structure.", e));
        }
    }
}
