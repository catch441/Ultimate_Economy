package com.ue.common.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveFileUtils {

	private static final Logger log = LoggerFactory.getLogger(SaveFileUtils.class);
	protected File file;
	protected YamlConfiguration config;

	/**
	 * Saves a configuration into the file.
	 * 
	 * @param config
	 * @param savefile
	 */
	public void save(YamlConfiguration config, File savefile) {
		try {
			config.save(savefile);
		} catch (IOException e) {
			log.warn("[Ultimate_Economy] Error on save config to file");
			log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}

	/**
	 * Creates a new file.
	 * 
	 * @param file
	 */
	public void createFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			log.warn("[Ultimate_Economy] Failed to create savefile");
			log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}
}
