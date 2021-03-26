package com.github.militaermilitz.command;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.BattleshipGameBuilder;
import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BattleshipCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length == 2 && args[0].equals("create")){
                //Initialise BattleshipGame
                final BattleshipGame game;
                try {
                    final String mode = args[1];
                    game = (mode.equals("small")) ? new BattleshipGame(true, player.getLocation()) :
                                                (mode.equals("big")) ? new BattleshipGame(false, player.getLocation()) : null;
                    if (game == null){
                        sender.sendMessage(ChatColor.RED + "The command syntax is wrong.");
                        return false;
                    }
                    throw new NotEnoughSpaceException(player.getLocation());
                }
                catch (NotEnoughSpaceException e) {
                    final String mode = args[1];
                    sender.sendMessage(ChatColor.RED + "There is not enough Space to create a game.");
                    sender.sendMessage(ChatColor.RED + "To create a " + mode + " game area. You need at least an empty field of "+
                                               ((mode.equals("small")) ? BattleshipGameBuilder.DIMENSIONS_SMALL : BattleshipGameBuilder.DIMENSIONS_BIG) + " Blocks (Width/Height/Length).");
                    sender.sendMessage(ChatColor.RED + "We suggest an ocean area.");

                    //Highlight space
                    Location location = player.getLocation();
                    final World world = player.getLocation().getWorld();
                    assert world != null;
                    final Biome biome = world.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());

                    final boolean isOcean = Arrays.stream(Biome.values())
                            .filter(LambdaBiome -> LambdaBiome.getKey().getKey().contains("ocean"))
                            .collect(Collectors.toList())
                    .contains(biome);

                    if (location.getBlockY() == 63 && isOcean){
                        location = location.subtract(new Vector(0, 3, 0));
                    }

                    BattleshipGameBuilder.highlight(mode.equals("small"), location, Direction.getFromLocation(player.getLocation()));
                    return true;
                }

                //Build Stage

                //return true;
            }
            else return false;
        }
        else{
            sender.sendMessage(ChatColor.RED + "Only Players can use this command.");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1){
            list.add("create");
            list.add("remove");
        }
        if (args.length == 2 && args[0].equals("create")){
            list.add("small");
            list.add("big");
        }
        return list;
    }
}
