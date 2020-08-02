package com.ue.jobsyste.dataaccess.api;

import java.util.Map;

public interface JobDao {

	/**
	 * Saves a job name.
	 * @param name
	 */
	public void saveJobName(String name);
	
	/**
	 * Saves the block list (map) for a job.
	 * @param blockList
	 */
	public void saveBlockList(Map<String, Double> blockList);
	
	/**
	 * Saves the fisher list (map) for a job.
	 * @param fisherList
	 */
	public void saveFisherList(Map<String, Double> fisherList);
	
	/**
	 * Saves the entity list (map) for a job.
	 * @param entityList
	 */
	public void saveEntityList(Map<String, Double> entityList);
	
	/**
	 * Loads the job name.
	 * @return job name as String
	 */
	public String loadJobName();
	
	/**
	 * Loads the block list (map) of a job.
	 * @return block list as Map
	 */
	public Map<String, Double> loadBlockList();
	
	/**
	 * Loads the entity list (map) of a job.
	 * @return entity list as Map
	 */
	public Map<String, Double> loadEntityList();
	
	/**
	 * Loads the fisher list (map) of a job.
	 * @return fisher list as Map
	 */
	public Map<String, Double> loadFisherList();
	
	/**
	 * Deletes the savefile.
	 */
	public void deleteSavefile();
}
