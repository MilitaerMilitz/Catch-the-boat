package com.github.militaermilitz.battleship.engine.area;

import com.github.militaermilitz.mcUtil.AtomicVector;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.ExLocation;
import com.github.militaermilitz.util.HomogenTuple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.BiConsumer;

/**
 * @author Alexander Ley
 * @version 1.2
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
    public abstract @Nullable HomogenTuple<Integer> getCoordsFromLocation(@NotNull Location location);

    /**
     * @return Returns Material to render when (Key) area[x][y] == true and (Value) area[x][y] == false or the player is locking on.
     */
    public abstract @NotNull HomogenTuple<Material> getMarkMaterial();

    /**
     * @return Returns Material to render when area[x][y] == null.
     */
    public abstract @NotNull Material getBasicMaterial();

    /**
     * Tests if a location is on the game area.
     */
    public boolean isInArea(@Nullable Location location) {
        if (location == null) return false;
        return getCoordsFromLocation(location) != null;
    }

    /**
     * Calculates x and y coordinates based on the block the player is locking on.
     * @return Returns null if location of the block the player is locking on is not in area.
     */
    public @Nullable HomogenTuple<Integer> getCoordsFromPlayer(@NotNull Player player){
        final Location loc = player.getTargetBlock(new HashSet<>(Arrays.asList(Material.AIR, Material.BARRIER)), 20).getLocation();
        return getCoordsFromLocation(loc);
    }

    /**
     * Calculates Location from x and y Coordinates.
     */
    public @NotNull Location getLocationFromCoords(int x, int y){
        return getLoc().add(xVector.multiply(x)).add(yVector.multiply(y));
    }

    /**
     * This method renders the game area.
     * @param additionalRenderTask task which is executed after the basic render operation.
     */
    public void render(@Nullable BiConsumer<Integer, Integer> additionalRenderTask){
        forEach((x, y) -> {
            final Location location = getLocationFromCoords(x, y);

            if (area[x][y] == null) {
                location.getBlock().setType(getBasicMaterial());
            }
            else if (area[x][y]){
                location.getBlock().setType(getMarkMaterial().getKey());
            }
            else{
                location.getBlock().setType(getMarkMaterial().getValue());
            }

            if (additionalRenderTask != null) additionalRenderTask.accept(x, y);
        });
    }

    /**
     * Clears the area and set the area to null.
     */
    public void clear(){
        forEach((x, y) -> {
            final Location location = getLocationFromCoords(x, y);
            area[x][y] = null;
            location.getBlock().setType(getBasicMaterial());
        });
    }

    /**
     * Marks the block the player is locking on.
     */
    public void markBlock(@NotNull Player player){
        final Location loc = player.getTargetBlock(new HashSet<>(Arrays.asList(Material.AIR, Material.BARRIER)), 20).getLocation();

        if (isInArea(loc)){
            clearMark();

            final HomogenTuple<Integer> coords = getCoordsFromLocation(loc);
            assert coords != null;

            final int x = coords.getKey(), y = coords.getValue();

            if (area[x][y] == null) loc.getBlock().setType(getMarkMaterial().getKey());
        }
    }

    /**
     * Removes all marks from area.
     */
    public void clearMark(){
        forEach((x, y) -> {
            if (area[x][y] == null){
                getLocationFromCoords(x, y).getBlock().setType(getBasicMaterial());
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
     * Checks if there is a boat at position (x, y).
     */
    public boolean isBoat(int x, int y){
        return area[x][y] != null && area[x][y];
    }

    /**
     * Tries to place a boat.
     * @param length Length of the boat.
     * @param direction The direction the boat should placed.
     * @return Returns if placing was successful.
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
     * Checks if whole area is empty = No boats anymore on the field.
     */
    public boolean isAreaEmpty(){
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                if (isBoat(x, y)) return false;
            }
        }
        return true;
    }

    /**
     * [Debug] Prints area into console.
     */
    public void printArea(){
        for (int y = 0; y < height; y++){
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++){
                line.append((area[x][y] == null) ? "e" : ((area[x][y]) ? 1 : 0));
                if (x != width - 1) line.append(" ");
            }
            System.out.println(line);
        }
    }
}
