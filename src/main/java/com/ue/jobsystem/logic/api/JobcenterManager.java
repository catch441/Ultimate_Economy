package com.ue.jobsystem.logic.api;

import java.util.List;

import org.bukkit.Location;

import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

public interface JobcenterManager {

	/**
	 * This method returns a jobcenter by it's name.
	 * 
	 * @param name
	 * @return JobCenter
	 * @throws GeneralEconomyException
	 */
	public Jobcenter getJobcenterByName(String name) throws GeneralEconomyException;
	
	/**
	 * This method returns a namelist of all jobcenters.
	 * 
	 * @return List of Strings
	 */
	public List<String> getJobcenterNameList();
	
	/**
	 * This method returns a list of all existing jobcenters.
	 * 
	 * @return List of JobCenters
	 */
	public List<Jobcenter> getJobcenterList();
	
	/**
	 * This method should me used to delete a jobcenter.
	 * 
	 * @param jobcenter
	 * @throws JobSystemException
	 */
	public void deleteJobcenter(Jobcenter jobcenter) throws JobSystemException;
	
	/**
	 * This method should be used to create a new jobcenter.
	 * 
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @throws JobSystemException
	 * @throws GeneralEconomyException
	 */
	public void createJobcenter(String name, Location spawnLocation, int size)
			throws JobSystemException, GeneralEconomyException;
	
	/**
	 * This method loads all jobcenters from the save files. !!!
	 * JobController.loadAllJobs() have to be executed before this method. !!!
	 */
	public void loadAllJobcenters();
	
	/**
	 * This method despawns all jobcenter villager.
	 */
	public void despawnAllVillagers();
}
