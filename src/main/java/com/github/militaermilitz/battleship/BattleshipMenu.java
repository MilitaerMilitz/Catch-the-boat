package com.github.militaermilitz.battleship;

import com.github.militaermilitz.CatchTheBoat;
import com.github.militaermilitz.chestGui.ChestGui;
import com.github.militaermilitz.chestGui.GuiPresets;
import com.github.militaermilitz.exception.NoContainerException;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.ExLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * @author Alexander Ley
 * @version 1.2
 * This class handles the battleshipGame Menus.
 */
public class BattleshipMenu {

    private final BattleshipGame game;
    private ChestGui frontGui, backGui;
    private final Location frontLoc, backLoc;

    /**
     * If initialize() was called.
     */
    private boolean isInitialized = false;

    /**
     * @param constructLater Possibility to initialize the actual Menu later.
     */
    public BattleshipMenu(@NotNull BattleshipGame game, boolean constructLater){
        this.game = game;

        final Direction direction = game.getDir();

        final Vector toFrontGui = game.getStageType().getFrontGuiPos();
        final Vector toBackGui = game.getStageType().getBackGuiPos();

        //Calculate front Position
        final Location front = game.getLoc();
        ExLocation.align(front);
        front.add(direction.getRelVecX().multiply(toFrontGui.getBlockX()));
        front.add(direction.getRelVecY().multiply(toFrontGui.getBlockY()));
        front.add(direction.getRelVecZ().multiply(toFrontGui.getBlockZ()));

        //Calculate back Position
        final Location back = game.getLoc();
        ExLocation.align(back);
        back.add(direction.getRelVecX().multiply(toBackGui.getBlockX()));
        back.add(direction.getRelVecY().multiply(toBackGui.getBlockY()));
        back.add(direction.getRelVecZ().multiply(toBackGui.getBlockZ()));

        this.frontLoc = front;
        this.backLoc = back;

        if (!constructLater) initialize(game);
    }

    /**
     * Initialize the menu for the first time and set the Guis to start Gui.
     */
    public void initialize(BattleshipGame game){
        if (isInitialized) throw new IllegalStateException("The Object is already constructed");

        final World world = game.getLoc().getWorld();
        assert world != null;

        //Set first Guis
        try {
            this.frontGui = new ChestGui(GuiPresets.START_GUI, frontLoc, game.getStageType(), game.getDir(), true);
            this.backGui = new ChestGui(GuiPresets.START_GUI, backLoc, game.getStageType(), game.getDir(), false);
        }
        catch (NoContainerException exception){
            CatchTheBoat.LOGGER.log(Level.SEVERE, "ChestGui Block is not a container -> removing Game Stage.", exception);
            game.destroy();
            return;
        }

        //Set Menu initialized
        isInitialized = true;
    }

    /**
     * Change Guis with GuiPresets.
     * If one presets is null, it will be ignored.
     */
    public void changeGuis(@Nullable GuiPresets frontGui, @Nullable GuiPresets backGui){
        try {
            if (frontGui != null)
                this.frontGui = new ChestGui(frontGui, frontLoc, game.getStageType(), game.getDir(), true);
            if (backGui != null)
                this.backGui = new ChestGui(backGui, backLoc, game.getStageType(), game.getDir(), false);
        }
        catch (NoContainerException exception){
            CatchTheBoat.LOGGER.log(Level.SEVERE, "ChestGui Block is not a container -> removing Game Stage.", exception);
            game.destroy();
        }
    }

    /**
     * Removes ChestGuis from list and frees the chest from it Gui Task.
     */
    public void deleteGuis(){
        ChestGui.CHEST_GUI.remove(ExLocation.getUniqueString(frontGui.getLocation()));
        ChestGui.CHEST_GUI.remove(ExLocation.getUniqueString(backGui.getLocation()));
        this.frontGui.clearInventory();
        this.backGui.clearInventory();
        this.frontGui = null;
        this.backGui = null;
    }

    /**
     * @return Returns if all chestGuis are alive and usable.
     */
    public boolean chestGuisAlive(){
        return frontGui.getLocation().getBlock().getState() instanceof Container &&
                    backGui.getLocation().getBlock().getState() instanceof Container;
    }
}
