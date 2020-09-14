package com.ue.jobsystem.logic.impl;

import java.util.HashMap;
import java.util.Map;

import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;

public class JobImpl implements Job {

	private final JobsystemValidationHandler validationHandler;
	private final JobDao jobDao;
	private Map<String, Double> entityList = new HashMap<>();
	private Map<String, Double> blockList = new HashMap<>();
	private Map<String, Double> fisherList = new HashMap<>();
	private String name;

	/**
	 * Constructor to create a new or load an existing job.
	 * 
	 * @param name
	 * @param isNew
	 */
	public JobImpl(JobsystemValidationHandler validationHandler, JobDao jobDao, String name, boolean isNew) {
		this.jobDao = jobDao;
		this.validationHandler = validationHandler;
		jobDao.setupSavefile(name);
		if (isNew) {
			setupJobName(name);
		} else {
			loadExistingJob(name);
		}
	}

	@Override
	public void addFisherLootType(String lootType, double price) throws JobSystemException, GeneralEconomyException {
		validationHandler.checkForValidFisherLootType(lootType);
		validationHandler.checkForPositivValue(price);
		validationHandler.checkForLoottypeNotInJob(getFisherList(), lootType);
		getFisherList().put(lootType, price);
		jobDao.saveFisherList(getFisherList());
	}

	@Override
	public void delFisherLootType(String lootType) throws JobSystemException, GeneralEconomyException {
		validationHandler.checkForValidFisherLootType(lootType);
		validationHandler.checkForLoottypeInJob(getFisherList(), lootType);
		getFisherList().remove(lootType);
		jobDao.saveFisherList(getFisherList());
	}

	@Override
	public void addMob(String entity, double price) throws JobSystemException, GeneralEconomyException {
		entity = entity.toUpperCase();
		validationHandler.checkForValidEntityType(entity);
		validationHandler.checkForEntityNotInJob(getEntityList(), entity);
		validationHandler.checkForPositivValue(price);
		getEntityList().put(entity, price);
		jobDao.saveEntityList(getEntityList());
	}

	@Override
	public void deleteMob(String entity) throws JobSystemException, GeneralEconomyException {
		entity = entity.toUpperCase();
		validationHandler.checkForValidEntityType(entity);
		validationHandler.checkForEntityInJob(getEntityList(), entity);
		getEntityList().remove(entity);
		jobDao.saveEntityList(getEntityList());
	}

	@Override
	public void addBlock(String material, double price) throws JobSystemException, GeneralEconomyException {
		material = material.toUpperCase();
		validationHandler.checkForValidMaterial(material);
		validationHandler.checkForPositivValue(price);
		validationHandler.checkForBlockNotInJob(getBlockList(), material);
		getBlockList().put(material, price);
		jobDao.saveBlockList(getBlockList());
	}

	@Override
	public void deleteBlock(String material) throws JobSystemException, GeneralEconomyException {
		material = material.toUpperCase();
		validationHandler.checkForValidMaterial(material);
		validationHandler.checkForBlockInJob(getBlockList(), material);
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
		validationHandler.checkForValidMaterial(material);
		validationHandler.checkForBlockInJob(getBlockList(), material);
		return getBlockList().get(material);
	}

	@Override
	public double getFisherPrice(String lootType) throws JobSystemException, GeneralEconomyException {
		validationHandler.checkForValidFisherLootType(lootType);
		validationHandler.checkForLoottypeInJob(getFisherList(), lootType);
		return getFisherList().get(lootType);
	}

	@Override
	public double getKillPrice(String entityName) throws JobSystemException, GeneralEconomyException {
		entityName = entityName.toUpperCase();
		validationHandler.checkForValidEntityType(entityName);
		validationHandler.checkForEntityInJob(getEntityList(), entityName);
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

	private void setupJobName(String name) {
		this.name = name;
		jobDao.saveJobName(name);
	}

	private void loadExistingJob(String name) {
		fisherList = jobDao.loadFisherList();
		entityList = jobDao.loadEntityList();
		blockList = jobDao.loadBlockList();
		this.name = jobDao.loadJobName();
	}
}
