package org.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.jobsystem.logic.JobSystemException;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;

public class JobcenterManagerImpl implements JobcenterManager {

	private static final Logger log = LoggerFactory.getLogger(JobcenterManagerImpl.class);
	private List<Jobcenter> jobCenterList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerManager ecoPlayerManager;
	private final GeneralEconomyValidationHandler generalValidator;
	private final ServerProvider serverProvider;
	private final ConfigDao configDao;

	@Inject
	public JobcenterManagerImpl(ConfigDao configDao, ServerProvider serverProvider,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper,
			GeneralEconomyValidationHandler generalValidator) {
		this.messageWrapper = messageWrapper;
		this.ecoPlayerManager = ecoPlayerManager;
		this.serverProvider = serverProvider;
		this.configDao = configDao;
		this.generalValidator = generalValidator;
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
					log.warn("[Ultimate_Economy] Failed to leave the job " + job.getName());
					log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
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
		generalValidator.checkForValueNotInList(getJobcenterNameList(), name);
		generalValidator.checkForValidSize(size);
		Jobcenter jobcenter = serverProvider.getServiceComponent().getJobcenter();
		jobcenter.setupNew(name, spawnLocation, size);
		getJobcenterList().add(jobcenter);
		configDao.saveJobcenterList(getJobcenterNameList());
	}

	@Override
	public void loadAllJobcenters() {
		for (String jobCenterName : configDao.loadJobcenterList()) {
			Jobcenter jobcenter = serverProvider.getServiceComponent().getJobcenter();
			jobcenter.setupExisting(jobCenterName);
			getJobcenterList().add(jobcenter);
		}
	}

	@Override
	public void despawnAllVillagers() {
		for (Jobcenter jobcenter : getJobcenterList()) {
			jobcenter.despawnVillager();
		}
	}
}
