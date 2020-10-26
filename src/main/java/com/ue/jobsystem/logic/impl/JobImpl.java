package com.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;

import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;

public class JobImpl implements Job {

	private final JobsystemValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final JobDao jobDao;
	private Map<String, Double> entityList = new HashMap<>();
	private Map<String, Double> blockList = new HashMap<>();
	private Map<String, Double> fisherList = new HashMap<>();
	private Map<String, Double> breedableList = new HashMap<>();
	private String name;

	/**
	 * Constructor to create a new or load an existing job.
	 * 
	 * @param generalValidator
	 * @param validationHandler
	 * @param jobDao
	 * @param name
	 * @param isNew
	 */
	public JobImpl(GeneralEconomyValidationHandler generalValidator, JobsystemValidationHandler validationHandler,
			JobDao jobDao, String name, boolean isNew) {
		this.jobDao = jobDao;
		this.validationHandler = validationHandler;
		this.generalValidator = generalValidator;
		jobDao.setupSavefile(name);
		if (isNew) {
			setupJobName(name);
		} else {
			loadExistingJob();
		}
	}

	@Override
	public void addFisherLootType(String lootType, double price) throws GeneralEconomyException {
		validationHandler.checkForValidFisherLootType(lootType);
		generalValidator.checkForValueGreaterZero(price);
		generalValidator.checkForValueNotInList(new ArrayList<>(getFisherList().keySet()), lootType);
		getFisherList().put(lootType, price);
		jobDao.saveFisherList(getFisherList());
	}

	@Override
	public void removeFisherLootType(String lootType) throws GeneralEconomyException {
		validationHandler.checkForValidFisherLootType(lootType);
		generalValidator.checkForValueInList(new ArrayList<>(getFisherList().keySet()), lootType);
		getFisherList().remove(lootType);
		jobDao.saveFisherList(getFisherList());
	}

	@Override
	public void addMob(String entity, double price) throws GeneralEconomyException {
		entity = entity.toUpperCase();
		validationHandler.checkForValidEntityType(entity);
		generalValidator.checkForValueNotInList(new ArrayList<>(getEntityList().keySet()), entity);
		generalValidator.checkForValueGreaterZero(price);
		entityList.put(entity, price);
		jobDao.saveEntityList(getEntityList());
	}

	@Override
	public void deleteMob(String entity) throws GeneralEconomyException {
		entity = entity.toUpperCase();
		validationHandler.checkForValidEntityType(entity);
		generalValidator.checkForValueInList(new ArrayList<>(getEntityList().keySet()), entity);
		entityList.remove(entity);
		jobDao.saveEntityList(getEntityList());
	}

	@Override
	public void addBreedable(EntityType breedable, double price) throws GeneralEconomyException {
		validationHandler.checkForValidBreedableEntity(breedable);
		generalValidator.checkForValueNotInList(new ArrayList<>(getBreedableList().keySet()),
				breedable.toString().toUpperCase());
		generalValidator.checkForValueGreaterZero(price);
		breedableList.put(breedable.toString().toUpperCase(), price);
		jobDao.saveBreedableList(getBreedableList());
	}

	@Override
	public void deleteBreedable(EntityType breedable) throws GeneralEconomyException {
		validationHandler.checkForValidBreedableEntity(breedable);
		generalValidator.checkForValueInList(new ArrayList<>(getBreedableList().keySet()),
				breedable.toString().toUpperCase());
		breedableList.remove(breedable.toString().toUpperCase());
		jobDao.saveBreedableList(getBreedableList());
	}

	@Override
	public void addBlock(String material, double price) throws GeneralEconomyException {
		material = material.toUpperCase();
		validationHandler.checkForValidMaterial(material);
		generalValidator.checkForValueGreaterZero(price);
		generalValidator.checkForValueNotInList(new ArrayList<>(getBlockList().keySet()), material);
		getBlockList().put(material, price);
		jobDao.saveBlockList(getBlockList());
	}

	@Override
	public void deleteBlock(String material) throws GeneralEconomyException {
		material = material.toUpperCase();
		validationHandler.checkForValidMaterial(material);
		generalValidator.checkForValueInList(new ArrayList<>(getBlockList().keySet()), material);
		getBlockList().remove(material);
		jobDao.saveBlockList(getBlockList());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getBreedPrice(EntityType breedable) throws GeneralEconomyException {
		validationHandler.checkForValidBreedableEntity(breedable);
		generalValidator.checkForValueInList(new ArrayList<>(getBreedableList().keySet()),
				breedable.toString().toUpperCase());
		return getBreedableList().get(breedable.toString().toUpperCase());
	}

	@Override
	public double getBlockPrice(String material) throws GeneralEconomyException {
		material = material.toUpperCase();
		validationHandler.checkForValidMaterial(material);
		generalValidator.checkForValueInList(new ArrayList<>(getBlockList().keySet()), material);
		return getBlockList().get(material);
	}

	@Override
	public double getFisherPrice(String lootType) throws GeneralEconomyException {
		validationHandler.checkForValidFisherLootType(lootType);
		generalValidator.checkForValueInList(new ArrayList<>(getFisherList().keySet()), lootType);
		return getFisherList().get(lootType);
	}

	@Override
	public double getKillPrice(String entityName) throws GeneralEconomyException {
		entityName = entityName.toUpperCase();
		validationHandler.checkForValidEntityType(entityName);
		generalValidator.checkForValueInList(new ArrayList<>(getEntityList().keySet()), entityName);
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
	public Map<String, Double> getBreedableList() {
		return breedableList;
	}

	@Override
	public Map<String, Double> getFisherList() {
		return fisherList;
	}

	private void setupJobName(String name) {
		this.name = name;
		jobDao.saveJobName(name);
	}

	private void loadExistingJob() {
		breedableList = jobDao.loadBreedableList();
		fisherList = jobDao.loadFisherList();
		entityList = jobDao.loadEntityList();
		blockList = jobDao.loadBlockList();
		this.name = jobDao.loadJobName();
	}
}
