package com.ue.jobsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.JobExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.api.Job;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobImpl implements Job {

    private Map<String, Double> entityList = new HashMap<>();
    private Map<String, Double> blockList = new HashMap<>();
    private Map<String, Double> fisherList = new HashMap<>();
    private File file;
    private String name;

    /**
     * Constructor to create a new or load a existing job.
     * 
     * @param name
     */
    public JobImpl(String name) {
	file = new File(UltimateEconomy.getInstance.getDataFolder(), name + "-Job.yml");
	if (!getSaveFile().exists()) {
	    setupNewJob(name);
	} else {
	    loadExistingJob();
	}
    }

    @Override
    public void addFisherLootType(String lootType, double price) throws JobSystemException, GeneralEconomyException {
	checkForValidFisherLootType(lootType);
	checkForPositivValue(price);
	checkForLoottypeNotInJob(lootType);
	getFisherList().put(lootType, price);
	saveFisherList();
    }

    @Override
    public void delFisherLootType(String lootType) throws JobSystemException, GeneralEconomyException {
	checkForValidFisherLootType(lootType);
	checkForLoottypeInJob(lootType);
	getFisherList().remove(lootType);
	saveFisherList();
    }

    @Override
    public void addMob(String entity, double price) throws JobSystemException, GeneralEconomyException {
	entity = entity.toUpperCase();
	checkForValidEntityType(entity);
	checkForEntityNotInJob(entity);
	checkForPositivValue(price);
	getEntityList().put(entity, price);
	saveEntityList();
    }

    @Override
    public void deleteMob(String entity) throws JobSystemException, GeneralEconomyException {
	entity = entity.toUpperCase();
	checkForValidEntityType(entity);
	checkForEntityInJob(entity);
	getEntityList().remove(entity);
	saveEntityList();
    }

    @Override
    public void addBlock(String material, double price) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	checkForValidMaterial(material);
	checkForPositivValue(price);
	checkForBlockNotInJob(material);
	getBlockList().put(material, price);
	saveBlockList();
    }

    @Override
    public void deleteBlock(String material) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	checkForValidMaterial(material);
	checkForBlockInJob(material);
	getBlockList().remove(material);
	saveBlockList();
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public double getBlockPrice(String material) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	checkForValidMaterial(material);
	checkForBlockInJob(material);
	return getBlockList().get(material);
    }

    @Override
    public double getFisherPrice(String lootType) throws JobSystemException, GeneralEconomyException {
	checkForValidFisherLootType(lootType);
	checkForLoottypeInJob(lootType);
	return getFisherList().get(lootType);
    }

    @Override
    public double getKillPrice(String entityName) throws JobSystemException, GeneralEconomyException {
	entityName = entityName.toUpperCase();
	checkForValidEntityType(entityName);
	checkForEntityInJob(entityName);
	return getEntityList().get(entityName);
    }

    @Override
    public void deleteJob() {
	getSaveFile().delete();
    }

    @Override
    public Map<String, Double> getBlockList() {
	return blockList;
    }

    @Override
    public Map<String, Double> getEntityList() {
	return entityList;
    }

    @Override
    public Map<String, Double> getFisherList() {
	return fisherList;
    }

    /*
     * Utility methods
     * 
     */

    private File getSaveFile() {
	return file;
    }

    /*
     * Setup methods
     * 
     */

    private void setupNewJob(String name) {
	try {
	    getSaveFile().createNewFile();
	    setupJobName(name);
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    private void setupJobName(String name) {
	this.name = name;
	saveJobName();
    }

    /*
     * Loading methods
     * 
     */

    private void loadExistingJob() {
	convertBlockList();
	convertEntityList();
	convertFisherList();
	loadBlockList();
	loadEntityList();
	loadFisherList();
	loadJobName();
    }

    private void loadFisherList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	for (String key : config.getConfigurationSection("FisherList").getKeys(false)) {
	    getFisherList().put(key, config.getDouble("FisherList." + key));
	}
    }

    private void loadEntityList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	for (String key : config.getConfigurationSection("EntityList").getKeys(false)) {
	    getEntityList().put(key, config.getDouble("EntityList." + key));
	}
    }

    private void loadBlockList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	for (String key : config.getConfigurationSection("BlockList").getKeys(false)) {
	    getBlockList().put(key, config.getDouble("BlockList." + key));
	}
    }

    private void loadJobName() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	name = config.getString("Jobname");
    }

    /*
     * Save methods
     * 
     */

    private void saveJobName() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("Jobname", getName());
	save(config);
    }

    private void saveBlockList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	for (String key : getBlockList().keySet()) {
	    config.set("BlockList." + key, getBlockList().get(key));
	}
	save(config);
    }

    private void saveFisherList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	for (String key : getFisherList().keySet()) {
	    config.set("FisherList." + key, getFisherList().get(key));
	}
	save(config);
    }

    private void saveEntityList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	for (String key : getEntityList().keySet()) {
	    config.set("EntityList." + key, getEntityList().get(key));
	}
	save(config);
    }

    private void save(YamlConfiguration config) {
	try {
	    config.save(getSaveFile());
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Error an save config to file");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    /*
     * Validation methods
     * 
     */

    private void checkForValidMaterial(String material) throws GeneralEconomyException {
	if (Material.matchMaterial(material) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, material);
	}
    }

    private void checkForValidEntityType(String entityName) throws GeneralEconomyException {
	try {
	    EntityType.valueOf(entityName);
	} catch (IllegalArgumentException e) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
		    entityName);
	}
    }

    private void checkForPositivValue(double value) throws GeneralEconomyException {
	if (value <= 0.0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	}
    }

    private void checkForValidFisherLootType(String lootType) throws GeneralEconomyException {
	if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, lootType);
	}
    }

    private void checkForBlockNotInJob(String material) throws JobSystemException {
	if (getBlockList().containsKey(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.BLOCK_ALREADY_EXISTS);
	}
    }

    private void checkForBlockInJob(String material) throws JobSystemException {
	if (!getBlockList().containsKey(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.BLOCK_DOES_NOT_EXIST);
	}
    }

    private void checkForLoottypeNotInJob(String lootType) throws JobSystemException {
	if (getFisherList().containsKey(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_ALREADY_EXISTS);
	}
    }

    private void checkForLoottypeInJob(String lootType) throws JobSystemException {
	if (!getFisherList().containsKey(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_DOES_NOT_EXIST);
	}
    }

    private void checkForEntityNotInJob(String entity) throws JobSystemException {
	if (getEntityList().containsKey(entity)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_ALREADY_EXISTS);
	}
    }

    private void checkForEntityInJob(String entityName) throws JobSystemException {
	if (!getEntityList().containsKey(entityName)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_DOES_NOT_EXIST);
	}
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
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	if (config.contains("JobItems")) {
	    for (String key : config.getStringList("Itemlist")) {
		double value = config.getDouble("JobItems." + key);
		getBlockList().put(key, value);
	    }
	    saveBlockList();
	    config.set("JobItems", null);
	    config.set("Itemlist", null);
	}
	saveBlockList();
    }

    /**
     * @since 1.2.6
     * @deprecated can be removed later
     */
    @Deprecated
    private void convertEntityList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	if (config.contains("JobEntitys")) {
	    for (String key : config.getStringList("Entitylist")) {
		double value = config.getDouble("JobEntitys." + key);
		getEntityList().put(key, value);
	    }
	    saveEntityList();
	    config.set("JobEntitys", null);
	    config.set("Entitylist", null);
	}
    }

    /**
     * @since 1.2.6
     * @deprecated can be removed later
     */
    @Deprecated
    private void convertFisherList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	if (config.contains("Fisher")) {
	    for (String key : config.getStringList("Fisherlist")) {
		double value = config.getDouble("Fisher." + key);
		getFisherList().put(key, value);
	    }
	    saveFisherList();
	    config.set("Fisher", null);
	    config.set("Fisherlist", null);
	}
    }
}
