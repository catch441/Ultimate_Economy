package com.ue.jobsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.api.Job;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobImpl implements Job {

    private Map<String, Double> entityList = new HashMap<>();
    private Map<String, Double> blockList = new HashMap<>();
    private Map<String, Double> fisherList = new HashMap<>();
    private Map<String, Double> breedableList = new HashMap<>();
    private String name;
    private JobSystemValidationHandler validationHandler;
    private JobSavefileHandler savefileHandler;

    /**
     * Constructor to create a new or load a existing job.
     * 
     * @param name
     */
    public JobImpl(String name) {
	validationHandler = new JobSystemValidationHandler();
	if (!new File(UltimateEconomy.getInstance.getDataFolder(), name + "-Job.yml").exists()) {
	    setupNewJob(name);
	} else {
	    loadExistingJob(name);
	}
    }

    @Override
    public void addFisherLootType(String lootType, double price) throws JobSystemException, GeneralEconomyException {
	getValidationHandler().checkForValidFisherLootType(lootType);
	getValidationHandler().checkForPositivValue(price);
	getValidationHandler().checkForLoottypeNotInJob(getFisherList(), lootType);
	getFisherList().put(lootType, price);
	getSavefileHandler().saveFisherList(getFisherList());
    }

    @Override
    public void delFisherLootType(String lootType) throws JobSystemException, GeneralEconomyException {
	getValidationHandler().checkForValidFisherLootType(lootType);
	getValidationHandler().checkForLoottypeInJob(getFisherList(), lootType);
	getFisherList().remove(lootType);
	getSavefileHandler().saveFisherList(getFisherList());
    }

    @Override
    public void addMob(String entity, double price) throws JobSystemException, GeneralEconomyException {
	entity = entity.toUpperCase();
	getValidationHandler().checkForValidEntityType(entity);
	getValidationHandler().checkForEntityNotInJob(getEntityList(), entity);
	getValidationHandler().checkForPositivValue(price);
	getEntityList().put(entity, price);
	getSavefileHandler().saveEntityList(getEntityList());
    }

    @Override
    public void deleteMob(String entity) throws JobSystemException, GeneralEconomyException {
	entity = entity.toUpperCase();
	getValidationHandler().checkForValidEntityType(entity);
	getValidationHandler().checkForEntityInJob(getEntityList(), entity);
	getEntityList().remove(entity);
	getSavefileHandler().saveEntityList(getEntityList());
    }

    @Override
    public void addBlock(String material, double price) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	getValidationHandler().checkForValidMaterial(material);
	getValidationHandler().checkForPositivValue(price);
	getValidationHandler().checkForBlockNotInJob(getBlockList(), material);
	getBlockList().put(material, price);
	getSavefileHandler().saveBlockList(getBlockList());
    }

    @Override
    public void deleteBlock(String material) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	getValidationHandler().checkForValidMaterial(material);
	getValidationHandler().checkForBlockInJob(getBlockList(), material);
	getBlockList().remove(material);
	getSavefileHandler().saveBlockList(getBlockList());
    }
    
	@Override
	public void addBreedable(String entity, double price) throws JobSystemException, GeneralEconomyException {
		entity = entity.toUpperCase();
		getValidationHandler().checkForValidEntityType(entity);
		getValidationHandler().checkForEntityNotInJob(getBreedableList(), entity);
		getValidationHandler().checkForPositivValue(price);
		getBreedableList().put(entity, price);
		getSavefileHandler().saveBreedableList(getBreedableList());
	}
    
	@Override
	public void deleteBreedable(String entity) throws JobSystemException, GeneralEconomyException {
		entity = entity.toUpperCase();
		getValidationHandler().checkForValidEntityType(entity);
		getValidationHandler().checkForEntityInJob(getEntityList(), entity);
		getBreedableList().remove(entity);
		getSavefileHandler().saveBreedableList(getBreedableList());
	}

    @Override
    public String getName() {
	return name;
    }

    @Override
    public double getBlockPrice(String material) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	getValidationHandler().checkForValidMaterial(material);
	getValidationHandler().checkForBlockInJob(getBlockList(), material);
	return getBlockList().get(material);
    }

    @Override
    public double getFisherPrice(String lootType) throws JobSystemException, GeneralEconomyException {
	getValidationHandler().checkForValidFisherLootType(lootType);
	getValidationHandler().checkForLoottypeInJob(getFisherList(), lootType);
	return getFisherList().get(lootType);
    }

    @Override
    public double getKillPrice(String entityName) throws JobSystemException, GeneralEconomyException {
	entityName = entityName.toUpperCase();
	getValidationHandler().checkForValidEntityType(entityName);
	getValidationHandler().checkForEntityInJob(getEntityList(), entityName);
	return getEntityList().get(entityName);
    }
    
	@Override
	public double getBreedablePrice(String entityName) throws JobSystemException, GeneralEconomyException {
		entityName = entityName.toUpperCase();
		getValidationHandler().checkForValidEntityType(entityName);
		getValidationHandler().checkForEntityInJob(getBreedableList(), entityName);
		return getBreedableList().get(entityName);
	}

    @Override
    public void deleteJob() {
	getSavefileHandler().deleteSavefile();
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

    @Override
    public Map<String, Double> getBreedableList() {
	return breedableList;
    }
    
    /*
     * Utility methods
     * 
     */

    private JobSystemValidationHandler getValidationHandler() {
	return validationHandler;
    }
    
    private JobSavefileHandler getSavefileHandler() {
	return savefileHandler;
    }

    /*
     * Setup methods
     * 
     */

    private void setupNewJob(String name) {
	try {
	    savefileHandler = new JobSavefileHandler(name, true);
	    setupJobName(name);
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create job :" + name);
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    private void setupJobName(String name) {
	this.name = name;
	getSavefileHandler().saveJobName(name);
    }

    /*
     * Loading methods
     * 
     */

    private void loadExistingJob(String name) {
	try {
	    savefileHandler = new JobSavefileHandler(name, false);
	    fisherList = getSavefileHandler().loadFisherList();
	    entityList = getSavefileHandler().loadEntityList();
	    blockList = getSavefileHandler().loadBlockList();
	    breedableList = getSavefileHandler().loadBreedableList();
	    this.name = getSavefileHandler().loadJobName();
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load job :" + name);
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }
}
