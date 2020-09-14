package com.ue.jobsystem.logic.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;

import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobManagerImpl implements JobManager {

	private List<Job> jobList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidationHandler validationHandler;
	private final JobcenterManager jobcenterManager;
	private final ComponentProvider componentProvider;

	/**
	 * Inject constructor.
	 * 
	 * @param componentProvider
	 * @param jobcenterManager
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param messageWrapper
	 */
	@Inject
	public JobManagerImpl(ComponentProvider componentProvider, JobcenterManager jobcenterManager,
			JobsystemValidationHandler validationHandler, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.jobcenterManager = jobcenterManager;
		this.componentProvider = componentProvider;
	}

	@Override
	public List<Job> getJobList() {
		return jobList;
	}

	@Override
	public List<String> getJobNameList() {
		List<String> jobNames = new ArrayList<>();
		for (Job job : getJobList()) {
			jobNames.add(job.getName());
		}
		return jobNames;
	}

	@Override
	public Job getJobByName(String jobName) throws GeneralEconomyException {
		for (Job job : getJobList()) {
			if (job.getName().equals(jobName)) {
				return job;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, jobName);
	}

	@Override
	public void deleteJob(Job job) {
		removeJobFromAllJobcenters(job);
		removeJobFromAllPlayers(job);
		getJobList().remove(job);
		job.deleteJob();
		saveJobNameList();
	}

	@Override
	public void createJob(String jobName) throws GeneralEconomyException {
		validationHandler.checkForJobNameDoesNotExist(getJobNameList(), jobName);
		JobDao jobDao = componentProvider.getServiceComponent().getJobDao();
		getJobList().add(new JobImpl(validationHandler, jobDao, jobName, true));
		saveJobNameList();
	}

	@Override
	public void loadAllJobs() {
		for (String jobName : UltimateEconomy.getInstance.getConfig().getStringList("JobList")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), jobName + "-Job.yml");
			if (file.exists()) {
				JobDao jobDao = componentProvider.getServiceComponent().getJobDao();
				getJobList().add(new JobImpl(validationHandler, jobDao, jobName, false));
			} else {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the job " + jobName);
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: No savefile found!");
			}
		}
	}

	@Override
	public void removeJobFromAllPlayers(Job job) {
		for (EconomyPlayer ecoPlayer : ecoPlayerManager.getAllEconomyPlayers()) {
			if (ecoPlayer.hasJob(job)) {
				try {
					ecoPlayer.leaveJob(job, false);
				} catch (EconomyPlayerException e) {
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

	private void removeJobFromAllJobcenters(Job job) {
		for (Jobcenter jobcenter : jobcenterManager.getJobcenterList()) {
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

	/*
	 * TODO exclude in dao
	 */
	private void saveJobNameList() {
		UltimateEconomy.getInstance.getConfig().set("JobList", getJobNameList());
		UltimateEconomy.getInstance.saveConfig();
	}
}
