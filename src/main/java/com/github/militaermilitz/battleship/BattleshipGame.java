package com.github.militaermilitz.battleship;

import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.ExLocation;
import com.github.militaermilitz.mcUtil.StageType;
import com.github.militaermilitz.util.Tickable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * @author Alexander Ley
 * @version 1.3
 * This Class handles all action around the game and includes the game loop.
 */
public class BattleshipGame extends Tickable {

    /**
     * All Games Instances
     */
    public static final HashMap<String, BattleshipGame> GAMES = new HashMap<>();

    //All information a battleship game needs
    private final StageType stageType;
    private final ExLocation location;
    private final ExLocation goalLocation;
    private final Direction direction;

    private final Plugin plugin;
    private final BattleshipMenu menu;
    private final BattleshipGameBuilder builder;

    /**
     * Construct a new game.
     * @param player Player for messages.
     * @param location Origin Location
     * @param plugin Needed for loading the structure.
     * @param buildStage Boolean if stage have to build or not.
     * @throws NotEnoughSpaceException If not enough space.
     */
    BattleshipGame(@NotNull StageType stageType, @NotNull Location location, @Nullable Player player, @NotNull Plugin plugin, boolean buildStage) throws NotEnoughSpaceException {
        this.stageType = stageType;
        this.plugin = plugin;

        final World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("Location is not suitable: " + location);

        this.location = new ExLocation(location);
        if (buildStage) this.location.subtract(new Vector(0, 3, 0));
        this.location.align();

        this.direction = Direction.getFromLocation(location);

        //Calculate second corner. (Location is first corner)
        goalLocation = new ExLocation(this.location);
        goalLocation.add(direction.getRelVecX().multiply(stageType.getDimensions().getBlockX()));
        goalLocation.add(direction.getRelVecY().multiply(stageType.getDimensions().getBlockY()));
        goalLocation.add(direction.getRelVecZ().multiply(stageType.getDimensions().getBlockZ()));

        this.menu = new BattleshipMenu(this, true);

        //Build Stage
        BattleshipGameBuilder gameBuilder = new BattleshipGameBuilder(this);
        if (buildStage) gameBuilder.buildGame(player, o -> menu.initialize(this));
        else menu.initialize(this);

        this.builder = gameBuilder;

        GAMES.put(ExLocation.getUniqueString(this.location), this);

        System.out.println(this.goalLocation);
    }

    /**
     * This class creates a new game und tries to build the stage.
     * @param stageType Defines if game is in small or big version.
     * @param location Origin Location
     * @throws NotEnoughSpaceException if game cannot build.
     */
    public BattleshipGame(@NotNull StageType stageType, @NotNull Location location, @Nullable Player player, @NotNull Plugin plugin) throws NotEnoughSpaceException, URISyntaxException {
        this(stageType, location, player, plugin, true);
    }

    //Getter
    StageType getStageType() {
        return stageType;
    }

    public Location getLocation() {
        return new ExLocation(this.location);
    }

    Direction getDirection() {
        return direction;
    }

    Location getGoalLocation() {
        return new ExLocation(this.goalLocation);
    }

    Plugin getPlugin() {
        return plugin;
    }

    BattleshipMenu getMenu() {
        return menu;
    }


    /**
     * Starts the game.
     *
     * @param delay  Start delay.
     * @param period Timer period.
     */
    @Override
    public void start(long delay, long period) {
        //super.start(delay, period);
        System.out.println("Start Game");
    }

    /**
     * Game Loop
     */
    @Override
    public void tick() {

    }

    /**
     * Stops the Game.
     */
    @Override
    public void stop() {
        //super.stop();
        System.out.println("End Game");
    }

    /**
     * Stops the game and destroys the stage.
     */
    public void destroy(){
        this.stop();
        builder.destroyGame();
        if (BattleshipGame.GAMES.containsValue(this)) {
            BattleshipGame.GAMES.remove(ExLocation.getUniqueString(this.getLocation()));
        }
    }
}
