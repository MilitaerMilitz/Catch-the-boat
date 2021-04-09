package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.Structure;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexander Ley
 * @version 1.3
 * This class handles all action to build and destroy the stage.
 */
public class BattleshipGameBuilder {

    private final BattleshipGame game;

    //Blocks which are ignored when load structure
    public static final  List<Material> IGNORED_BLOCKS = Arrays.stream(Material.values())
            .filter(material -> {
                final String name = material.name();
                return name.contains("ORE") || name.contains("DIRT")  || material == Material.STONE  || material == Material.GRASS_BLOCK || material == Material.PODZOL ||
                        material == Material.SAND || material == Material.SANDSTONE || material == Material.RED_SAND || material == Material.NETHERRACK ||
                        material == Material.END_STONE || material == Material.CLAY || material == Material.ANDESITE || material == Material.GRANITE ||
                        material == Material.DIORITE || material == Material.GRAVEL;
            })
            .collect(Collectors.toList()
    );

    public BattleshipGameBuilder(BattleshipGame battleshipGame) {
        this.game = battleshipGame;
    }

    /**
     * Highlights relative to direction the size of the game.
     * @param location Location of the game.
     * @param dimensions Highlighting Area
     */
    public static void highlight(Location location, Direction direction, Vector dimensions){
        new Thread(new HighlightRunnable(location, direction, dimensions)).start();
    }

    BattleshipGame getGame() {
        return game;
    }

    /**
     * Builds a new stage.
     * @throws NotEnoughSpaceException If there is not enough space for creating.
     */
    void buildGame(@Nullable Player player, @Nullable Consumer<Object> consumer) throws NotEnoughSpaceException {
        if (!haveEnoughSpace(player)) throw new NotEnoughSpaceException("Here ist not enough space to build the game.", game.getLoc());

        loadStructure(game.getStageType().getStructurePreset(), consumer);
    }

    /**
     * Loads a structure from Preset.
     * @param presets Structure Preset.
     * @param consumer Task which have to be done directly after loading.
     */
    void loadStructure(Structure.Presets presets, @Nullable Consumer<Object> consumer){
        presets.getStructure().loadStructure(game.getPlugin(), game.getLoc(), game.getDir(), consumer);
    }

    /**
     * Destroys the stage and replace all block with air.
     */
    void destroyGame(){
        final Location loc = game.getLoc();
        final Location goalLoc = game.getGoalLoc();
        final Direction dir = game.getDir();
        final World world = loc.getWorld();
        assert world != null;

        for (double x = loc.getBlockX(); dir.getRelXTestPredicate(goalLoc).test(x); x = dir.incrementInRelX().apply(x)){
            for (double y = loc.getBlockY(); dir.getRelYTestPredicate(goalLoc).test(y); y = dir.incrementInRelY().apply(y)){
                for (double z = loc.getBlockZ(); dir.getRelZTestPredicate(goalLoc).test(z); z = dir.incrementInRelZ().apply(z)){
                    final Block block = world.getBlockAt((int)x, (int)y, (int)z);

                    //Container drop items fix
                    if (block.getState() instanceof Container){
                        Container container = (Container) block.getState();
                        container.getInventory().clear();
                    }

                    block.setType(Material.AIR);
                }
            }
        }

        game.getMenu().deleteGuis();
    }

    /**
     * Help Method for build game.
     * @return Returns if there is enough space for creating a new stage.
     */
    private boolean haveEnoughSpace(@Nullable Player player){
        final Location loc = game.getLoc();
        final Location goalLoc = game.getGoalLoc();
        final Direction dir = game.getDir();
        final World world = loc.getWorld();
        assert world != null;

        boolean flag = true;
        int counter = 0;

        for (double x = loc.getBlockX(); dir.getRelXTestPredicate(goalLoc).test(x); x = dir.incrementInRelX().apply(x)){
            for (double y = loc.getBlockY(); dir.getRelYTestPredicate(goalLoc).test(y); y = dir.incrementInRelY().apply(y)){
                for (double z = loc.getBlockZ(); dir.getRelZTestPredicate(goalLoc).test(z); z = dir.incrementInRelZ().apply(z)){
                    final Block block = world.getBlockAt((int)x, (int)y, (int)z);
                    final Material material = block.getType();

                    if (IGNORED_BLOCKS.contains(material)) continue;
                    if (!block.isPassable()) {
                        //Show blocking Blocks in Chat
                        final Location blockLocation = block.getLocation();

                        if (player != null) {
                            player.sendMessage(ChatColor.DARK_RED + block.getType().getKey().getKey() +
                                                       " at (" + blockLocation.getBlockX() + ", " + blockLocation.getBlockY() + ", " + blockLocation.getBlockZ() + ") is blocking.");
                        }

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
