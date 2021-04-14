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
		config = YamlConfiguration.loadConfiguration(getSavefile());
	}

	@Override
	public void saveJobName(String name) {
		getConfig().set("Jobname", name);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveBlockList(Map<String, Double> blockList) {
		getConfig().set("BlockList", null);
		for (String key : blockList.keySet()) {
			getConfig().set("BlockList." + key, blockList.get(key));
		}
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveFisherList(Map<String, Double> fisherList) {
		getConfig().set("FisherList", null);
		for (String key : fisherList.keySet()) {
			getConfig().set("FisherList." + key, fisherList.get(key));
		}
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveEntityList(Map<String, Double> entityList) {
		getConfig().set("EntityList", null);
		for (String key : entityList.keySet()) {
			getConfig().set("EntityList." + key, entityList.get(key));
		}
		save(getConfig(), getSavefile());
	}
	
	@Override
	public void saveBreedableList(Map<String, Double> breedableList) {
		getConfig().set("BreedableList", null);
		for (String key : breedableList.keySet()) {
			getConfig().set("BreedableList." + key, breedableList.get(key));
		}
		save(getConfig(), getSavefile());
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
	public Map<String, Double> loadBreedableList() {
		Map<String, Double> list = new HashMap<>();
		if (getConfig().contains("BreedableList")) {
			for (String key : getConfig().getConfigurationSection("BreedableList").getKeys(false)) {
				list.put(key, getConfig().getDouble("BreedableList." + key));
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
