package com.ue.jobsystem.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.ue.exceptions.JobExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.impl.JobImpl;
import com.ue.language.MessageWrapper;
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
	throw JobSystemException.getException(JobExceptionMessageEnum.JOB_DOES_NOT_EXISTS);
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
		jobcenter.removeJob(job);
	    } catch (JobSystemException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
	    }
	}
	for (EconomyPlayer ecoPlayer : EconomyPlayerController.getAllEconomyPlayers()) {
	    if (ecoPlayer.hasJob(job)) {
		try {
		    ecoPlayer.leaveJob(job, false);
		} catch (PlayerException e) {
		    Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
		}
	    }
	}
	jobList.remove(job);
	job.deleteJob();
	UltimateEconomy.getInstance.getConfig().set("JobList", JobController.getJobNameList());
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * This method should be used to create a new Job.
     * 
     * @param jobName
     * @throws JobSystemException
     */
    public static void createJob(String jobName) throws JobSystemException {
	if (getJobNameList().contains(jobName)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.JOB_ALREADY_EXISTS);
	} else {
	    jobList.add(new JobImpl(jobName));
	    UltimateEconomy.getInstance.getConfig().set("JobList", JobController.getJobNameList());
	    UltimateEconomy.getInstance.saveConfig();
	}
    }

    /**
     * This method loads all Jobs from the save files.
     * 
     */
    public static void loadAllJobs() {
	for (String jobName : UltimateEconomy.getInstance.getConfig().getStringList("JobList")) {
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), jobName + "-Job.yml");
	    if (file.exists()) {
		jobList.add(new JobImpl(jobName));
	    } else {
		Bukkit.getLogger()
			.warning("[Ultimate_Economy] " + MessageWrapper.getErrorString("cannot_load_job", jobName));
	    }
	}
    }
}
