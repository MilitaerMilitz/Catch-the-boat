package com.github.militaermilitz.mcUtil;

import com.github.militaermilitz.CatchTheBoat;
import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.BattleshipMenu;
import com.github.militaermilitz.util.Tuple;
import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;
import com.github.shynixn.structureblocklib.api.bukkit.entity.StructureLoader;
import com.github.shynixn.structureblocklib.api.entity.ProgressToken;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.HTMLDocument;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alexander Ley
 * @version 1.1
 * This Class takes all information needed for an Structure..
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
    public void loadStructure(@NotNull Plugin plugin, @NotNull Location location, @NotNull Direction direction, @Nullable Consumer<Object> consumer) {
        final Location zeroPoint = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        List<ProgressToken<?>> tokens = new ArrayList<>();

        for (int i = 0; i < structures.size(); i++) {
            final Path path = structures.get(i);
            final Vector offset = offsets.get(i);

            zeroPoint.add(direction.getRelVecX().multiply(offset.getBlockX()));
            zeroPoint.add(direction.getRelVecY().multiply(offset.getBlockY()));
            zeroPoint.add(direction.getRelVecZ().multiply(offset.getBlockZ()));


            tokens.add(StructureBlockLibApi.INSTANCE
                    .loadStructure(plugin)
                    .at(zeroPoint)
                    .rotation(direction.getStructureBlockRotation())
                    .loadFromPath(path)
                    .onException(e -> plugin.getLogger().log(Level.SEVERE, "Failed to load structure.", e)));

        }

        //Guaranties that consumer will be executed only if structure is fully loaded.
        for (int i = 1; i < tokens.size() && consumer != null; i++){
            tokens.get(i - 1).getCompletionStage().runAfterBoth(tokens.get(i).getCompletionStage(), () -> {}).whenComplete((unused, throwable) -> consumer.accept(null));
        }

        if (tokens.size() == 1 && consumer != null) tokens.get(0).getCompletionStage().whenComplete((unused, throwable) -> consumer.accept(null));
    }

    public enum Presets{
        STAGE_BIG(new Structure(
                new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_big_1.nbt"), new Vector(0, 0, 0)),
                new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_big_2.nbt"), new Vector(0, 0, 22)))
        ),
        STAGE_SMALL(new Structure(new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_small.nbt"), new Vector(0, 0, 0)))),
        STAGE_BIG_INGAME(new Structure(
                new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_big_1_ingame.nbt"), new Vector(0, 0, 0)),
                new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_big_2_ingame.nbt"), new Vector(0, 0, 22)))
        ),
        STAGE_SMALL_INGAME(new Structure(new Tuple<>(Paths.get("plugins/CatchTheBoat/structures/battleship_small_ingame.nbt"), new Vector(0, 0, 0))));

        private final Structure structure;

        Presets (Structure structure){
            this.structure = structure;
        }

        public Structure getStructure() {
            return structure;
        }
    }
}
