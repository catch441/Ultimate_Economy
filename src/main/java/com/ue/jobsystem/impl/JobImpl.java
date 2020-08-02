package com.ue.jobsystem.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.dataaccese.impl.JobDaoImpl;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.jobsystem.logic.impl.JobsystemValidationHandlerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobImpl implements Job {

	private Map<String, Double> entityList = new HashMap<>();
	private Map<String, Double> blockList = new HashMap<>();
	private Map<String, Double> fisherList = new HashMap<>();
	private String name;
	private JobsystemValidationHandlerImpl validationHandler;
	private JobDaoImpl savefileHandler;

	/**
	 * Constructor to create a new or load a existing job.
	 * 
	 * @param name
	 */
	public JobImpl(String name) {
		validationHandler = new JobsystemValidationHandlerImpl();
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

	/*
	 * Utility methods
	 * 
	 */

	private JobsystemValidationHandlerImpl getValidationHandler() {
		return validationHandler;
	}

	private JobDaoImpl getSavefileHandler() {
		return savefileHandler;
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewJob(String name) {
		savefileHandler = new JobDaoImpl(name, true);
		setupJobName(name);
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
		savefileHandler = new JobDaoImpl(name, false);
		fisherList = getSavefileHandler().loadFisherList();
		entityList = getSavefileHandler().loadEntityList();
		blockList = getSavefileHandler().loadBlockList();
		this.name = getSavefileHandler().loadJobName();
	}
}
