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
	public static void save(YamlConfiguration config, File savefile) {
        try {
        	config.save(savefile);
        } catch (IOException e) {
            Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
            Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
        }
    }
}
