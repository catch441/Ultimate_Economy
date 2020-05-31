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

    protected JobSavefileHandler(String name, boolean createFile) throws IOException {
	file = new File(UltimateEconomy.getInstance.getDataFolder(), name + "-Job.yml");
	if (createFile) {
	    getSavefile().createNewFile();
	}
	config = YamlConfiguration.loadConfiguration(getSavefile());
    }
    
    protected void saveJobName(String name) {
	getConfig().set("Jobname", name);
	save();
    }

    protected void saveBlockList(Map<String, Double> blockList) {
	getConfig().set("BlockList", null);
	for (String key : blockList.keySet()) {
	    getConfig().set("BlockList." + key, blockList.get(key));
	}
	save();
    }

    protected void saveFisherList(Map<String, Double> fisherList) {
	getConfig().set("FisherList", null);
	for (String key : fisherList.keySet()) {
	    getConfig().set("FisherList." + key, fisherList.get(key));
	}
	save();
    }

    protected void saveEntityList(Map<String, Double> entityList) {
	getConfig().set("EntityList", null);
	for (String key : entityList.keySet()) {
	    getConfig().set("EntityList." + key, entityList.get(key));
	}
	save();
    }
    
	public void saveBreedableList(Map<String, Double> breedableList) {
	getConfig().set("BreedableList", null);
	for (String key : breedableList.keySet()) {
		   getConfig().set("BreedableList." + key, breedableList.get(key));
	}
	save();
	}
    
    protected String loadJobName() {
	return getConfig().getString("Jobname");
    }
    
    protected Map<String, Double> loadBlockList() {
	Map<String, Double> list = new HashMap<>();
	convertBlockList();
	if (getConfig().contains("BlockList")) {
	    for (String key : getConfig().getConfigurationSection("BlockList").getKeys(false)) {
		list.put(key, getConfig().getDouble("BlockList." + key));
	    }
	}
	return list;
    }
    
    protected Map<String, Double> loadEntityList() {
	Map<String, Double> list = new HashMap<>();
	convertEntityList();
	if (getConfig().contains("EntityList")) {
	    for (String key : getConfig().getConfigurationSection("EntityList").getKeys(false)) {
		list.put(key, getConfig().getDouble("EntityList." + key));
	    }
	}
	return list;
    }
    
    protected Map<String, Double> loadBreedableList() {
	Map<String, Double> list = new HashMap<>();
	if (getConfig().contains("BreedableList")) {
	    for (String key : getConfig().getConfigurationSection("BreedableList").getKeys(false)) {
		list.put(key, getConfig().getDouble("BreedableList." + key));
	    }
	}
	return list;
    }
    
    protected Map<String, Double> loadFisherList() {
	Map<String, Double> list = new HashMap<>();
	convertFisherList();
	if (getConfig().contains("FisherList")) {
	    for (String key : getConfig().getConfigurationSection("FisherList").getKeys(false)) {
		list.put(key, getConfig().getDouble("FisherList." + key));
	    }
	}
	return list;
    }
    
    protected void deleteSavefile() {
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
