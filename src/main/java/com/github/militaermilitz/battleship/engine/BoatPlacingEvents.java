package com.github.militaermilitz.battleship.engine;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.engine.player.BasicGamePlayer;
import com.github.militaermilitz.battleship.engine.player.HumanGamePlayer;
import com.github.militaermilitz.mcUtil.StageType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Class handles all game boats events.
 */
public class BoatPlacingEvents implements Listener {

    @EventHandler
    public void onPlaceBoat(PlayerInteractEvent event){
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
            && ItemGameBoatStack.isGameBoat(event.getPlayer().getInventory().getItemInMainHand())
        ) {
            event.setCancelled(true);

            try {
                HumanGamePlayer gamePlayer = null;

                //Tries to find the game the player is playing in.
                for (BattleshipGame game : BattleshipGame.GAMES.values()) {
                    final BasicGamePlayer playerFront = game.getPlayers().getKey();
                    final BasicGamePlayer playerBack = game.getPlayers().getValue();

                    if (playerFront instanceof HumanGamePlayer && ((HumanGamePlayer) playerFront).equalsPlayer(event.getPlayer())){
                        gamePlayer = (HumanGamePlayer) playerFront;
                        break;
                    }
                    else if (playerBack instanceof HumanGamePlayer && ((HumanGamePlayer) playerBack).equalsPlayer(event.getPlayer())){
                        gamePlayer = (HumanGamePlayer) playerBack;
                        break;
                    }
                }

                //The boat cannot placed when the player is not playing.
                if (gamePlayer == null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You are playing not an game.");
                    event.getPlayer().getInventory().removeItem(StageType.BIG.getBoatInventory());
                    return;
                }

                final Player player = gamePlayer.getPlayer();

                if (!gamePlayer.placeBoat()){
                    player.sendMessage(ChatColor.RED + "The boat cannot placed here.");
                }
                else{
                    //Removes boat from inventory if it was successful.
                    player.getInventory().removeItem(player.getInventory().getItemInMainHand());
                }

            } catch (IndexOutOfBoundsException ignored){ }
        }
    }

    /**
     * Cancel game boat drop.
     */
    @EventHandler
    public void onBoatDrop(PlayerDropItemEvent event){
        if (ItemGameBoatStack.isGameBoat(event.getItemDrop().getItemStack())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Boat cannot dropped.");
            event.setCancelled(true);
        }
    }

    /**
     * Cancel boat entity event on playing players.
     */
    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent event){
        if (BattleshipGame.GAMES.values().stream().anyMatch(game -> game.isPlaying(event.getPlayer()))){
            event.setCancelled(true);
        }
    }
}
