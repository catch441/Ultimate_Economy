package com.ue.common.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

public class SaveFileUtils {

	protected final Logger logger;
	protected File file;
	protected YamlConfiguration config;

	/**
	 * Inject Constructor.
	 * 
	 * @param logger
	 */
	public SaveFileUtils(Logger logger) {
		this.logger = logger;
	}

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
			logger.warn("[Ultimate_Economy] Error on save config to file");
			logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
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
			logger.warn("[Ultimate_Economy] Failed to create savefile");
			logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}
}
