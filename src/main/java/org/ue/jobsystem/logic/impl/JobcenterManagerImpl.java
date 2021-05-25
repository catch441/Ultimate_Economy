package org.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.utils.ServerProvider;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidator;

public class JobcenterManagerImpl implements JobcenterManager {

	private static final Logger log = LoggerFactory.getLogger(JobcenterManagerImpl.class);
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidator validationHandler;
	private final ServerProvider serverProvider;
	private final ConfigDao configDao;
	private Map<String, Jobcenter> jobcenterList = new HashMap<>();

	public JobcenterManagerImpl(ConfigDao configDao, ServerProvider serverProvider,
			EconomyPlayerManager ecoPlayerManager, JobsystemValidator validationHandler) {
		this.ecoPlayerManager = ecoPlayerManager;
		this.serverProvider = serverProvider;
		this.configDao = configDao;
		this.validationHandler = validationHandler;
	}

	@Override
	public Jobcenter getJobcenterByName(String name) throws JobsystemException {
		Jobcenter jobcenter = jobcenterList.get(name);
		validationHandler.checkForValueExists(jobcenter, name);
		return jobcenter;
	}

	@Override
	public List<String> getJobcenterNameList() {
		return new ArrayList<>(jobcenterList.keySet());
	}

	@Override
	public List<Jobcenter> getJobcenterList() {
		return new ArrayList<>(jobcenterList.values());
	}

	@Override
	public void deleteJobcenter(Jobcenter jobcenter) throws JobsystemException {
		jobcenter.deleteJobcenter();
		jobcenterList.remove(jobcenter.getName());
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
					log.warn("[Ultimate_Economy] Failed to leave the job " + job.getName());
					log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			}
		}
	}

	private boolean otherJobcenterHasJob(Job job) throws JobsystemException {
		for (Jobcenter jobCenter : jobcenterList.values()) {
			if (jobCenter.hasJob(job)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void createJobcenter(String name, Location spawnLocation, int size) throws JobsystemException {
		validationHandler.checkForValueNotInList(getJobcenterNameList(), name);
		validationHandler.checkForValidSize(size);
		Jobcenter jobcenter = serverProvider.getProvider().createJobcenter();
		jobcenter.setupNew(name, spawnLocation, size);
		jobcenterList.put(name, jobcenter);
		configDao.saveJobcenterList(getJobcenterNameList());
	}

	@Override
	public void loadAllJobcenters() {
		for (String jobcenterName : configDao.loadJobcenterList()) {
			Jobcenter jobcenter = serverProvider.getProvider().createJobcenter();
			jobcenter.setupExisting(jobcenterName);
			jobcenterList.put(jobcenterName, jobcenter);
		}
	}

	@Override
	public void despawnAllVillagers() {
		for (Jobcenter jobcenter : jobcenterList.values()) {
			jobcenter.despawn();
		}
	}
}
