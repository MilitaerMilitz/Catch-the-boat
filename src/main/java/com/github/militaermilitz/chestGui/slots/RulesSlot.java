package com.github.militaermilitz.chestGui.slots;

import com.github.militaermilitz.chestGui.ChestGuiSlot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This class Overrides the onAction Method of ChestGuiSlot to show the game rules.
 */
public class RulesSlot extends ChestGuiSlot {

    /**
     * @param stack Stack of the Slot
     * @param position Position in Container
     * @param name Name of the ItemStack
     */
    public RulesSlot(ItemStack stack, int position, @Nullable String name) {
        super(stack, position, name);
    }

    @Override
    public void onAction(Player player, Location location) {
        player.sendMessage("\n" + ChatColor.LIGHT_PURPLE +  ChatColor.BOLD + ChatColor.UNDERLINE + "Rules for BattleShip" + "\n \n" +
                                   ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Game Objective\n" +
                                   "\n" + ChatColor.RESET + ChatColor.GOLD +
                                   "The object of Catch-The-Boat is to try and sink all of the other player's ships before they sink all of yours. " +
                                   "All of the other player's ships are somewhere on his/her board. " +
                                   "You try and hit them by selecting one of the squares on the board. " +
                                   "The other player also tries to hit your ships. " +
                                   "Neither you nor the other player can see the other's board so you must try to guess where the boats are. " +
                                   "Each board in the physical game has two grids:  " +
                                   "the lower (horizontal) section for the player's ships and the upper part (vertical during play) for recording the player's guesses.\n\n" +

                                   ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Placing The Boats\n" +
                                   "\n" + ChatColor.RESET + ChatColor.GOLD +
                                   "Each player places they ships somewhere on their board. (You will find the them in your inventory after confirming.)" +
                                   "The ships can only be placed vertically or horizontally. Diagonal placement is not allowed. " +
                                   "No part of a ship may hang off the edge of the board. " +
                                   "Ships may not overlap each other. No ships may be placed on another ship. \n" +
                                   "Once the guessing begins, the players may not move the ships.\n" +
                                   "The ships are:  Battleship (occupies 5 spaces), Cruiser (4), Destroyer (3), and Submarine (2).  \n" +

                                   ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD +"Playing the Game\n" +
                                   "\n" + ChatColor.RESET + ChatColor.GOLD +
                                   "Player's take turns guessing by selecting one of the squares on the board. The opponent responds with \"Hit\" or \"Water\" as appropriate. " +
                                   "Both players should mark their board with pegs:  green for hit, red for miss. " +
                                   "For example, if you select a field where the opponent does not have any ship located, he would respond with \"water\". " +
                                   "The miss will be recorded automatically. " +
                                   "As soon as all of one player's ships have been sunk, the game ends. ");
    }
}
