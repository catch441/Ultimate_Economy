package com.ue.jobsystem.logic.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsystem.logic.impl.JobSystemException;

public interface Jobcenter {

	/**
	 * Setup a new jobcenter.
	 * 
	 * @param name
	 * @param spawnLocation
	 * @param size
	 */
	public void setupNew(String name, Location spawnLocation, int size);

	/**
	 * Setup an existing shop.
	 * 
	 * @param name
	 */
	public void setupExisting(String name);

	/**
	 * This method adds a job to this jobcenter.
	 * 
	 * @param job
	 * @param itemMaterial
	 * @param slot
	 * @throws JobSystemException
	 * @throws EconomyPlayerException
	 * @throws GeneralEconomyException
	 */
	public void addJob(Job job, String itemMaterial, int slot)
			throws JobSystemException, EconomyPlayerException, GeneralEconomyException;

	/**
	 * This method removes a job from this jobcenter.
	 * 
	 * @param job
	 * @throws JobSystemException
	 */
	public void removeJob(Job job) throws JobSystemException;

	/**
	 * This method moves a jobcenter villager to a other location.
	 * 
	 * @param location
	 */
	public void moveJobcenter(Location location);

	/**
	 * This method returns the name of this jobcenter.
	 * 
	 * @return String
	 */
	public String getName();

	/**
	 * This method despawns the jobcenter villager.
	 */
	public void despawnVillager();

	/**
	 * This method returns a list of all jobs in this jobcenter.
	 * 
	 * @return List of jobs
	 */
	public List<Job> getJobList();

	/**
	 * This method opens the jobcenter inventory.
	 * 
	 * @param player
	 */
	public void openInv(Player player);

	/**
	 * This method returns true if this jobcenter contains this job.
	 * 
	 * @param job
	 * @return boolean
	 */
	public boolean hasJob(Job job);

	/**
	 * Deletes savefile and despawns villager.
	 */
	public void deleteJobcenter();

	/**
	 * Returns the location of the jobcenter villager.
	 * 
	 * @return location
	 */
	public Location getJobcenterLocation();
}
