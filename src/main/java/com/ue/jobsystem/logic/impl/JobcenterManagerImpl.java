package com.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

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
	private final ServerProvider serverProvider;
	private final ConfigDao configDao;
	private final ComponentProvider componentProvider;

	/**
	 * Inject constructor.
	 * 
	 * @param componentProvider
	 * @param configDao
	 * @param jobManager
	 * @param serverProvider
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param messageWrapper
	 */
	@Inject
	public JobcenterManagerImpl(ComponentProvider componentProvider, ConfigDao configDao, Lazy<JobManager> jobManager, ServerProvider serverProvider,
			JobsystemValidationHandler validationHandler, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
		this.jobManager = jobManager;
		this.configDao = configDao;
		this.componentProvider = componentProvider;
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
		configDao.saveJobcenterList(getJobcenterNameList());
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
		JobcenterDao jobcenterDao = componentProvider.getServiceComponent().getJobcenterDao();
		getJobcenterList().add(new JobcenterImpl(jobcenterDao, jobManager.get(), this, ecoPlayerManager, validationHandler,
				serverProvider, name, spawnLocation, size));
		configDao.saveJobcenterList(getJobcenterNameList());
	}

	@Override
	public void loadAllJobcenters() {
		for (String jobCenterName : configDao.loadJobcenterList()) {
			JobcenterDao jobcenterDao = componentProvider.getServiceComponent().getJobcenterDao();
			getJobcenterList().add(new JobcenterImpl(jobcenterDao, jobManager.get(), this, ecoPlayerManager, validationHandler,
					serverProvider, jobCenterName));
		}
	}

	@Override
	public void despawnAllVillagers() {
		for (Jobcenter jobcenter : getJobcenterList()) {
			jobcenter.despawnVillager();
		}
	}
}
