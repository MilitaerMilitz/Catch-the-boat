package com.github.militaermilitz.battleship;

import com.github.militaermilitz.CatchTheBoat;
import com.github.militaermilitz.exception.NotEnoughSpaceException;
import com.github.militaermilitz.mcUtil.ExLocation;
import com.github.militaermilitz.mcUtil.IFileConstructor;
import com.github.militaermilitz.mcUtil.LocationData;
import com.github.militaermilitz.mcUtil.StageType;
import com.github.militaermilitz.util.FileUtil;
import org.bukkit.Bukkit;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

/**
 * @author Alexander Ley
 * @version 1.0
 * This class contains all data needed to initialize a new game.
 * This class is a bridge between gson and the actual BattleshipGame Class.
 */
public class BattleshipGameFile implements IFileConstructor<BattleshipGame> {
    private final StageType stageType;
    private final LocationData locationData;
    private final String pluginName;

    /**
     * Extracts needed data to construct a battleship game.
     * The extractet data will be saved to json via gson.
     */
    public BattleshipGameFile(BattleshipGame game){
        this.stageType = game.getStageType();
        this.locationData = new LocationData(game.getLocation());
        this.pluginName = game.getPlugin().getName();
    }

    /**
     * Saves game to file.
     */
    @Override
    public void saveToFile() {
        FileUtil.saveToJson(Paths.get("plugins/CatchTheBoat/Games/" + ExLocation.getUniqueString(locationData.load())+ ".json"), this);
    }

    /**
     * @return Return BattleshipGame of the loaded data.
     */
    @Override
    public BattleshipGame load() {
        try {
            return new BattleshipGame(stageType, locationData.load(), null, Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(pluginName)), false);
        }
        catch (NotEnoughSpaceException e) {
            CatchTheBoat.LOGGER.log(Level.SEVERE, "Cannot Load Battleship game.", e);
            return null;
        }
    }
}
