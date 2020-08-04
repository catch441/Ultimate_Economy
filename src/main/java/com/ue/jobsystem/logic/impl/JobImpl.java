package com.ue.jobsystem.logic.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsystem.dataaccese.impl.JobDaoImpl;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobImpl implements Job {

	@Inject
	JobsystemValidationHandler validationHandler;
	private Map<String, Double> entityList = new HashMap<>();
	private Map<String, Double> blockList = new HashMap<>();
	private Map<String, Double> fisherList = new HashMap<>();
	private String name;
	private JobDao jobDao;

	/**
	 * Constructor to create a new or load a existing job.
	 * 
	 * @param name
	 */
	public JobImpl(String name) {
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
		jobDao.saveFisherList(getFisherList());
	}

	@Override
	public void delFisherLootType(String lootType) throws JobSystemException, GeneralEconomyException {
		getValidationHandler().checkForValidFisherLootType(lootType);
		getValidationHandler().checkForLoottypeInJob(getFisherList(), lootType);
		getFisherList().remove(lootType);
		jobDao.saveFisherList(getFisherList());
	}

	@Override
	public void addMob(String entity, double price) throws JobSystemException, GeneralEconomyException {
		entity = entity.toUpperCase();
		getValidationHandler().checkForValidEntityType(entity);
		getValidationHandler().checkForEntityNotInJob(getEntityList(), entity);
		getValidationHandler().checkForPositivValue(price);
		getEntityList().put(entity, price);
		jobDao.saveEntityList(getEntityList());
	}

	@Override
	public void deleteMob(String entity) throws JobSystemException, GeneralEconomyException {
		entity = entity.toUpperCase();
		getValidationHandler().checkForValidEntityType(entity);
		getValidationHandler().checkForEntityInJob(getEntityList(), entity);
		getEntityList().remove(entity);
		jobDao.saveEntityList(getEntityList());
	}

	@Override
	public void addBlock(String material, double price) throws JobSystemException, GeneralEconomyException {
		material = material.toUpperCase();
		getValidationHandler().checkForValidMaterial(material);
		getValidationHandler().checkForPositivValue(price);
		getValidationHandler().checkForBlockNotInJob(getBlockList(), material);
		getBlockList().put(material, price);
		jobDao.saveBlockList(getBlockList());
	}

	@Override
	public void deleteBlock(String material) throws JobSystemException, GeneralEconomyException {
		material = material.toUpperCase();
		getValidationHandler().checkForValidMaterial(material);
		getValidationHandler().checkForBlockInJob(getBlockList(), material);
		getBlockList().remove(material);
		jobDao.saveBlockList(getBlockList());
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
		jobDao.deleteSavefile();
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

	private JobsystemValidationHandler getValidationHandler() {
		return validationHandler;
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewJob(String name) {
		jobDao = new JobDaoImpl(name, true);
		setupJobName(name);
	}

	private void setupJobName(String name) {
		this.name = name;
		jobDao.saveJobName(name);
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingJob(String name) {
		jobDao = new JobDaoImpl(name, false);
		fisherList = jobDao.loadFisherList();
		entityList = jobDao.loadEntityList();
		blockList = jobDao.loadBlockList();
		this.name = jobDao.loadJobName();
	}
}
