package com.github.militaermilitz.battleship.engine.area;

import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.util.HomogenTuple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Ley
 * @version 1.1
 * This Class handles all operations based on the game area which is showing the enemies boats.
 */
public class EnemyGameArea extends BasicGameArea{

    private final Direction xDir;

    /**
     * @param loc Location (Zero point) of the area
     * @param gameDir Direction of the game
     */
    public EnemyGameArea(@NotNull Location loc, int width, int height, @NotNull Direction gameDir, boolean isFront) {
        super(loc, width, height,
              ((isFront) ? gameDir.rotate270() : gameDir.rotate90()).getRelVecZ(), new Vector(0, -1, 0), gameDir);

        if (isFront){
            this.xDir = gameDir.rotate270();
        }
        else {
            this.xDir = gameDir.rotate90();
        }
    }

    /**
     * Calculates x and y coordinates based on the difference between loc and @param location.
     * @return Returns null if location is not in area.
     */
    @Override
    public @Nullable HomogenTuple<Integer> getCoordsFromLocation(@NotNull Location location){
        final Vector coords = Direction.SOUTH.subtractRelative().apply(loc, location.toVector()).toVector();

        int xCoord = (gameDir.XZswaped()) ? coords.getBlockZ() : coords.getBlockX();
        int yCoord = coords.getBlockY();

        if (xDir == Direction.EAST || xDir == Direction.SOUTH) xCoord *= -1;

        //Return null if coords are not in game area
        if (((gameDir.XZswaped()) ? coords.getBlockX() != 0 : coords.getBlockZ() != 0) || xCoord < 0 || xCoord >= width || yCoord < 0 || yCoord >= height) return null;

        return new HomogenTuple<>(xCoord, yCoord);
    }

    /**
     * @return Returns Material to render when area[x][y] == true and area[x][y] == false or the player is locking on.
     */
    @Override
    public @NotNull HomogenTuple<Material> getMarkMaterial() {
        return new HomogenTuple<>(Material.LIME_CONCRETE, Material.RED_CONCRETE);
    }

    /**
     * @return Returns Material to render when area[x][y] == null.
     */
    @Override
    public @NotNull Material getBasicMaterial() {
        return Material.LIGHT_BLUE_CONCRETE;
    }

    public void setBool(int x, int y, @Nullable Boolean bool){
        area[x][y] = bool;
    }

    public boolean isAlreadyShoot(int x, int y){
        return area[x][y] != null;
    }
}
