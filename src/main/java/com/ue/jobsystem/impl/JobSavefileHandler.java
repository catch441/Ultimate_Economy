package com.ue.jobsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.ultimate_economy.UltimateEconomy;

public class JobSavefileHandler {

	private File file;
	private YamlConfiguration config;

	/**
	 * Constructor for creating a new SavefileHandler.
	 * @param name
	 * @param createFile if true, then a new save file is create, else a existing file gets loaded
	 */
	public JobSavefileHandler(String name, boolean createFile) {
		file = new File(UltimateEconomy.getInstance.getDataFolder(), name + "-Job.yml");
		if (createFile) {
			try {
				getSavefile().createNewFile();
			} catch (IOException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create job :" + name);
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		config = YamlConfiguration.loadConfiguration(getSavefile());
	}

	/**
	 * Saves a job name.
	 * @param name
	 */
	public void saveJobName(String name) {
		getConfig().set("Jobname", name);
		save();
	}

	/**
	 * Saves the block list (map) for a job.
	 * @param blockList
	 */
	public void saveBlockList(Map<String, Double> blockList) {
		getConfig().set("BlockList", null);
		for (String key : blockList.keySet()) {
			getConfig().set("BlockList." + key, blockList.get(key));
		}
		save();
	}

	/**
	 * Saves the fisher list (map) for a job.
	 * @param fisherList
	 */
	public void saveFisherList(Map<String, Double> fisherList) {
		getConfig().set("FisherList", null);
		for (String key : fisherList.keySet()) {
			getConfig().set("FisherList." + key, fisherList.get(key));
		}
		save();
	}

	/**
	 * Saves the entity list (map) for a job.
	 * @param entityList
	 */
	public void saveEntityList(Map<String, Double> entityList) {
		getConfig().set("EntityList", null);
		for (String key : entityList.keySet()) {
			getConfig().set("EntityList." + key, entityList.get(key));
		}
		save();
	}

	/**
	 * Loads the job name.
	 * @return job name as String
	 */
	public String loadJobName() {
		return getConfig().getString("Jobname");
	}

	/**
	 * Loads the block list (map) of a job.
	 * @return block list as Map
	 */
	public Map<String, Double> loadBlockList() {
		Map<String, Double> list = new HashMap<>();
		convertBlockList();
		if (getConfig().contains("BlockList")) {
			for (String key : getConfig().getConfigurationSection("BlockList").getKeys(false)) {
				list.put(key, getConfig().getDouble("BlockList." + key));
			}
		}
		return list;
	}

	/**
	 * Loads the entity list (map) of a job.
	 * @return entity list as Map
	 */
	public Map<String, Double> loadEntityList() {
		Map<String, Double> list = new HashMap<>();
		convertEntityList();
		if (getConfig().contains("EntityList")) {
			for (String key : getConfig().getConfigurationSection("EntityList").getKeys(false)) {
				list.put(key, getConfig().getDouble("EntityList." + key));
			}
		}
		return list;
	}

	/**
	 * Loads the fisher list (map) of a job.
	 * @return fisher list as Map
	 */
	public Map<String, Double> loadFisherList() {
		Map<String, Double> list = new HashMap<>();
		convertFisherList();
		if (getConfig().contains("FisherList")) {
			for (String key : getConfig().getConfigurationSection("FisherList").getKeys(false)) {
				list.put(key, getConfig().getDouble("FisherList." + key));
			}
		}
		return list;
	}

	/**
	 * Deletes the savefile.
	 */
	public void deleteSavefile() {
		getSavefile().delete();
	}

	private void save() {
		try {
			getConfig().save(getSavefile());
		} catch (IOException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] Error an save config to file");
			Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}

	private File getSavefile() {
		return file;
	}

	private YamlConfiguration getConfig() {
		return config;
	}

	/*
	 * Deprecated
	 * 
	 */

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertBlockList() {
		Map<String, Double> list = new HashMap<>();
		if (getConfig().contains("JobItems")) {
			for (String key : getConfig().getStringList("Itemlist")) {
				double value = getConfig().getDouble("JobItems." + key);
				list.put(key, value);
			}
			getConfig().set("JobItems", null);
			getConfig().set("Itemlist", null);
			saveBlockList(list);
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertEntityList() {
		Map<String, Double> list = new HashMap<>();
		if (getConfig().contains("JobEntitys")) {
			for (String key : getConfig().getStringList("Entitylist")) {
				double value = getConfig().getDouble("JobEntitys." + key);
				list.put(key, value);
			}
			getConfig().set("JobEntitys", null);
			getConfig().set("Entitylist", null);
			saveEntityList(list);
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertFisherList() {
		Map<String, Double> list = new HashMap<>();
		if (getConfig().contains("Fisher")) {
			for (String key : getConfig().getStringList("Fisherlist")) {
				double value = getConfig().getDouble("Fisher." + key);
				list.put(key, value);
			}
			getConfig().set("Fisher", null);
			getConfig().set("Fisherlist", null);
			saveFisherList(list);
		}
	}
}
