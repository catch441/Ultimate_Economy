package org.ue.jobsystem.dataaccess.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.configuration.file.YamlConfiguration;
import org.ue.common.utils.SaveFileUtils;
import org.ue.common.utils.ServerProvider;
import org.ue.jobsystem.dataaccess.api.JobDao;

public class JobDaoImpl extends SaveFileUtils implements JobDao {

	private final ServerProvider serverProvider;
	
	@Inject
	public JobDaoImpl(ServerProvider serverProvider) {
		this.serverProvider = serverProvider;
	}

	@Override
	public void setupSavefile(String name) {
		file = new File(serverProvider.getDataFolderPath(), name + "-Job.yml");
		if (!file.exists()) {
			createFile(file);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public void saveJobName(String name) {
		config.set("Jobname", name);
		save();
	}

	@Override
	public void saveBlockList(Map<String, Double> blockList) {
		config.set("BlockList", null);
		for (String key : blockList.keySet()) {
			config.set("BlockList." + key, blockList.get(key));
		}
		save();
	}

	@Override
	public void saveFisherList(Map<String, Double> fisherList) {
		config.set("FisherList", null);
		for (String key : fisherList.keySet()) {
			config.set("FisherList." + key, fisherList.get(key));
		}
		save();
	}

	@Override
	public void saveEntityList(Map<String, Double> entityList) {
		config.set("EntityList", null);
		for (String key : entityList.keySet()) {
			config.set("EntityList." + key, entityList.get(key));
		}
		save();
	}
	
	@Override
	public void saveBreedableList(Map<String, Double> breedableList) {
		config.set("BreedableList", null);
		for (String key : breedableList.keySet()) {
			config.set("BreedableList." + key, breedableList.get(key));
		}
		save();
	}

	@Override
	public String loadJobName() {
		return config.getString("Jobname");
	}

	@Override
	public Map<String, Double> loadBlockList() {
		Map<String, Double> list = new HashMap<>();
		convertBlockList();
		if (config.contains("BlockList")) {
			for (String key : config.getConfigurationSection("BlockList").getKeys(false)) {
				list.put(key, config.getDouble("BlockList." + key));
			}
		}
		return list;
	}
	
	@Override
	public Map<String, Double> loadBreedableList() {
		Map<String, Double> list = new HashMap<>();
		if (config.contains("BreedableList")) {
			for (String key : config.getConfigurationSection("BreedableList").getKeys(false)) {
				list.put(key, config.getDouble("BreedableList." + key));
			}
		}
		return list;
	}

	@Override
	public Map<String, Double> loadEntityList() {
		Map<String, Double> list = new HashMap<>();
		convertEntityList();
		if (config.contains("EntityList")) {
			for (String key : config.getConfigurationSection("EntityList").getKeys(false)) {
				list.put(key, config.getDouble("EntityList." + key));
			}
		}
		return list;
	}

	@Override
	public Map<String, Double> loadFisherList() {
		Map<String, Double> list = new HashMap<>();
		convertFisherList();
		if (config.contains("FisherList")) {
			for (String key : config.getConfigurationSection("FisherList").getKeys(false)) {
				list.put(key, config.getDouble("FisherList." + key));
			}
		}
		return list;
	}

	@Override
	public void deleteSavefile() {
		file.delete();
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
		if (config.contains("JobItems")) {
			for (String key : config.getStringList("Itemlist")) {
				double value = config.getDouble("JobItems." + key + ".breakprice");
				list.put(key, value);
			}
			config.set("JobItems", null);
			config.set("Itemlist", null);
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
		if (config.contains("JobEntitys")) {
			for (String key : config.getStringList("Entitylist")) {
				double value = config.getDouble("JobEntitys." + key + ".killprice");
				list.put(key, value);
			}
			config.set("JobEntitys", null);
			config.set("Entitylist", null);
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
		if (config.contains("Fisher")) {
			for (String key : config.getStringList("Fisherlist")) {
				double value = config.getDouble("Fisher." + key);
				list.put(key, value);
			}
			config.set("Fisher", null);
			config.set("Fisherlist", null);
			saveFisherList(list);
		}
	}
}
