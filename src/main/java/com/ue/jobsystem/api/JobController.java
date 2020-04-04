package com.ue.jobsystem.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.impl.JobImpl;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobController {

    private static List<Job> jobList = new ArrayList<>();

    /**
     * Returns a list of all available jobs.
     * 
     * @return list of jobs
     */
    public static List<Job> getJobList() {
	return jobList;
    }

    /**
     * Returns a string list of all job names.
     * 
     * @return list of job names
     */
    public static List<String> getJobNameList() {
	List<String> jobNames = new ArrayList<>();
	for (Job job : getJobList()) {
	    jobNames.add(job.getName());
	}
	return jobNames;
    }

    /**
     * This method returns a job by it's name.
     * 
     * @param jobName
     * @return Job
     * @throws GeneralEconomyException
     */
    public static Job getJobByName(String jobName) throws GeneralEconomyException {
	for (Job job : getJobList()) {
	    if (job.getName().equals(jobName)) {
		return job;
	    }
	}
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, jobName);
    }

    /**
     * This method deletes a job.
     * 
     * @param job
     * @throws JobSystemException
     * @throws GeneralEconomyException
     */
    public static void deleteJob(Job job) {
	removeJobFromAllJobcenters(job);
	removeJobFromAllPlayers(job);
	getJobList().remove(job);
	job.deleteJob();
	saveJobNameList();
    }

    /**
     * This method should be used to create a new Job.
     * 
     * @param jobName
     * @throws GeneralEconomyException
     */
    public static void createJob(String jobName) throws GeneralEconomyException {
	checkForJobNameDoesNotExist(jobName);
	getJobList().add(new JobImpl(jobName));
	saveJobNameList();
    }

    /**
     * This method loads all Jobs from the save files.
     * 
     */
    public static void loadAllJobs() {
	for (String jobName : UltimateEconomy.getInstance.getConfig().getStringList("JobList")) {
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), jobName + "-Job.yml");
	    if (file.exists()) {
		getJobList().add(new JobImpl(jobName));
	    } else {
		Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the job " + jobName);
		Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: No savefile found!");
	    }
	}
    }
    
    /**
     * Remove a job from all economy players.
     * 
     * @param job
     */
    public static void removeJobFromAllPlayers(Job job) {
	for (EconomyPlayer ecoPlayer : EconomyPlayerController.getAllEconomyPlayers()) {
	    if (ecoPlayer.hasJob(job)) {
		try {
		    ecoPlayer.leaveJob(job, false);
		} catch (PlayerException e) {
		    Bukkit.getLogger().warning("[Ultimate_Economy] Failed leave the job " + job.getName());
		    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	    }
	}
    }
    
    /*
     * Utility methods
     * 
     */
    
    private static void removeJobFromAllJobcenters(Job job) {
	for (Jobcenter jobcenter : JobcenterController.getJobCenterList()) {
	    if (jobcenter.hasJob(job)) {
		try {
		    jobcenter.removeJob(job);
		} catch (JobSystemException e) {
		    Bukkit.getLogger().warning("[Ultimate_Economy] Failed remove the job " + job.getName());
		    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	    }
	}
    }
    
    /*
     * Save methods
     * 
     */
    
    private static void saveJobNameList() {
	UltimateEconomy.getInstance.getConfig().set("JobList", JobController.getJobNameList());
	UltimateEconomy.getInstance.saveConfig();
    }

    /*
     * Validation check methods
     * 
     */

    private static void checkForJobNameDoesNotExist(String jobName) throws GeneralEconomyException {
	if (getJobNameList().contains(jobName)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, jobName);
	}
    }
}
