package com.github.militaermilitz.mcUtil;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This enum handles different Directions and offers Methods to do ^ ^ ^ things (Relative to direction).
 *
 */
public enum Direction {
    SOUTH(new Vector(1, 0, 0), new Vector(0, 1, 0), new Vector(0, 0, 1)),
    NORTH(new Vector(-1, 0, 0), new Vector(0, 1, 0), new Vector(0, 0, -1)),
    WEST(new Vector(0, 0, 1), new Vector(0, 1, 0), new Vector(-1, 0, 0)),
    EAST(new Vector(0, 0, -1), new Vector(0, 1, 0), new Vector(1, 0, 0));

    //Relative direction vectors.
    private final Vector relVecX, relVecY, relVecZ;

    Direction(Vector relVecX, Vector relVecY, Vector relVecZ){
        this.relVecX = relVecX;
        this.relVecY = relVecY;
        this.relVecZ = relVecZ;
    }

    public static Direction getFromLocation(Location location){
        double direction = (location.getYaw() % 360);
        if (direction < 0) direction += 360;

        System.out.println(direction);

        if ((direction < 45 && direction >= 0) || direction >= 315) return SOUTH;
        else if (direction >= 45 && direction < 135) return WEST;
        else if (direction >= 135 && direction < 225) return NORTH;
        else return EAST;
    }

    public Vector getRelVecX() {
        return new Vector(relVecX.getBlockX(), relVecX.getBlockY(), relVecX.getBlockZ());
    }

    public Vector getRelVecY() {
        return new Vector(relVecY.getBlockX(), relVecY.getBlockY(), relVecY.getBlockZ());
    }

    public Vector getRelVecZ() {
        return new Vector(relVecZ.getBlockX(), relVecZ.getBlockY(), relVecZ.getBlockZ());
    }

    /**
     * @return Returns relative posX test predicate.
     * This predicate tests if a coordinate is smaller or larger than location.getBlockX() (Depends on Direction).
     * This Predicate is optimised to use in a for loop.
     */
    public Predicate<Double> getRelXTestPredicate(Location location){
        switch (this){
            case EAST:
            case WEST:
                return integer -> (this.getRelVecX().getBlockZ() < 0) ? integer < location.getBlockX() : integer > location.getBlockX();
            default:
                return integer -> (this.getRelVecX().getBlockX() >= 0) ? integer < location.getBlockX() : integer > location.getBlockX();
        }
    }

    /**
     * @return Returns relative posY test predicate.
     * This predicate tests if a coordinate is smaller than location.getBlockY().
     * This Predicate is optimised to use in a for loop.
     */
    public Predicate<Double> getRelYTestPredicate(Location location){
        return integer -> (this.getRelVecY().getBlockY() >= 0) ? integer < location.getBlockY() : integer > location.getBlockY();
    }

    /**
     * @return Returns relative posZ test predicate.
     * This predicate tests if a coordinate is smaller or larger than location.getBlockZ() (Depends on Direction).
     * This Predicate is optimised to use in a for loop.
     */
    public Predicate<Double> getRelZTestPredicate(Location location){
        switch (this){
            case EAST:
            case WEST:
                return integer -> (this.getRelVecZ().getBlockX() < 0) ? integer < location.getBlockZ() : integer > location.getBlockZ();
            default:
                return integer -> (this.getRelVecZ().getBlockZ() >= 0) ? integer < location.getBlockZ() : integer > location.getBlockZ();
        }
    }

    /**
     * @return Returns a function which increase the relative posX.
     * (^operand ^ ^)
     */
    public Function<Double, Double> increaseInRelX(double operand){
        switch (this){
            case EAST:
            case WEST:
                return pos -> (this.getRelVecX().getBlockZ() < 0) ? pos += operand : (pos -= operand);
            default:
                return pos -> (this.getRelVecX().getBlockX() >= 0) ? pos += operand : (pos -= operand);
        }
    }

    /**
     * @return Returns a function which increase the relative posY.
     * (^ ^operand ^)
     */
    public Function<Double, Double> increaseInRelY(double operand){
        return pos -> (this.getRelVecY().getBlockY() >= 0) ? pos += operand : (pos -= operand);
    }

    /**
     * @return Returns a function which increase the relative posZ.
     * (^ ^ ^operand)
     */
    public Function<Double, Double> increaseInRelZ(double operand){
        switch (this){
            case EAST:
            case WEST:
                return pos -> (this.getRelVecZ().getBlockX() < 0) ? pos += operand : (pos -= operand);
            default:
                return pos -> (this.getRelVecZ().getBlockZ() >= 0) ? pos += operand : (pos -= operand);
        }
    }

    /**
     * @return Returns a function which increment the relative posX.
     * (^1 ^ ^)
     */
    public Function<Double, Double> incrementInRelX(){
        return increaseInRelX(1);
    }

    /**
     * @return Returns a function which increment the relative posY.
     * (^ ^1 ^)
     */
    public Function<Double, Double> incrementInRelY(){
        return increaseInRelY(1);
    }

    /**
     * @return Returns a function which increment the relative posZ.
     * (^ ^ ^1)
     */
    public Function<Double, Double> incrementInRelZ(){
        return increaseInRelZ(1);
    }
}
