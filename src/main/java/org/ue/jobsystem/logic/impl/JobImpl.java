package org.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.ue.jobsystem.dataaccess.api.JobDao;
import org.ue.jobsystem.logic.api.FishingLootTypeEnum;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidator;

public class JobImpl implements Job {

	protected static final List<EntityType> breedableMobs = Arrays.asList(EntityType.BEE, EntityType.COW, EntityType.HOGLIN,
			EntityType.MUSHROOM_COW, EntityType.PIG, EntityType.SHEEP, EntityType.WOLF, EntityType.CAT,
			EntityType.DONKEY, EntityType.HORSE, EntityType.OCELOT, EntityType.POLAR_BEAR, EntityType.TURTLE,
			EntityType.CHICKEN, EntityType.FOX, EntityType.LLAMA, EntityType.PANDA, EntityType.RABBIT,
			EntityType.VILLAGER);
	private final JobsystemValidator validationHandler;
	private final JobDao jobDao;
	private Map<String, Double> entityList = new HashMap<>();
	private Map<String, Double> blockList = new HashMap<>();
	private Map<String, Double> fisherList = new HashMap<>();
	private Map<String, Double> breedableList = new HashMap<>();
	private String name;

	/**
	 * Constructor to create a new or load an existing job.
	 * 
	 * @param validationHandler
	 * @param jobDao
	 * @param name
	 * @param isNew
	 */
	public JobImpl(JobsystemValidator validationHandler,
			JobDao jobDao, String name, boolean isNew) {
		this.jobDao = jobDao;
		this.validationHandler = validationHandler;
		jobDao.setupSavefile(name);
		if (isNew) {
			setupJobName(name);
		} else {
			loadExistingJob();
		}
	}

	@Override
	public void addFisherLootType(String lootType, double price) throws JobsystemException {
		validationHandler.checkForValidEnum(FishingLootTypeEnum.values(), lootType);
		validationHandler.checkForValueGreaterZero(price);
		validationHandler.checkForValueNotInList(new ArrayList<>(getFisherList().keySet()), lootType);
		getFisherList().put(lootType, price);
		jobDao.saveFisherList(getFisherList());
	}

	@Override
	public void removeFisherLootType(String lootType) throws JobsystemException {
		validationHandler.checkForValidEnum(FishingLootTypeEnum.values(), lootType);
		validationHandler.checkForValueInList(new ArrayList<>(getFisherList().keySet()), lootType);
		getFisherList().remove(lootType);
		jobDao.saveFisherList(getFisherList());
	}

	@Override
	public void addMob(String entity, double price) throws JobsystemException {
		entity = entity.toUpperCase();
		validationHandler.checkForValidEnum(EntityType.values(), entity);
		validationHandler.checkForValueNotInList(new ArrayList<>(getEntityList().keySet()), entity);
		validationHandler.checkForValueGreaterZero(price);
		entityList.put(entity, price);
		jobDao.saveEntityList(getEntityList());
	}

	@Override
	public void deleteMob(String entity) throws JobsystemException {
		entity = entity.toUpperCase();
		validationHandler.checkForValidEnum(EntityType.values(), entity);
		validationHandler.checkForValueInList(new ArrayList<>(getEntityList().keySet()), entity);
		entityList.remove(entity);
		jobDao.saveEntityList(getEntityList());
	}

	@Override
	public void addBreedable(EntityType breedable, double price) throws JobsystemException {
		validationHandler.checkForValueInList(breedableMobs, breedable);
		validationHandler.checkForValueNotInList(new ArrayList<>(getBreedableList().keySet()),
				breedable.toString().toUpperCase());
		validationHandler.checkForValueGreaterZero(price);
		breedableList.put(breedable.toString().toUpperCase(), price);
		jobDao.saveBreedableList(getBreedableList());
	}

	@Override
	public void deleteBreedable(EntityType breedable) throws JobsystemException {
		validationHandler.checkForValueInList(breedableMobs, breedable);
		validationHandler.checkForValueInList(new ArrayList<>(getBreedableList().keySet()),
				breedable.toString().toUpperCase());
		breedableList.remove(breedable.toString().toUpperCase());
		jobDao.saveBreedableList(getBreedableList());
	}

	@Override
	public void addBlock(String material, double price) throws JobsystemException {
		material = material.toUpperCase();
		validationHandler.checkForValidEnum(Material.values(), material);
		validationHandler.checkForValueGreaterZero(price);
		validationHandler.checkForValueNotInList(new ArrayList<>(getBlockList().keySet()), material);
		getBlockList().put(material, price);
		jobDao.saveBlockList(getBlockList());
	}

	@Override
	public void deleteBlock(String material) throws JobsystemException {
		material = material.toUpperCase();
		validationHandler.checkForValidEnum(Material.values(), material);
		validationHandler.checkForValueInList(new ArrayList<>(getBlockList().keySet()), material);
		getBlockList().remove(material);
		jobDao.saveBlockList(getBlockList());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getBreedPrice(EntityType breedable) throws JobsystemException {
		validationHandler.checkForValueInList(breedableMobs, breedable);
		validationHandler.checkForValueInList(new ArrayList<>(getBreedableList().keySet()),
				breedable.toString().toUpperCase());
		return getBreedableList().get(breedable.toString().toUpperCase());
	}

	@Override
	public double getBlockPrice(String material) throws JobsystemException {
		material = material.toUpperCase();
		validationHandler.checkForValidEnum(Material.values(), material);
		validationHandler.checkForValueInList(new ArrayList<>(getBlockList().keySet()), material);
		return getBlockList().get(material);
	}

	@Override
	public double getFisherPrice(String lootType) throws JobsystemException {
		validationHandler.checkForValidEnum(FishingLootTypeEnum.values(), lootType);
		validationHandler.checkForValueInList(new ArrayList<>(getFisherList().keySet()), lootType);
		return getFisherList().get(lootType);
	}

	@Override
	public double getKillPrice(String entityName) throws JobsystemException {
		entityName = entityName.toUpperCase();
		validationHandler.checkForValidEnum(EntityType.values(), entityName);
		validationHandler.checkForValueInList(new ArrayList<>(getEntityList().keySet()), entityName);
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
