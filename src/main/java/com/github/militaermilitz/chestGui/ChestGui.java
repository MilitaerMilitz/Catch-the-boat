package com.github.militaermilitz.chestGui;

import com.github.militaermilitz.CatchTheBoat;
import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.ExLocation;
import com.github.militaermilitz.mcUtil.StageType;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Alexander Ley
 * @version 1.0
 * This class represents a ches Gui.
 */
public class ChestGui {

    /**
     * All ChestGuis
     */
    public static final HashMap<String, ChestGui> CHEST_GUI = new HashMap<>();

    //Gui Data
    private final Container container;
    private final List<ChestGuiSlot> slots;
    private final String name;

    //Chest Data needed to find back the game
    private final Direction direction;
    private final StageType stageType;
    private final boolean isFront;

    /**
     * @param loc Location of ChestGui. Have to be a container. If not -> IllegalArgumentException
     * @param name Name of the Gui which is displayed in container.
     * @param stageType Type of the Stage for finding the game instance.
     * @param direction Direction of the Stage for finding the game instance.
     * @param isFront IsFront? For finding the game back.
     * @param slots Slots of the Gui.
     */
    ChestGui(Location loc, String name, StageType stageType, Direction direction, boolean isFront, List<ChestGuiSlot> slots){
        final Block block = loc.getBlock();
        if (!(block.getState() instanceof Container)) throw new IllegalArgumentException("Block is not a container");

        this.container = (Container) block.getState();
        this.name = name;
        this.slots = slots;
        this.direction = direction;
        this.stageType = stageType;
        this.isFront = isFront;

        final Inventory chestInventory = container.getInventory();
        chestInventory.clear();

        //Put Name into container
        final NBTTileEntity nbtChest = new NBTTileEntity(container);
        nbtChest.setString("CustomName", "{\"text\":\"" + name +"\"}");

        final ItemStack[] chestContent = new ItemStack[27];
        for (ChestGuiSlot slot : slots) {
            final NBTItem nbtSlotItem = new NBTItem(slot.getStack(), true);
            nbtSlotItem.setBoolean("ChestGuiSlot", true);

            chestContent[slot.getPosition()] = slot.getStack();
        }

        chestInventory.setContents(chestContent);

        CHEST_GUI.put(ExLocation.getUniqueString(container.getLocation()), this);
    }

    /**
     * @param loc Location of ChestGui. Have to be a container. If not -> IllegalArgumentException
     * @param name Name of the Gui which is displayed in container.
     * @param stageType Type of the Stage for finding the game instance.
     * @param direction Direction of the Stage for finding the game instance.
     * @param isFront IsFront? For finding the game back.
     * @param slots Slots of the Gui.
     */
    ChestGui(Location loc, String name, StageType stageType, Direction direction, boolean isFront, ChestGuiSlot... slots) {
        this(loc, name, stageType, direction, isFront, Arrays.asList(slots));
    }

    /**
     * @param presets Use GuiPresets for slots and guiName.
     * @param loc Location of ChestGui. Have to be a container. If not -> IllegalArgumentException
     * @param stageType Type of the Stage for finding the game instance.
     * @param direction Direction of the Stage for finding the game instance.
     * @param isFront IsFront? For finding the game back.
     */
    public ChestGui(GuiPresets presets, Location loc, StageType stageType, Direction direction, boolean isFront){
        this(loc, presets.getName(), stageType, direction, isFront, presets.getSlots());
    }

    //Getter
    Block getBlock(){
        return this.container.getBlock();
    }
    public Location getLocation(){
        return new ExLocation(this.container.getLocation());
    }
    String getName() {
        return name;
    }
    List<ChestGuiSlot> getSlots() {
        return slots;
    }
    Direction getDirection() {
        return direction;
    }
    StageType getStageType() {
        return stageType;
    }
    boolean isFront() {
        return isFront;
    }

    /**
     * Activated if the Gui is opened by a player
     */
    public void onOpen(Player player){

    }

    /**
     * Activated if the Gui is closed by a player
     */
    public void onClose(Player player){

    }

    /**
     * @return Returns belonging game instance and null if no game instance is found.
     */
    public BattleshipGame parseGame(){
        final Direction direction = getDirection();

        //Direction Vector pointing from game origin to chest
        final Vector toChest = (isFront()) ? getStageType().getFrontGuiPos() : getStageType().getBackGuiPos();

        //Calculate game Location
        final Location gameLoc = this.getLocation();
        gameLoc.subtract(direction.getRelVecX().multiply(toChest.getBlockX()));
        gameLoc.subtract(direction.getRelVecY().multiply(toChest.getBlockY()));
        gameLoc.subtract(direction.getRelVecZ().multiply(toChest.getBlockZ()));

        //Search game by Unique Location String.
        return BattleshipGame.GAMES.get(ExLocation.getUniqueString(gameLoc));
    }
}