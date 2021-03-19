package com.github.militaermilitz.exception;

import org.bukkit.Location;

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
