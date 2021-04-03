package com.github.militaermilitz.chestGui;

import com.github.militaermilitz.mcUtil.ExLocation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author Alexander Ley
 * @version 1.0
 * This class handles all events belonging to Gui Chests.
 */
public class ChestGuiEvents implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final Block block = event.getClickedBlock();

            if (block != null) {
                //Tries to get chest gui
                final ChestGui chestGui = ChestGui.CHEST_GUI.get(ExLocation.getUniqueString(block.getLocation()));

                if (chestGui != null) {
                    chestGui.onOpen(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        final Location location = event.getInventory().getLocation();
        assert location != null;
        //Tries to get chest gui
        final ChestGui chestGui = ChestGui.CHEST_GUI.get(ExLocation.getUniqueString(location));

        if (chestGui != null){
            chestGui.onClose((Player) event.getPlayer());
        }
    }

    @EventHandler
    public void onChestDestroy(BlockBreakEvent event){
        final Location location = event.getBlock().getLocation();
        //Tries to get chest gui
        final ChestGui chestGui = ChestGui.CHEST_GUI.get(ExLocation.getUniqueString(location));

        if (chestGui != null){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        final Location location = event.getInventory().getLocation();
        assert location != null;
        //Tries to get chest gui
        final ChestGui chestGui = ChestGui.CHEST_GUI.get(ExLocation.getUniqueString(location));

        if (chestGui != null){
            final ItemStack clickedStack = event.getCurrentItem();

            if (clickedStack != null) {
                try {
                    chestGui.getSlots().stream()
                            .filter(chestGuiSlot -> clickedStack.equals(chestGuiSlot.getStack()))
                            .collect(Collectors.toList())
                            .get(0)
                            .onAction((Player) event.getWhoClicked(), location);
                }
                catch (IndexOutOfBoundsException ignored){ }

                //Cancel Container harvesting
                event.setCancelled(true);
            }
        }
    }
}
