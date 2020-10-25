package com.ue.jobsystem.logic.api;

import java.util.Map;

import org.bukkit.entity.EntityType;

import com.ue.general.impl.GeneralEconomyException;

public interface Job {

	/**
	 * Returns the breedable entity list of the job.
	 * 
	 * @return breedableList
	 */
	public Map<String, Double> getBreedableList();

	/**
	 * Returns the price for breeding this entity.
	 * 
	 * @param breedable
	 * @return price
	 * @throws GeneralEconomyException
	 */
	public double getBreedPrice(EntityType breedable) throws GeneralEconomyException;

	/**
	 * Removes a breedable entity from the job.
	 * 
	 * @param breedable
	 * @throws GeneralEconomyException
	 */
	public void deleteBreedable(EntityType breedable) throws GeneralEconomyException;

	/**
	 * Adds a breedable entity to the job.
	 * 
	 * @param breedable
	 * @param price
	 * @throws GeneralEconomyException
	 */
	public void addBreedable(EntityType breedable, double price) throws GeneralEconomyException;

	/**
	 * This method removes a mob from a job.
	 * 
	 * @param entity
	 * @throws GeneralEconomyException
	 */
	public void deleteMob(String entity) throws GeneralEconomyException;

	/**
	 * This method removes a block from a job.
	 * 
	 * @param material
	 * @throws GeneralEconomyException
	 */
	public void deleteBlock(String material) throws GeneralEconomyException;

	/**
	 * This method removes a loottype from this job. The loottype is for a
	 * fisherJob. It should be "treasure", "junk" or "fish".
	 * 
	 * @param lootType
	 * @throws GeneralEconomyException
	 */
	public void removeFisherLootType(String lootType) throws GeneralEconomyException;

	/**
	 * This method adds a loottype with a price to this job. The loottype is for a
	 * fisherJob. It should be "treasure", "junk" or "fish".
	 * 
	 * @param lootType
	 * @param price
	 * @throws GeneralEconomyException
	 */
	public void addFisherLootType(String lootType, double price) throws GeneralEconomyException;

	/**
	 * This method adds a mob to a job.
	 * 
	 * @param entity
	 * @param price
	 * @throws GeneralEconomyException
	 */
	public void addMob(String entity, double price) throws GeneralEconomyException;

	/**
	 * This method adds a block to a job.
	 * 
	 * @param material
	 * @param price
	 * @throws GeneralEconomyException
	 */
	public void addBlock(String material, double price) throws GeneralEconomyException;

	/**
	 * This method deletes the job saveFile.
	 */
	public void deleteJob();

	/**
	 * This method returns the name of this job.
	 * 
	 * @return String
	 */
	public String getName();

	/**
	 * This method returns the price of a block in this job.
	 * 
	 * @param material
	 * @return double
	 * @throws GeneralEconomyException
	 */
	public double getBlockPrice(String material) throws GeneralEconomyException;

	/**
	 * This method returns the price of a fisher lootType.
	 * 
	 * @param lootType
	 * @return double
	 * @throws GeneralEconomyException
	 */
	public double getFisherPrice(String lootType) throws GeneralEconomyException;

	/**
	 * This method returns the price for killing a entity.
	 * 
	 * @param entityName
	 * @return double
	 * @throws GeneralEconomyException
	 */
	public double getKillPrice(String entityName) throws GeneralEconomyException;

	/**
	 * This method returns the fisherList.
	 * 
	 * @return List of Strings
	 */
	public Map<String, Double> getFisherList();

	/**
	 * This method returns the entityList.
	 * 
	 * @return List of Strings
	 */
	public Map<String, Double> getEntityList();

	/**
	 * This method returns the block list.
	 * 
	 * @return List of Strings
	 */
	public Map<String, Double> getBlockList();

}
