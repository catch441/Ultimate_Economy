package com.ue.common.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class SaveFileUtils {

	/**
	 * Saves a configuration into the file.
	 * @param config
	 * @param savefile
	 */
	public void save(YamlConfiguration config, File savefile) {
        try {
        	config.save(savefile);
        } catch (IOException e) {
            Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
            Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
        }
    }
	
	public void createFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
			Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}
}
