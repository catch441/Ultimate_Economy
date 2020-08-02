package com.ue.jobsystem.logic.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.common.utils.MessageWrapper;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.impl.JobcenterImpl;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobcenterManagerImpl implements JobcenterManager {

	private List<Jobcenter> jobCenterList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final JobManager jobManager;
	private final JobsystemValidationHandler validationHandler;

	/**
	 * Inject constructor.
	 * 
	 * @param validationHandler
	 * @param jobManager
	 * @param messageWrapper
	 */
	@Inject
	public JobcenterManagerImpl(JobsystemValidationHandler validationHandler, JobManager jobManager,
			MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
		this.jobManager = jobManager;
		this.validationHandler = validationHandler;
	}

	@Override
	public Jobcenter getJobcenterByName(String name) throws GeneralEconomyException {
		for (Jobcenter jobcenter : getJobcenterList()) {
			if (jobcenter.getName().equals(name)) {
				return jobcenter;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public List<String> getJobcenterNameList() {
		List<String> jobCenterNames = new ArrayList<>();
		for (Jobcenter jobcenter : getJobcenterList()) {
			jobCenterNames.add(jobcenter.getName());
		}
		return jobCenterNames;
	}

	@Override
	public List<Jobcenter> getJobcenterList() {
		return jobCenterList;
	}

	@Override
	public void deleteJobcenter(Jobcenter jobcenter) throws JobSystemException {
		jobcenter.deleteJobcenter();
		getJobcenterList().remove(jobcenter);
		for (Job job : jobcenter.getJobList()) {
			if (!otherJobcenterHasJob(job)) {
				jobManager.removeJobFromAllPlayers(job);
			}
		}
		saveJobcenterNameList();
	}

	private boolean otherJobcenterHasJob(Job job) throws JobSystemException {
		for (Jobcenter jobCenter2 : getJobcenterList()) {
			if (jobCenter2.hasJob(job)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void createJobcenter(String name, Location spawnLocation, int size)
			throws JobSystemException, GeneralEconomyException {
		validationHandler.checkForJobcenterNameDoesNotExist(getJobcenterNameList(), name);
		validationHandler.checkForValidSize(size);
		getJobcenterList().add(new JobcenterImpl(name, spawnLocation, size));
		saveJobcenterNameList();
	}

	@Override
	public void loadAllJobcenters() {
		for (String jobCenterName : UltimateEconomy.getInstance.getConfig().getStringList("JobCenterNames")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), jobCenterName + "-JobCenter.yml");
			if (file.exists()) {
				getJobcenterList().add(new JobcenterImpl(jobCenterName));
			} else {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the jobcenter " + jobCenterName);
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: No savefile found!");
			}
		}
	}

	@Override
	public void despawnAllVillagers() {
		for (Jobcenter jobcenter : getJobcenterList()) {
			jobcenter.despawnVillager();
		}
	}

	/*
	 * Save methods
	 * 
	 */

	/*
	 * TODO exclude in dao
	 */
	private void saveJobcenterNameList() {
		UltimateEconomy.getInstance.getConfig().set("JobCenterNames", getJobcenterNameList());
		UltimateEconomy.getInstance.saveConfig();
	}
}
