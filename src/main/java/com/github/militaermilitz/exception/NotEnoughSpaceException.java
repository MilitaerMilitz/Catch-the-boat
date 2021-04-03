package com.github.militaermilitz.exception;

import org.bukkit.Location;

/**
 * @author Alexander Ley
 * @version 1.0
 * If there is not enough space -> Exception is thrown.
 */
public class NotEnoughSpaceException extends Exception{

    private final Location location;

    public NotEnoughSpaceException(Location location) {
        super("Not enough space at " + location);
        this.location = location;
    }

    public NotEnoughSpaceException(String message, Location location) {
        super(message + ", " + location);
        this.location = location;
    }

    public NotEnoughSpaceException(String message, Throwable cause, Location location) {
        super(message + ", " + location, cause);
        this.location = location;
    }
}
