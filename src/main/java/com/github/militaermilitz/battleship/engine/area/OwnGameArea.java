package com.github.militaermilitz.battleship.engine.area;

import com.github.militaermilitz.CatchTheBoat;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.ExLocation;
import com.github.militaermilitz.util.HomogenTuple;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Class handles all operations based on the game area which is showing the own boats.
 */
public class OwnGameArea extends BasicGameArea{

    private final Direction xDir;
    private final Direction yDir;

    /**
     * Saves all rendered boats.
     */
    private final ArmorStand[][] boatMarker;
    /**
     * Saves all direction of the boats.
     */
    private final Direction[][] dirCache;

    /**
     * @param loc Location (Zero point) of the area
     * @param gameDir Direction of the game
     */
    public OwnGameArea(Location loc, int width, int height, Direction gameDir, boolean isFront) {
        super(loc, width, height, ((isFront) ? gameDir.rotate270() : gameDir.rotate90()).getRelVecZ(),
              ((isFront) ? gameDir.getOpposite() : gameDir).getRelVecZ(), gameDir);

        boatMarker = new ArmorStand[width][height];
        dirCache = new Direction[width][height];

        if (isFront){
            this.xDir = gameDir.rotate270();
            this.yDir = gameDir.getOpposite();
        }
       else {
           this.xDir = gameDir.rotate90();
           this.yDir = gameDir;
        }
    }

    /**
     * Calculates x and y coordinates based on the difference between loc and @param location.
     * @return Returns null if location is not in area.
     */
    @Override
    public HomogenTuple<Integer> getCoordFromLocation(@NotNull Location location) {
        final Vector coords = Direction.SOUTH.subtractRelative().apply(loc, location.toVector()).toVector();

        int xCoord = (gameDir.XZswaped()) ? coords.getBlockZ() : coords.getBlockX();
        int yCoord = (gameDir.XZswaped()) ? coords.getBlockX() : coords.getBlockZ();

        if (xDir == Direction.EAST || xDir == Direction.SOUTH) xCoord *= -1;
        if (yDir == Direction.SOUTH || yDir == Direction.EAST) yCoord *= -1;

        if (coords.getBlockY() != 0 || xCoord < 0 || xCoord >= width || yCoord < 0 || yCoord >= height) return null;

        return new HomogenTuple<>(xCoord, yCoord);
    }

    /**
     * @return Returns Material to render when area[x][y] == true and area[x][y] == false or the player is locking on.
     */
    @Override
    public HomogenTuple<Material> getMarkMaterial() {
        return new HomogenTuple<>(Material.LIME_STAINED_GLASS, Material.RED_STAINED_GLASS);
    }

    /**
     * @return Returns Material to render when area[x][y] == null.
     */
    @Override
    public Material getBasicMaterial() {
        return Material.BLUE_STAINED_GLASS;
    }

    /**
     * This method renders the game area and manages boat entities.
     * @param plugin Is needed for placing block definitely on the Bukkit main Thread.
     * @param additionalRenderTask task which is executed after the basic render operation.
     */
    @Override
    public void render(@NotNull Plugin plugin, @Nullable BiConsumer<Integer, Integer> additionalRenderTask) {
        super.render(plugin, (x, y) -> {
            final ArmorStand stand =  boatMarker[x][y];

            //Spawn boat
            if (area[x][y] && stand == null){
                Location loc = getLocationFromCoords(x, y).add(new Vector(0, 1, 0));
                loc = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getY(), loc.getZ() + 0.5);

                final World world = loc.getWorld();
                assert world != null;

                final Location finalLoc = loc;

                final CountDownLatch latch = new CountDownLatch(1);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "summon armor_stand "
                            + finalLoc.getX() + " " + finalLoc.getY() + " " + finalLoc.getZ() + " "
                            + "{Silent:1b,Invulnerable:1b,Small:1b,Marker:1b,Invisible:1b,NoBasePlate:1b,PersistenceRequired:1b,Tags:[\"CTB\"],"
                            + "Passengers:[{id:\"minecraft:boat\",Invulnerable:1b,Type:\"oak\",Tags:[\"CTB\"]}]}");
                    latch.countDown();
                });

                //Waiting for command to be finished.
                try {
                    latch.await();
                }
                catch (InterruptedException e) {
                    CatchTheBoat.LOGGER.log(Level.SEVERE, "Error while spawning boat.", e);
                }

                final ArmorStand armorStand = world.getEntities().stream()
                        .filter(entity -> entity instanceof ArmorStand)
                        .map(entity -> ((ArmorStand) entity))
                        .filter(armorStand1 -> ExLocation.equalsPos(armorStand1.getLocation(), finalLoc))
                        .collect(Collectors.toList()).get(0);

                boatMarker[x][y] = armorStand;
            }
            //Removes Boat
            else if ((!area[x][y] || area[x][y] == null) && stand != null){
                stand.getPassengers().forEach(Entity::remove);
                stand.remove();
            }
            //Rotate boats.
            else if (stand != null && !stand.getPassengers().isEmpty()){
                stand.getPassengers().forEach(entity -> {
                    if (dirCache[x][y] != null) {
                        entity.setRotation(dirCache[x][y].getYaw(), 0);
                    }
                });
            }

            //Executes additional render Tasks
            if (additionalRenderTask != null) {
                additionalRenderTask.accept(x, y);
            }
        });

    }

    /**
     * Clears the area and set the area to null and removes all boats.
     * @param plugin Is needed for placing block definitely on the Bukkit main Thread.
     */
    @Override
    public void clear(@NotNull Plugin plugin) {
        super.clear(plugin);
        forEach((x, y) -> {
            if (boatMarker[x][y] != null){
                boatMarker[x][y].getPassengers().forEach(Entity::remove);
                boatMarker[x][y].remove();
                boatMarker[x][y] = null;
            }
        });
    }

    /**
     * Tries to place a boat and saves right direction in cache.
     * @param length Length of the boat.
     * @param direction The direction the boat should placed.
     * @return Returns if placing was successfully.
     */
    @Override
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
            dirCache[wx][wy] = direction;
            area[wx][wy] = true;
        }

        return true;
    }
}
