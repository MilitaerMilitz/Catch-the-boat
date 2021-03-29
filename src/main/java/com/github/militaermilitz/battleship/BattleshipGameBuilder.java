package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.StageType;
import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This class handles all action to build the stage.
 *
 */
public class BattleshipGameBuilder {

    private final BattleshipGame battleshipGame;

    //Blocks which are ignored when load structure
    public static final  List<Material> IGNORED_BLOCKS = Arrays.stream(Material.values())
            .filter(material -> {
                final String name = material.name();
                return name.contains("ORE") || name.contains("DIRT")  || material == Material.STONE  || material == Material.GRASS_BLOCK || material == Material.PODZOL ||
                        material == Material.SAND || material == Material.SANDSTONE || material == Material.RED_SAND || material == Material.NETHERRACK ||
                        material == Material.END_STONE || material == Material.CLAY;
            })
            .collect(Collectors.toList()
    );


    public BattleshipGameBuilder(BattleshipGame battleshipGame) {
        this.battleshipGame = battleshipGame;
    }

    /**
     * Highlights relative to direction the size of the game.
     * @param location Location of the game.
     * @param dimensions Highlighting Area
     */
    public static void highlight(Location location, Direction direction, Vector dimensions){
        new Thread(new HighlightRunnable(location, direction, dimensions)).start();
    }

    /**
     * @return Returns an instance of battleshipGame
     */
    BattleshipGame getBattleshipGame() {
        return battleshipGame;
    }

    /**
     * Builds a new stage.
     * @throws NotEnoughSpaceException If there is not enough space for creating.
     */
    void buildGame(Player player) throws NotEnoughSpaceException {
        final Location location = battleshipGame.getLocation();
        if (!haveEnoughSpace(player)) throw new NotEnoughSpaceException("Here ist not enough space to build the game.", location);

        final Direction direction = battleshipGame.getDirection();
        final Plugin plugin = battleshipGame.getPlugin();

        battleshipGame.getStageType().getStructure().loadStructure(plugin, location, direction);
    }

    /**
     * Help Method for build game.
     * @return Returns if there is enough space for creating a new stage.
     */
    private boolean haveEnoughSpace(Player player){
        final Location location = battleshipGame.getLocation();
        final Location goalLocation = battleshipGame.getGoalLocation();
        final Direction direction = battleshipGame.getDirection();
        final World world = location.getWorld();
        assert world != null;

        boolean flag = true;
        int counter = 0;

        for (double x = location.getBlockX(); direction.getRelXTestPredicate(goalLocation).test(x); x = direction.incrementInRelX().apply(x)){
            for (double y = location.getBlockY(); direction.getRelYTestPredicate(goalLocation).test(y); y = direction.incrementInRelY().apply(y)){
                for (double z = location.getBlockZ(); direction.getRelZTestPredicate(goalLocation).test(z); z = direction.incrementInRelZ().apply(z)){
                    final Block block = world.getBlockAt((int)x, (int)y, (int)z);
                    final Material material = block.getType();

                    if (IGNORED_BLOCKS.contains(material)) continue;
                    if (!block.isPassable()) {
                        //Show blocking Blocks in Chat
                        final Location blockLocation = block.getLocation();
                        player.sendMessage(ChatColor.DARK_RED + block.getType().getKey().getKey() +
                                               " at (" + blockLocation.getBlockX() + ", " + blockLocation.getBlockY() + ", " + blockLocation.getBlockZ() + ") is blocking.");
                        flag = false;

                        //Show only max. 20 blocking Blocks
                        counter++;
                        if (counter >= 20){
                            return flag;
                        }
                    }
                }
            }
        }

        return flag;
    }
}
