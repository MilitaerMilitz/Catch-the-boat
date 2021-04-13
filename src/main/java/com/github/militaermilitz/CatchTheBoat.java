package com.github.militaermilitz;

import com.github.militaermilitz.battleship.BattleshipGame;
import com.github.militaermilitz.battleship.BattleshipGameFile;
import com.github.militaermilitz.battleship.engine.GameEvents;
import com.github.militaermilitz.command.BattleshipCommand;
import com.github.militaermilitz.chestGui.ChestGuiEvents;
import com.github.militaermilitz.mcUtil.ExLocation;
import com.github.militaermilitz.util.FileUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Alexander Ley
 * @version 1.3
 * Plugin Entry Class. This class register needed Staff.
 */
public final class CatchTheBoat extends JavaPlugin {
    public static Logger LOGGER;

    @Override
    public void onEnable() {
        LOGGER = this.getLogger();

        //Try to load saved battleship Games from file via gson.
        try {
            loadGameAssets();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Register Commands and Event Listerner
        try {
            unzipStructures();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(new ChestGuiEvents(), this);
        getServer().getPluginManager().registerEvents(new GameEvents(), this);
        Objects.requireNonNull(getCommand("battleship")).setExecutor(new BattleshipCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveGameAssets();
    }

    /**
     * Saves the battleship Game Instances and stop all running games.
     */
    public void saveGameAssets(){
        //Delete old game files.
        final Path gamesDir = Paths.get("plugins/CatchTheBoat/Games");
        if (Files.exists(gamesDir)) FileUtil.deleteDirectoryWithContent(gamesDir.toFile());

        //Create new game files
        BattleshipGame.GAMES.forEach((location, game) -> {
            game.exitGame();
            new BattleshipGameFile(game).saveToFile();
        });
    }

    /**
     * Load Game Instance from File..
     */
    public void loadGameAssets() throws IOException {
        final Path gameDir = Paths.get("plugins/CatchTheBoat/Games");

        if (Files.exists(gameDir)){
            Files.list(gameDir).forEach(path -> {
                final BattleshipGameFile gameFile = FileUtil.loadFromJson(path, BattleshipGameFile.class);

                if (gameFile != null){
                    final BattleshipGame game = gameFile.load();
                    BattleshipGame.GAMES.put(ExLocation.getUniqueString(game.getLoc()), game);
                    game.stop();
                }
            });
        }
    }

    /**
     * Extract structure files from plugin.
     */
    private void unzipStructures() throws IOException {
        //Test if structures already exists
        Path struct1 = Paths.get("plugins/CatchTheBoat/structures/battleship_big_1.nbt");
        if (Files.exists(struct1)) return;

        //Source jar File
        String fileZip = this.getFile().getAbsolutePath();

        //Output directory
        File destDir = new File("plugins/CatchTheBoat");

        //Create Stream
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();

        //Write only .nbt files to directory
        byte[] buffer = new byte[1024];
        while (zipEntry != null) {

            //Creates a new destination file where the bytes are copied in
            File newFile = newFile(destDir, zipEntry);

            if (zipEntry.toString().endsWith(".nbt")) {
                //Create Directories above the .nbt files
                Files.createDirectories(newFile.getParentFile().toPath());

                //Write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    /**
     * Creates new File at the destinationDir/zipEntry.
     * @throws IOException If entry is outside of the target directory.
     */
    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        //Creates destination File
        File destFile = new File(destinationDir, zipEntry.getName());

        //Checks if Entry is outside of the target dir
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
