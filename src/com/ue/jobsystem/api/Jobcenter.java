package com.ue.jobsystem.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.exceptions.JobSystemException;

public interface Jobcenter {
	
	/**
	 * This method adds a job to this jobcenter.
	 * 
	 * @param jobname
	 * @param itemMaterial
	 * @param slot
	 * @throws JobSystemException
	 */
	public void addJob(String jobname, String itemMaterial, int slot) throws JobSystemException;
	
	/**
	 * This method removes a job from this jobcenter.
	 * 
	 * @param jobname
	 * @throws JobSystemException
	 */
	public void removeJob(String jobname) throws JobSystemException;
	
	/**
	 * This method moves a jobcenter villager to a other location.
	 * 
	 * @param location
	 */
	public void moveJobCenter(Location location);
	
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
	 * This method returns a list of all jobnames in this jobcenter.
	 * 
	 * @return
	 */
	public List<String> getJobNameList();

	/**
	 * This method opens the jobcenter inventory.
	 * 
	 * @param player
	 */
	public void openInv(Player player);
	
	/**
	 * This method returns true if this jobcenter contains this job.
	 * 
	 * @param jobname
	 * @return boolean
	 * @throws JobSystemException
	 */
	public boolean hasJob(String jobname) throws JobSystemException;
	
	/**
	 * Deletes savefile and despawns villager;
	 */
	public void deleteJobCenter();
}
