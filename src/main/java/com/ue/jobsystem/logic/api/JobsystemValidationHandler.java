package com.ue.jobsystem.logic.api;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsystem.logic.impl.JobSystemException;

public interface JobsystemValidationHandler {

	/**
	 * Check for value does exist in the list.
	 * 
	 * @param list
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForDoesExist(Map<String, Double> list, String value) throws GeneralEconomyException;

	/**
	 * Check for value is not in the list.
	 * 
	 * @param list
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForDoesNotExist(Map<String, Double> list, String value) throws GeneralEconomyException;

	/**
	 * Checks for a valid breedable entity.
	 * 
	 * @param breedable
	 * @throws GeneralEconomyException
	 */
	public void checkForValidBreedableEntity(EntityType breedable) throws GeneralEconomyException;

	/**
	 * Check for a valid material name.
	 * 
	 * @param material
	 * @throws GeneralEconomyException
	 */
	public void checkForValidMaterial(String material) throws GeneralEconomyException;

	/**
	 * Check for a valid entity name.
	 * 
	 * @param entityName
	 * @throws GeneralEconomyException
	 */
	public void checkForValidEntityType(String entityName) throws GeneralEconomyException;

	/**
	 * Check for a positive value.
	 * 
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForPositivValue(double value) throws GeneralEconomyException;

	/**
	 * Check for a valid fisher loot type.
	 * 
	 * @param lootType
	 * @throws GeneralEconomyException
	 */
	public void checkForValidFisherLootType(String lootType) throws GeneralEconomyException;

	/**
	 * Check for a valid slot.
	 * 
	 * @param slot
	 * @param size
	 * @throws GeneralEconomyException
	 */
	public void checkForValidSlot(int slot, int size) throws GeneralEconomyException;

	/**
	 * Check for slot is free.
	 * 
	 * @param inventory
	 * @param slot
	 * @throws EconomyPlayerException
	 */
	public void checkForFreeSlot(Inventory inventory, int slot) throws EconomyPlayerException;

	/**
	 * Check for the job does not exists in the jobcenter.
	 * 
	 * @param jobList
	 * @param job
	 * @throws JobSystemException
	 */
	public void checkForJobDoesNotExistInJobcenter(List<Job> jobList, Job job) throws JobSystemException;

	/**
	 * Check for the job exixts in the jobcenter.
	 * 
	 * @param jobList
	 * @param job
	 * @throws JobSystemException
	 */
	public void checkForJobExistsInJobcenter(List<Job> jobList, Job job) throws JobSystemException;

	/**
	 * Check for jobname does not exist.
	 * 
	 * @param jobList
	 * @param jobName
	 * @throws GeneralEconomyException
	 */
	public void checkForJobNameDoesNotExist(List<String> jobList, String jobName) throws GeneralEconomyException;

	/**
	 * Check for joncenter name does not exist.
	 * 
	 * @param jobcenterList
	 * @param name
	 * @throws GeneralEconomyException
	 */
	public void checkForJobcenterNameDoesNotExist(List<String> jobcenterList, String name)
			throws GeneralEconomyException;

	/**
	 * Check for valid size.
	 * 
	 * @param size
	 * @throws GeneralEconomyException
	 */
	public void checkForValidSize(int size) throws GeneralEconomyException;
}
