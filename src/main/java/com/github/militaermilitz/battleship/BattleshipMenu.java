package com.github.militaermilitz.battleship;

import com.github.militaermilitz.chestGui.ChestGui;
import com.github.militaermilitz.chestGui.GuiPresets;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.ExLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * @author Alexander Ley
 * @version 1.0
 * This class handles the battleshipGame Menus.
 */
public class BattleshipMenu {

    private final BattleshipGame game;
    private ChestGui frontGui, backGui;

    /**
     * If initialize() was called.
     */
    private boolean isInitialized = false;

    /**
     * @param constructLater Possibility to initialize the actual Menu later.
     */
    public BattleshipMenu(BattleshipGame game, boolean constructLater){
        this.game = game;
        if (!constructLater) initialize(game);
    }

    /**
     * Initialize the menu for the first time and set the Guis to start Gui.
     */
    public void initialize(BattleshipGame game){
        if (isInitialized) throw new IllegalStateException("The Object is already constructed");

        final World world = game.getLocation().getWorld();
        assert world != null;

        final Direction direction = game.getDirection();
        final Location location = game.getLocation();

        //Direction Vector pointing from game location to chest position
        final Vector toFrontGui = game.getStageType().getFrontGuiPos();
        final Vector toBackGui = game.getStageType().getBackGuiPos();

        //Calculate front Position
        final ExLocation front = new ExLocation(location);
        front.align();
        front.add(direction.getRelVecX().multiply(toFrontGui.getBlockX()));
        front.add(direction.getRelVecY().multiply(toFrontGui.getBlockY()));
        front.add(direction.getRelVecZ().multiply(toFrontGui.getBlockZ()));

        //Calculate back Position
        final ExLocation back = new ExLocation(location);
        back.align();
        back.add(direction.getRelVecX().multiply(toBackGui.getBlockX()));
        back.add(direction.getRelVecY().multiply(toBackGui.getBlockY()));
        back.add(direction.getRelVecZ().multiply(toBackGui.getBlockZ()));

        //Set first Guis
        this.frontGui = new ChestGui(GuiPresets.START_GUI, front, game.getStageType(), game.getDirection(), true);
        this.backGui = new ChestGui(GuiPresets.START_GUI, back, game.getStageType(), game.getDirection(), false);

        //Set Menu initialized
        isInitialized = true;
    }

    BattleshipGame getGame() {
        return game;
    }

    public void deleteGuis(){
        ChestGui.CHEST_GUI.remove(ExLocation.getUniqueString(frontGui.getLocation()));
        ChestGui.CHEST_GUI.remove(ExLocation.getUniqueString(backGui.getLocation()));
        this.frontGui = null;
        this.backGui = null;
    }
}
