package com.ue.jobsystem.logic.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.common.utils.BukkitService;
import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

import dagger.Lazy;

public class JobcenterManagerImpl implements JobcenterManager {

	private List<Jobcenter> jobCenterList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidationHandler validationHandler;
	// lazy because of circulating dependency, cannot resolved with refactoring
	// the object will never be created, thats just fine, because it is only used
	// during load jobcenter jobs and not during runtime
	private final Lazy<JobManager> jobManager;
	private final BukkitService bukkitService;
	
	/**
	 * Inject constructor.
	 * 
	 * @param jobManager
	 * @param bukkitService
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param messageWrapper
	 */
	@Inject
	public JobcenterManagerImpl(Lazy<JobManager> jobManager, BukkitService bukkitService, JobsystemValidationHandler validationHandler,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.bukkitService = bukkitService;
		this.jobManager = jobManager;
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
				removeJobFromAllPlayers(job);
			}
		}
		saveJobcenterNameList();
	}

	private void removeJobFromAllPlayers(Job job) {
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

	private boolean otherJobcenterHasJob(Job job) throws JobSystemException {
		for (Jobcenter jobCenter : getJobcenterList()) {
			if (jobCenter.hasJob(job)) {
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
		getJobcenterList().add(new JobcenterImpl(jobManager.get(), this, ecoPlayerManager, validationHandler, bukkitService,
				name, spawnLocation, size));
		saveJobcenterNameList();
	}

	@Override
	public void loadAllJobcenters() {
		for (String jobCenterName : UltimateEconomy.getInstance.getConfig().getStringList("JobCenterNames")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), jobCenterName + "-JobCenter.yml");
			if (file.exists()) {
				getJobcenterList().add(new JobcenterImpl(jobManager.get(), this, ecoPlayerManager, validationHandler,
						bukkitService, jobCenterName));
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
