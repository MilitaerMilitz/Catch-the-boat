package com.github.militaermilitz.battleship;

import com.github.militaermilitz.battleship.engine.area.EnemyGameArea;
import com.github.militaermilitz.battleship.engine.area.OwnGameArea;
import com.github.militaermilitz.battleship.engine.player.BasicGamePlayer;
import com.github.militaermilitz.battleship.engine.player.ComputerGamePlayer;
import com.github.militaermilitz.battleship.engine.player.HumanGamePlayer;
import com.github.militaermilitz.chestGui.GuiPresets;
import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.ExLocation;
import com.github.militaermilitz.mcUtil.StageType;
import com.github.militaermilitz.mcUtil.Structure;
import com.github.militaermilitz.util.HomogenTuple;
import com.github.militaermilitz.util.Tickable;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Alexander Ley
 * @version 1.5
 * This Class handles all action around the game and includes the game loop.
 */
public class BattleshipGame extends Tickable {

    /**
     * All Games Instances
     */
    public static final HashMap<String, BattleshipGame> GAMES = new HashMap<>();

    //All information a battleship game needs
    private final StageType stageType;
    private final ExLocation loc;
    private final ExLocation goalLoc;
    private final Direction dir;

    //All information to run.
    private final Plugin plugin;
    private final BattleshipMenu menu;
    private final BattleshipGameBuilder builder;
    private HomogenTuple<BasicGamePlayer> players = null;

    private boolean isRunning = false;

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

        this.loc = new ExLocation(location);
        if (buildStage) this.loc.subtract(new Vector(0, 3, 0));
        this.loc.align();

        this.dir = Direction.getFromLocation(location);

        //Calculate second corner. (Location is first corner)
        goalLoc = new ExLocation(this.loc);
        goalLoc.add(dir.getRelVecX().multiply(stageType.getDimensions().getBlockX()));
        goalLoc.add(dir.getRelVecY().multiply(stageType.getDimensions().getBlockY()));
        goalLoc.add(dir.getRelVecZ().multiply(stageType.getDimensions().getBlockZ()));

        this.menu = new BattleshipMenu(this, true);

        //Build Stage
        BattleshipGameBuilder gameBuilder = new BattleshipGameBuilder(this);
        if (buildStage) gameBuilder.buildGame(player, o -> menu.initialize(this));
        else menu.initialize(this);

        this.builder = gameBuilder;

        GAMES.put(ExLocation.getUniqueString(this.loc), this);

        System.out.println(this.goalLoc);
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
    public StageType getStageType() {
        return stageType;
    }

    public Location getLoc() {
        return new ExLocation(this.loc);
    }

    Direction getDir() {
        return dir;
    }

    Location getGoalLoc() {
        return new ExLocation(this.goalLoc);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    BattleshipMenu getMenu() {
        return menu;
    }

    public HomogenTuple<BasicGamePlayer> getPlayers() {
        return players;
    }

    /**
     * Starts the game.
     * @param delay  Start delay.
     * @param period Timer period.
     */
    @Override
    public void start(long delay, long period) {
        //Game cannot be started twice.
        if (isRunning) {
            destroy();
            throw new IllegalStateException("Game is already running.");
        }
        isRunning = true;

        super.start(delay, period);

        //Initialize players
        if (!calculatePlayers()) return;

        //Give players the boat Inventory.
        players.forEach(basicGamePlayer -> {
            if (basicGamePlayer instanceof HumanGamePlayer){
                final Player player = ((HumanGamePlayer) basicGamePlayer).getPlayer();
                final HashMap<Integer, ItemStack> rest = player.getInventory().addItem(stageType.getBoatInventory());

                //Drop if player Inventory is full.
                if (!rest.isEmpty()){
                    final World world = player.getLocation().getWorld();
                    assert world != null;

                    rest.forEach((integer, itemStack) -> world.dropItemNaturally(player.getLocation(), itemStack));
                }
            }
        });

        //Loads structure
        builder.loadStructure((getStageType() == StageType.SMALL) ? Structure.Presets.STAGE_SMALL_INGAME : Structure.Presets.STAGE_BIG_INGAME, o ->
                menu.changeGuis(GuiPresets.BOAT_CONFIRM_GUI, GuiPresets.BOAT_CONFIRM_GUI));
    }

    /**
     * Game Loop
     */
    @Override
    public void tick() {
        if (players != null) {
            players.forEach(basicGamePlayer -> {

                if (basicGamePlayer != null) {
                    basicGamePlayer.renderAreas();

                    if (basicGamePlayer instanceof HumanGamePlayer) {
                        final HumanGamePlayer gamePlayer = (HumanGamePlayer) basicGamePlayer;
                        gamePlayer.getOwnGameAr().markBlock(plugin, gamePlayer.getPlayer());
                    }
                }
            });
        }
    }

    /**
     * Stops the Game.
     */
    @Override
    public void stop() {
        //Do exit stuff.
        exitGame();

        //Refresh the structure
        builder.loadStructure(getStageType().getStructurePreset(), o ->
                menu.changeGuis(GuiPresets.START_GUI, GuiPresets.START_GUI));
    }

    /**
     * Tries to initialize the players.
     * @return Returns if players can calculated.
     */
    private boolean calculatePlayers(){
        players = new HomogenTuple<>();
        final Vector dim = stageType.getDimensions();

        //Calculate Middle Location
        final Location midLocEnd = new ExLocation(loc.getWorld(), goalLoc.toVector().subtract(dir.getRelVecZ().multiply(stageType.getDimensions().getZ() / 2.0)));
        midLocEnd.subtract(dir.getRelVecX());
        midLocEnd.subtract(dir.getRelVecY());
        final Location midLocStart = new ExLocation(loc.getWorld(), loc.toVector().add(dir.getRelVecZ().multiply(stageType.getDimensions().getZ() / 2.0)));

        //Calculates all game areas.
        final EnemyGameArea enFrontAr = new EnemyGameArea(
                dir.subtractRelative().apply(getGoalLoc(), new Vector(2, 3, dim.getBlockZ() / 2 + 2)),
                dim.getBlockX() - 2,
                dim.getBlockY() - 6,
                dir, true
        );

        final EnemyGameArea enBackAr = new EnemyGameArea(
                dir.addRelative().apply(getLoc(), new Vector(1, dim.getBlockY() - 3, dim.getBlockZ() / 2 + 1)),
                dim.getBlockX() - 2,
                dim.getBlockY() - 6,
                dir, true
        );

        final OwnGameArea ownFrontAr = new OwnGameArea(
                dir.subtractRelative().apply(getGoalLoc(), new Vector(2, dim.getBlockY() - 2, dim.getBlockZ() / 2 + 4)),
                dim.getBlockX() - 2,
                dim.getBlockY() - 6,
                dir, true
        );

        final OwnGameArea ownBackAr = new OwnGameArea(
                dir.addRelative().apply(getLoc(), new Vector(1 , 2, dim.getBlockZ() / 2 + 3)),
                dim.getBlockX() - 2,
                dim.getBlockY() - 6,
                dir, false
        );

        //Marks if player was found
        boolean flagFront = false, flagBack = false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR) continue;

            //Search for front player
            if (!flagFront && ExLocation.intersects(loc, midLocEnd, player.getLocation(), dir)){
                players.setKey(new HumanGamePlayer(this, player, player.getGameMode(),enFrontAr, ownFrontAr, true));
                player.setGameMode(GameMode.ADVENTURE);
                flagFront = true;
            }
            //Search for back player
            else if (!flagBack && ExLocation.intersects(midLocStart, goalLoc, player.getLocation(), dir)){
                players.setValue(new HumanGamePlayer(this, player, player.getGameMode(), enBackAr, ownBackAr, false));
                player.setGameMode(GameMode.ADVENTURE);
                flagBack = true;
            }
            if (flagFront && flagBack) break;
        }

        /*if (!flagFront || !flagBack) {
            if (flagFront.get()) ((HumanGamePlayer)players.getKey()).getPlayer().sendMessage(ChatColor.RED + " There is no other Player.");
            stop();
            return false;
        }*/

        if (players.getKey() == null) players.setKey(new ComputerGamePlayer(this, enFrontAr, ownFrontAr, true));
        if (players.getValue() == null) players.setValue(new ComputerGamePlayer(this, enBackAr, ownBackAr, false));

        return true;
    }


    /**
     * Stops the game and destroys the stage.
     */
    public void destroy(){
        exitGame();

        builder.destroyGame();
        if (BattleshipGame.GAMES.containsValue(this)) {
            BattleshipGame.GAMES.remove(ExLocation.getUniqueString(this.getLoc()));
        }
    }

    /**
     * Exits the game and make it ready for next time.
     */
    public void exitGame(){
        //Clear all players -> stages
        if (players != null) players.forEach(BasicGamePlayer::clear);

        //Stops and renew the timer
        super.stop();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        };

        isRunning = false;
        players = null;
    }

    /**
     * @return Returns if @param player is currently playing.
     */
    public boolean isPlaying(Player player){
        final AtomicBoolean flag = new AtomicBoolean(false);
        players.forEach(basicGamePlayer -> {
            if (basicGamePlayer instanceof HumanGamePlayer){
                if (((HumanGamePlayer) basicGamePlayer).getPlayer().getName().equals(player.getName())) {
                    flag.set(true);
                }
            }
        });
        return flag.get();
    }
}
