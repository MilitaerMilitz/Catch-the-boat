package com.github.militaermilitz.command;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.BattleshipGameBuilder;
import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.Direction;
import com.github.militaermilitz.mcUtil.StageType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This class handles the battleship command.
 *
 */
public class BattleshipCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public BattleshipCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;

            //Creating Task
            if (args.length == 2 && args[0].equals("create")){

                //Initialise BattleshipGame
                final BattleshipGame game;
                final StageType stageType = StageType.getFromString(args[1]);

                if (stageType == null){
                    sender.sendMessage(ChatColor.RED + "The command syntax is wrong.");
                    return false;
                }

                try {
                    game = new BattleshipGame(stageType, player.getLocation(), player, plugin);
                }
                //Highlight if NotEnoughSpaceException
                catch (NotEnoughSpaceException | URISyntaxException e) {
                    final String mode = args[1];

                    //Message
                    sender.sendMessage(ChatColor.RED + "There is not enough Space to create a game.");
                    sender.sendMessage(ChatColor.RED + "To create a " + mode + " game area. You need at least an empty field of "+
                                           StageType.getFromString(mode) + " Blocks (Width/Height/Length).");
                    sender.sendMessage(ChatColor.RED + "We suggest an ocean area.");

                    //Highlight space
                    final Location location = player.getLocation();
                    location.subtract(new Vector(0, 3, 0));

                    final World world = player.getLocation().getWorld();
                    assert world != null;

                    BattleshipGameBuilder.highlight(location,
                        Direction.getFromLocation(player.getLocation()),
                        stageType.getDimensions()
                    );
                    return true;
                }
                return true;
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
        }
        if (args.length == 2 && args[0].equals("create")){
            list.add("small");
            list.add("big");
        }
        return list;
    }
}
