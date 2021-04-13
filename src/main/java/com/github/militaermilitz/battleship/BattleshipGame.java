package com.github.militaermilitz.battleship;

import com.github.militaermilitz.CatchTheBoat;
import com.github.militaermilitz.battleship.engine.area.EnemyGameArea;
import com.github.militaermilitz.battleship.engine.area.OwnGameArea;
import com.github.militaermilitz.battleship.engine.player.BasicGamePlayer;
import com.github.militaermilitz.battleship.engine.player.HumanGamePlayer;
import com.github.militaermilitz.chestGui.GuiPresets;
import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.*;
import com.github.militaermilitz.util.HomogenTuple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Alexander Ley
 * @version 1.8
 * This Class handles all action around the game and includes the game loop.
 */
public class BattleshipGame extends BukkitTickable {

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

    /**
     * Saves reference to player which can shoot.
     */
    private BasicGamePlayer movePlayer = null;

    private boolean isRunning = false;

    /**
     * Creates a new game und tries to build the stage.
     * @param player Player for messages.
     * @param location Origin Location
     * @param plugin Needed for loading the structure.
     * @param buildStage Boolean if stage have to build or not.
     * @throws NotEnoughSpaceException If not enough space.
     */
    BattleshipGame(@NotNull StageType stageType, @NotNull Location location, @Nullable Player player, @NotNull Plugin plugin, boolean buildStage) throws NotEnoughSpaceException {
        super(plugin);
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
     * Creates a new game und tries to build the stage.
     * @param stageType Defines if game is in small or big version.
     * @param location Origin Location
     * @throws NotEnoughSpaceException if game cannot build.
     */
    public BattleshipGame(@NotNull StageType stageType, @NotNull Location location, @Nullable Player player, @NotNull Plugin plugin) throws NotEnoughSpaceException, URISyntaxException {
        this(stageType, location, player, plugin, true);
    }

    /**
     * @return Returns right game instance from playing player.
     */
    public static @Nullable BattleshipGame getGameFromPlayer(Player player){
        final List<BattleshipGame> games = BattleshipGame.GAMES.values().stream()
                .filter(battleshipGame -> battleshipGame.isPlaying(player))
                .collect(Collectors.toList());

        if (games.isEmpty()) return null;
        assert games.size() == 1;

        return games.get(0);
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

    Plugin getPlugin() {
        return plugin;
    }

    BattleshipMenu getMenu() {
        return menu;
    }

    public HomogenTuple<BasicGamePlayer> getPlayers() {
        return players;
    }

    public BasicGamePlayer getMovePlayer() {
        return movePlayer;
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
            CatchTheBoat.LOGGER.log(Level.SEVERE, "Game cannot be started twice. -> Game will be removed.");
            destroy();
            return;
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
            final boolean allPlayersReady = allPlayersReady();

            //Player Loop
            players.forEach(basicGamePlayer -> {

                if (basicGamePlayer != null) {
                    basicGamePlayer.renderAreas();

                    if (basicGamePlayer instanceof HumanGamePlayer) {
                        final HumanGamePlayer gamePlayer = (HumanGamePlayer) basicGamePlayer;
                        final Player player = gamePlayer.getPlayer();

                        //Boat placing
                        if (!allPlayersReady) {
                            gamePlayer.getOwnGameAr().markBlock(player);
                        }
                        //Shooting
                        else{
                            if (basicGamePlayer == movePlayer){
                                gamePlayer.getEnGameAr().markBlock( player);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    TextComponent.fromLegacyText(ChatColor.GOLD + "Its your turn. Shoot the enemies Field.")
                                );
                            }
                            else {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    TextComponent.fromLegacyText(ChatColor.RED + "Waiting for enemy to shoot.")
                                );
                            }
                        }

                        //Waiting fro enemy to confirm boat positions.
                        if (!allPlayersReady && gamePlayer.isReady()) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(ChatColor.GOLD + "Waiting for enemy.")
                            );
                        }
                    }

                    //If one player has won.
                    if (allPlayersReady() && basicGamePlayer.hasWon()){
                        if (basicGamePlayer instanceof  HumanGamePlayer){
                            final Player player = ((HumanGamePlayer) basicGamePlayer).getPlayer();
                            player.sendTitle("", org.bukkit.ChatColor.GREEN + "You are the Winner", 20, 60, 30);

                            final BasicGamePlayer basicEnemy = getEnemy(basicGamePlayer);

                            if (basicEnemy instanceof HumanGamePlayer){
                                final Player enemy = ((HumanGamePlayer) basicEnemy).getPlayer();
                                enemy.sendTitle("", org.bukkit.ChatColor.RED + "You have lost", 20, 60, 30);
                            }
                        }

                        stop();
                    }
                }


            });

            //Tests if ChestGuis are alive and if not -> Removing stage
            if (!menu.chestGuisAlive()){
                CatchTheBoat.LOGGER.log(Level.SEVERE, "ChestGui Block is not a container -> removing Game Stage.");
                this.destroy();
            }
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
                dir, false
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

                Location tpLoc = dir.addRelative().apply(getLoc(), new Vector(4, 6, 2));
                tpLoc.add(new Vector(0.5, 0, 0.5));
                tpLoc.setYaw(dir.getYaw());

                player.teleport(tpLoc);
                flagFront = true;
            }
            //Search for back player
            else if (!flagBack && ExLocation.intersects(midLocStart, goalLoc, player.getLocation(), dir)){
                players.setValue(new HumanGamePlayer(this, player, player.getGameMode(), enBackAr, ownBackAr, false));
                player.setGameMode(GameMode.ADVENTURE);

                Location tpLoc = dir.addRelative().apply(getLoc(), new Vector(4, 6,  (stageType == StageType.SMALL) ? 28 : 40));
                tpLoc.add(new Vector(0.5, 0, 0.5));
                tpLoc.setYaw(dir.getOpposite().getYaw());

                player.teleport(tpLoc);
                flagBack = true;
            }
            if (flagFront && flagBack) break;
        }

        //TODO Change
        if (!flagFront || !flagBack) {
            if (flagFront) ((HumanGamePlayer)players.getKey()).getPlayer().sendMessage(ChatColor.RED + " There is no other Player.");
            stop();
            return false;
        }

        /*if (players.getKey() == null) players.setKey(new ComputerGamePlayer(this, enFrontAr, ownFrontAr, true));
        if (players.getValue() == null) players.setValue(new ComputerGamePlayer(this, enBackAr, ownBackAr, false));*/

        this.movePlayer = players.getKey();

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
        if (players != null) players.stream().filter(Objects::nonNull).forEach(BasicGamePlayer::clear);

        //Stops and renew the timer
        super.stop();

        isRunning = false;
        players = null;
    }

    /**
     * @return Returns if @param player is currently playing.
     */
    public boolean isPlaying(@Nullable Player player){
        if (players == null || player == null) return false;
        final AtomicBoolean flag = new AtomicBoolean(false);
        players.forEach(basicGamePlayer -> {
            if (basicGamePlayer != null) {
                if (basicGamePlayer instanceof HumanGamePlayer) {
                    if (((HumanGamePlayer) basicGamePlayer).getPlayer().getName().equals(player.getName())) {
                        flag.set(true);
                    }
                }
            }
        });
        return flag.get();
    }

    /**
     * @return Returns if all players have confirmed boat positions.
     */
    public boolean allPlayersReady() {
        if (players == null) return false;
        return players.stream().allMatch(gamePlayer -> gamePlayer != null && gamePlayer.isReady());
    }

    /**
     * @return Returns playing BasicGamePlayer from player.
     */
    public @Nullable HumanGamePlayer getPlayingPlayer(@NotNull Player player){
        final List<HumanGamePlayer> gamePlayerList =  players.stream()
                .filter(basicGamePlayer -> basicGamePlayer instanceof HumanGamePlayer)
                .map(basicGamePlayer -> ((HumanGamePlayer) basicGamePlayer))
                .filter(humanGamePlayer -> humanGamePlayer.equalsPlayer(player))
                .collect(Collectors.toList());

        if (gamePlayerList.isEmpty()) return null;
        return gamePlayerList.get(0);
    }

    /**
     * Isolates the HumanGamePlayer.
     */
    public void isolatePlayer(@NotNull HumanGamePlayer gamePlayer){
        final boolean isFront = gamePlayer.isFront();
        gamePlayer.setIsolated();
        builder.isolatePlayer(isFront);
        menu.changeGuis((isFront) ? GuiPresets.WAITING_GUI : null, (!isFront) ? GuiPresets.WAITING_GUI : null);
    }

    /**
     * Swaps Move Player -_-
     */
    public void swapMovePlayer(){
        if (movePlayer.isFront()) movePlayer = players.getValue();
        else movePlayer = players.getKey();
    }

    /**
     * @return Returns Enemy from BasicGamePlayer.
     */
    public @Nullable BasicGamePlayer getEnemy(@NotNull BasicGamePlayer gamePlayer){
        if (players == null) return null;

        if (gamePlayer.isFront()) return players.getValue();
        else return players.getKey();
    }
}
