package com.ue.jobsyste.dataaccess.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import com.ue.jobsystem.logic.api.Job;

public interface JobcenterDao {

	/**
	 * Saves the jobcenter size.
	 * 
	 * @param size
	 */
	public void saveJobcenterSize(int size);

	/**
	 * Saves the jobcenter name.
	 * 
	 * @param name
	 */
	public void saveJobcenterName(String name);

	/**
	 * Saves the location of a jobcenter.
	 * 
	 * @param location
	 */
	public void saveJobcenterLocation(Location location);

	/**
	 * Saves a job name list.
	 * 
	 * @param jobNameList
	 */
	public void saveJobNameList(List<String> jobNameList);

	/**
	 * Saves a job inside a jobcenter. If materialname is null, then the job gets
	 * deleted.
	 * 
	 * @param job
	 * @param itemMaterial
	 * @param slot
	 */
	public void saveJob(Job job, String itemMaterial, int slot);

	/**
	 * Deletes the savefile.
	 */
	public void deleteSavefile();

	/**
	 * Loads the jobcenter size.
	 * 
	 * @return size as integer
	 */
	public int loadJobcenterSize();

	/**
	 * Loads the location of a jobcenter.
	 * 
	 * @return location as Location
	 */
	public Location loadJobcenterLocation();

	/**
	 * Loads the slot of a job inside a jobcenter.
	 * 
	 * @param job
	 * @return slot as integer
	 */
	public int loadJobSlot(Job job);

	/**
	 * Load Jobname list.
	 * 
	 * @return jobnames
	 */
	public List<String> loadJobNameList();

	/**
	 * Load the item material that represenst the job in the jobcenter.
	 * 
	 * @param job
	 * @return material name as Material
	 */
	public Material loadJobItemMaterial(Job job);

	/**
	 * Setup a new savefile or loads an existing one.
	 * 
	 * @param name
	 */
	public void setupSavefile(String name);
}
