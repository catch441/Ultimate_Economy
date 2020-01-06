package com.ue.jobsystem.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.impl.JobImpl;
import com.ue.player.EconomyPlayer;

public class JobController {

	private static List<Job> jobList = new ArrayList<>();
	
	public static List<Job> getJobList() {
		return jobList;
	}

	public static List<String> getJobNameList() {
		List<String> jobNames = new ArrayList<>();
		for (Job job : jobList) {
			jobNames.add(job.getName());
		}
		return jobNames;
	}

	/**
	 * This method returns a job by it's name.
	 * 
	 * @param jobName
	 * @return Job
	 * @throws JobSystemException
	 */
	public static Job getJobByName(String jobName) throws JobSystemException {
		for (Job job : jobList) {
			if (job.getName().equals(jobName)) {
				return job;
			}
		}
		throw new JobSystemException(JobSystemException.JOB_DOES_NOT_EXIST);
	}

	/**
	 * This method deletes a job.
	 * 
	 * @param jobName
	 * @throws JobSystemException
	 */
	public static void deleteJob(String jobName) throws JobSystemException {
		Job job = getJobByName(jobName);
		List<Jobcenter> jobCenterList = JobcenterController.getJobCenterList();
		for (Jobcenter jobcenter : jobCenterList) {
			try {
				jobcenter.removeJob(jobName);
			} catch (JobSystemException e) {
				Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
			}
		}
		for (EconomyPlayer ecoPlayer : EconomyPlayer.getAllEconomyPlayers()) {
			if (ecoPlayer.hasJob(jobName)) {
				try {
					ecoPlayer.removeJob(jobName);
				} catch (PlayerException e) {
					Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
		jobList.remove(job);
		job.deleteJob();
	}

	/**
	 * This method should be used to create a new Job.
	 * 
	 * @param dataFolder
	 * @param jobName
	 * @throws JobSystemException
	 */
	public static void createJob(File dataFolder, String jobName) throws JobSystemException {
		if (getJobNameList().contains(jobName)) {
			throw new JobSystemException(JobSystemException.JOB_ALREADY_EXIST);
		} else {
			jobList.add(new JobImpl(dataFolder, jobName));
		}
	}

	/**
	 * This method loads all Jobs from the save files.
	 * 
	 * @param dataFolder
	 * @param fileConfig
	 */
	public static void loadAllJobs(File dataFolder, FileConfiguration fileConfig) {
		for (String jobName : fileConfig.getStringList("JobList")) {
			File file = new File(dataFolder, jobName + "-Job.yml");
			if (file.exists()) {
				jobList.add(new JobImpl(dataFolder, jobName));
			} else {
				Bukkit.getLogger().log(Level.WARNING, JobSystemException.CANNOT_LOAD_JOB,
						new JobSystemException(JobSystemException.CANNOT_LOAD_JOB));
			}

		}
	}
	
}
