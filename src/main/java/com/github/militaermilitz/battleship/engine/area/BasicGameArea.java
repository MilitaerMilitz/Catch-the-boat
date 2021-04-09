package com.github.militaermilitz.battleship.engine.area;

import com.github.militaermilitz.mcUtil.AtomicVector;
import com.github.militaermilitz.mcUtil.BlockPlacingRunnable;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.ExLocation;
import com.github.militaermilitz.util.HomogenTuple;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.BiConsumer;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Class handles all basic operations for all game areas.
 */
public abstract class BasicGameArea {

    protected final Location loc;
    /**
     * Actual Game Area:
     * null -> water, 1/true -> Boat, 0/false -> water but boat not placeable
     */
    protected final Boolean[][] area;
    protected final AtomicVector xVector, yVector;
    protected final int width, height;
    protected final Direction gameDir;

    /**
     * @param loc Location (Zero point) of the area
     * @param xVector Pointing in xDir
     * @param yVector Pointing in yDir
     * @param gameDir Direction of the game
     */
    public BasicGameArea(@NotNull Location loc, int width, int height, @NotNull Vector xVector, @NotNull Vector yVector, @NotNull Direction gameDir) {
        this.loc = loc;
        this.area = new Boolean[width][height];
        this.xVector = new AtomicVector(xVector);
        this.yVector = new AtomicVector(yVector);
        this.width = width;
        this.height = height;
        this.gameDir = gameDir;
    }

    /**
     * @return Returns a copy of loc
     */
    public Location getLoc() {
        return new ExLocation(loc);
    }

    /**
     * Calculates x and y coordinates based on the difference between loc and @param location.
     * @return Returns null if location is not in area.
     */
    public abstract @Nullable HomogenTuple<Integer> getCoordFromLocation(@NotNull Location location);

    /**
     * @return Returns Material to render when area[x][y] == true and area[x][y] == false or the player is locking on.
     */
    public abstract HomogenTuple<Material> getMarkMaterial();

    /**
     * @return Returns Material to render when area[x][y] == null.
     */
    public abstract Material getBasicMaterial();

    /**
     * Tests if a location is on the game area.
     */
    public boolean isInArea(@Nullable Location location) {
        if (location == null) return false;
        return getCoordFromLocation(location) != null;
    }

    /**
     * Calculates x and y coordinates based on the block the player is locking on.
     * @return Returns null if location of the block the player is locking on is not in area.
     */
    public HomogenTuple<Integer> getCoordFromPlayer(@NotNull Player player){
        final Location loc = player.getTargetBlock(new HashSet<>(Arrays.asList(Material.AIR, Material.BARRIER)), 20).getLocation();
        return getCoordFromLocation(loc);
    }

    /**
     * Calculates Location from x and y Coordinates.
     */
    public Location getLocationFromCoords(int x, int y){
        return getLoc().add(xVector.multiply(x)).add(yVector.multiply(y));
    }

    /**
     * This method renders the game area.
     * @param plugin Is needed for placing block definitely on the Bukkit main Thread.
     * @param additionalRenderTask task which is executed after the basic render operation.
     */
    public void render(@NotNull Plugin plugin, @Nullable BiConsumer<Integer, Integer> additionalRenderTask){
        forEach((x, y) -> {
            final Location location = getLocationFromCoords(x, y);

            if (area[x][y] == null) {
                Bukkit.getScheduler().runTask(plugin, new BlockPlacingRunnable(location, getBasicMaterial()));
            }
            else {
                Bukkit.getScheduler().runTask(plugin, new BlockPlacingRunnable(location, getMarkMaterial().getValue()));
                if (additionalRenderTask != null) additionalRenderTask.accept(x, y);
            }
        });
    }

    /**
     * Clears the area and set the area to null.
     * @param plugin Is needed for placing block definitely on the Bukkit main Thread.
     */
    public void clear(@NotNull Plugin plugin){
        forEach((x, y) -> {
            final Location location = getLocationFromCoords(x, y);
            area[x][y] = null;
            Bukkit.getScheduler().runTask(plugin, new BlockPlacingRunnable(location, getBasicMaterial()));
        });
    }

    /**
     * Marks the block the player is locking on.
     * @param plugin Is needed for placing block definitely on the Bukkit main Thread.
     */
    public void markBlock(@NotNull Plugin plugin, @NotNull Player player){
        final Location loc = player.getTargetBlock(new HashSet<>(Arrays.asList(Material.AIR, Material.BARRIER)), 20).getLocation();

        if (isInArea(loc)){
            clearMark(plugin);

            final HomogenTuple<Integer> coords = getCoordFromLocation(loc);
            assert coords != null;

            final int x = coords.getKey(), y = coords.getValue();

            Bukkit.getScheduler().runTask(plugin, new BlockPlacingRunnable(loc, (area[x][y] == null) ?
                    getMarkMaterial().getKey() : getMarkMaterial().getValue()));
        }
    }

    /**
     * Removes all marks from area.
     * @param plugin Is needed for placing block definitely on the Bukkit main Thread.
     */
    public void clearMark(@NotNull Plugin plugin){
        forEach((x, y) -> {
            if (area[x][y] == null){
                final Location location = getLocationFromCoords(x, y);
                Bukkit.getScheduler().runTask(plugin, new BlockPlacingRunnable(location, getBasicMaterial()));
            }
        });
    }

    /**
     * Tests if around a position is a boat (Includes position).
     */
    public boolean isAroundBoat(int x, int y){
        for (int i = x - 1; i <= x + 1; i++){
            for (int j = y - 1; j <= y + 1; j++){
                try{
                    if (area[i][j] != null && area[i][j]) return true;
                }
                catch (IndexOutOfBoundsException ignored){ }
            }
        }

        return false;
    }

    /**
     * Set around a position @param bool (Includes position).
     */
    public void setAround(int x, int y, @Nullable Boolean bool){
        if (x < 0 || y < 0) return;

        for (int i = x - 1; i <= x + 1; i++){
            for (int j = y - 1; j <= y + 1; j++){
                try{
                    if (area[i][j] == null) area[i][j] = bool;
                }
                catch (IndexOutOfBoundsException ignored){ }
            }
        }
    }

    /**
     * Tries to place a boat.
     * @param length Length of the boat.
     * @param direction The direction the boat should placed.
     * @return Returns if placing was successfully.
     */
    public boolean placeBoat(int length, int x, int y, @NotNull Direction direction, boolean isFront){
        if (isBoatNotPlaceable(length, x, y, direction, isFront)) return false;

        int wx = x, wy = y;
        for (int i = 0 ; i < length; i++){

            if (i > 0){
                if (isFront) {
                    if (gameDir == direction) wy--;
                    else if (gameDir.getOpposite() == direction) wy++;
                    else if (gameDir.rotate90() == direction) wx--;
                    else if (gameDir.rotate270() == direction) wx++;
                }
                else{
                    if (gameDir == direction) wy++;
                    else if (gameDir.getOpposite() == direction) wy--;
                    else if (gameDir.rotate90() == direction) wx++;
                    else if (gameDir.rotate270() == direction) wx--;
                }
            }

            setAround(wx, wy, false);
            area[wx][wy] = true;
        }

        return true;
    }

    /**
     * Tries if a boat can placed.
     * @param length Length of the boat.
     * @param direction The direction the boat should placed.
     * @return Returns true if boat is not placeable.
     */
    public boolean isBoatNotPlaceable(int length, int x, int y, @NotNull Direction direction, boolean isFront){
        int wx = x, wy = y;
        for (int i = 0 ; i < length; i++){

            if (i > 0){
                if (isFront) {
                    if (gameDir == direction) wy--;
                    else if (gameDir.getOpposite() == direction) wy++;
                    else if (gameDir.rotate90() == direction) wx--;
                    else if (gameDir.rotate270() == direction) wx++;
                }
                else{
                    if (gameDir == direction) wy++;
                    else if (gameDir.getOpposite() == direction) wy--;
                    else if (gameDir.rotate90() == direction) wx++;
                    else if (gameDir.rotate270() == direction) wx--;
                }
            }

            if (wx < 0 || wy < 0 || wx >= width || wy >= height) return true;
            if (isAroundBoat(wx, wy)) return true;
        }

        return false;
    }

    /**
     * Basic for loop which enables lambdas.
     */
    protected void forEach(@NotNull BiConsumer<Integer, Integer> action){
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                action.accept(x, y);
            }
        }
    }

    /**
     * [Debug] Prints the area into console.
     */
    public void printArea(){
        for (int y = 0; y < height; y++){
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++){
                line.append((area[x][y] == null) ? "e" : ((area[x][y]) ? 1 : 0));
                if (x != width - 1) line.append(" ");
            }
            System.out.println(line.toString());
        }
    }
}
