package com.ue.jobsystem.dataaccese.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.common.utils.SaveFileUtils;
import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobDaoImpl extends SaveFileUtils implements JobDao {

	private File file;
	private YamlConfiguration config;

	/**
	 * Constructor for creating a new SavefileHandler.
	 * @param name
	 * @param createFile if true, then a new save file is create, else a existing file gets loaded
	 */
	public JobDaoImpl(String name, boolean createFile) {
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

	@Override
	public void saveJobName(String name) {
		getConfig().set("Jobname", name);
		save(getConfig(),getSavefile());
	}

	@Override
	public void saveBlockList(Map<String, Double> blockList) {
		getConfig().set("BlockList", null);
		for (String key : blockList.keySet()) {
			getConfig().set("BlockList." + key, blockList.get(key));
		}
		save(getConfig(),getSavefile());
	}

	@Override
	public void saveFisherList(Map<String, Double> fisherList) {
		getConfig().set("FisherList", null);
		for (String key : fisherList.keySet()) {
			getConfig().set("FisherList." + key, fisherList.get(key));
		}
		save(getConfig(),getSavefile());
	}

	@Override
	public void saveEntityList(Map<String, Double> entityList) {
		getConfig().set("EntityList", null);
		for (String key : entityList.keySet()) {
			getConfig().set("EntityList." + key, entityList.get(key));
		}
		save(getConfig(),getSavefile());
	}

	@Override
	public String loadJobName() {
		return getConfig().getString("Jobname");
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
	public void deleteSavefile() {
		getSavefile().delete();
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
				double value = getConfig().getDouble("JobItems." + key + ".breakprice");
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
				double value = getConfig().getDouble("JobEntitys." + key + ".killprice");
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
