package com.ue.jobsystem.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.JobExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.jobsystem.api.Job;

public class JobSystemValidationHandler {

	/*
	 * EntityTypes that correspond to entities that can be bred by the player.
	 * 
	 * TODO put this list definition somewhere that actually makes sense
	 * */
	private static final ArrayList<EntityType> breedableTypes = new ArrayList<EntityType>(Arrays.asList(EntityType.BEE, EntityType.CAT, EntityType.CHICKEN,
			EntityType.COW, EntityType.MUSHROOM_COW, EntityType.HORSE, EntityType.DONKEY, EntityType.SHEEP,
			EntityType.FOX, EntityType.PIG, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT,
			EntityType.LLAMA, EntityType.TURTLE, EntityType.PANDA));
	// TODO EntityType.HOGLIN, EntityType.STRIDER needed for 1.16
	
	public static ArrayList<EntityType> getBreedableTypes()
	{
		return breedableTypes;
	}
  
	/**
	 * Check for a valid material name.
	 * @param material
	 * @throws GeneralEconomyException
	 */
	public void checkForValidMaterial(String material) throws GeneralEconomyException {
		if (Material.matchMaterial(material) == null) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, material);
		}
	}

	/**
	 * Check for a valid entity name.
	 * @param entityName
	 * @throws GeneralEconomyException
	 */
	public void checkForValidEntityType(String entityName) throws GeneralEconomyException {
		try {
			EntityType.valueOf(entityName);
		} catch (IllegalArgumentException e) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					entityName);
		}
	}

	/**
	 * Check for a positive value.
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForPositivValue(double value) throws GeneralEconomyException {
		if (value <= 0.0) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
		}
	}

	/**
	 * Check for a valid fisher loot type.
	 * @param lootType
	 * @throws GeneralEconomyException
	 */
	public void checkForValidFisherLootType(String lootType) throws GeneralEconomyException {
		if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, lootType);
		}
	}

	/**
	 * Check for block is not in the job.
	 * @param blockList
	 * @param material
	 * @throws JobSystemException
	 */
	public void checkForBlockNotInJob(Map<String, Double> blockList, String material) throws JobSystemException {
		if (blockList.containsKey(material)) {
			throw JobSystemException.getException(JobExceptionMessageEnum.BLOCK_ALREADY_EXISTS);
		}
	}

	/**
	 * Check for block is in the job.
	 * @param blockList
	 * @param material
	 * @throws JobSystemException
	 */
	public void checkForBlockInJob(Map<String, Double> blockList, String material) throws JobSystemException {
		if (!blockList.containsKey(material)) {
			throw JobSystemException.getException(JobExceptionMessageEnum.BLOCK_DOES_NOT_EXIST);
		}
	}

	/**
	 * Check for loot type is not in the job.
	 * @param fisherList
	 * @param lootType
	 * @throws JobSystemException
	 */
	public void checkForLoottypeNotInJob(Map<String, Double> fisherList, String lootType) throws JobSystemException {
		if (fisherList.containsKey(lootType)) {
			throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_ALREADY_EXISTS);
		}
	}

	/**
	 * Check for loot type is in the job.
	 * @param fisherList
	 * @param lootType
	 * @throws JobSystemException
	 */
	public void checkForLoottypeInJob(Map<String, Double> fisherList, String lootType) throws JobSystemException {
		if (!fisherList.containsKey(lootType)) {
			throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_DOES_NOT_EXIST);
		}
	}

	/**
	 * Check for entity is not the the job.
	 * @param entityList
	 * @param entity
	 * @throws JobSystemException
	 */
	public void checkForEntityNotInJob(Map<String, Double> entityList, String entity) throws JobSystemException {
		if (entityList.containsKey(entity)) {
			throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_ALREADY_EXISTS);
		}
	}

	/**
	 * Check for entity is in the job.
	 * @param entityList
	 * @param entityName
	 * @throws JobSystemException
	 */
	public void checkForEntityInJob(Map<String, Double> entityList, String entityName) throws JobSystemException {
		if (!entityList.containsKey(entityName)) {
			throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_DOES_NOT_EXIST);
		}
	}

	/**
	 * Check for a valid slot.
	 * @param slot
	 * @param size
	 * @throws GeneralEconomyException
	 */
	public void checkForValidSlot(int slot, int size) throws GeneralEconomyException {
		// size -1 because of one reserved slot
		if (slot < 0 || slot >= (size-1)) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, slot);
		}
	}

	/**
	 * Check for slot is free.
	 * @param inventory
	 * @param slot
	 * @throws PlayerException
	 */
	public void checkForFreeSlot(Inventory inventory, int slot) throws PlayerException {
		if (!isSlotEmpty(inventory, slot)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
		}
	}

	/**
	 * Check for the job does not exists in the jobcenter.
	 * @param jobList
	 * @param job
	 * @throws JobSystemException
	 */
	public void checkForJobDoesNotExistInJobcenter(List<Job> jobList, Job job) throws JobSystemException {
		if (jobList.contains(job)) {
			throw JobSystemException.getException(JobExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER);
		}
	}

	/**
	 * Check for the job exixts in the jobcenter.
	 * @param jobList
	 * @param job
	 * @throws JobSystemException
	 */
	public void checkForJobExistsInJobcenter(List<Job> jobList, Job job) throws JobSystemException {
		if (!jobList.contains(job)) {
			throw JobSystemException.getException(JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER);
		}
	}

	private boolean isSlotEmpty(Inventory inventory, int slot) {
		if (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {
			return true;
		}
		return false;
	}
    
    protected void checkForEntityCanBreed(Map<String, Double> entityList, String entityName) throws JobSystemException {
    if(entityList.containsKey(entityName)) {
    	if (!getBreedableTypes().contains(EntityType.valueOf(entityName)))
    	{
    		throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_CANNOT_BREED);
    	}
    }
    }
}