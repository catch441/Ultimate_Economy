package com.ue.jobsystem.logic.api;

import java.util.List;

import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

public interface JobManager {

	/**
	 * Returns a list of all available jobs.
	 * 
	 * @return list of jobs
	 */
	public List<Job> getJobList();
	
	/**
	 * Returns a string list of all job names.
	 * 
	 * @return list of job names
	 */
	public List<String> getJobNameList();
	
	/**
	 * This method returns a job by it's name.
	 * 
	 * @param jobName
	 * @return Job
	 * @throws GeneralEconomyException
	 */
	public Job getJobByName(String jobName) throws GeneralEconomyException;
	
	/**
	 * This method deletes a job.
	 * 
	 * @param job
	 * @throws JobSystemException
	 * @throws GeneralEconomyException
	 */
	public void deleteJob(Job job);
	
	/**
	 * This method should be used to create a new Job.
	 * 
	 * @param jobName
	 * @throws GeneralEconomyException
	 */
	public void createJob(String jobName) throws GeneralEconomyException;
	
	/**
	 * This method loads all Jobs from the save files.
	 * 
	 */
	public void loadAllJobs();

	/**
	 * Removes a job from all joined players.
	 * @param job
	 */
	public void removeJobFromAllPlayers(Job job);
}
