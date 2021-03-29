package com.github.militaermilitz;

import com.github.militaermilitz.command.BattleshipCommand;
import com.github.militaermilitz.util.FileUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

/**
 * @author Alexander Ley
 * @version 1.1
 *
 * Plugin Entry Class. This class register needed Staff.
 *
 */
public final class CatchTheBoat extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register Commands
        try {
            unzipStructures();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        getCommand("battleship").setExecutor(new BattleshipCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

    //Creates new File.
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
