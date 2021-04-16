package org.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.utils.ServerProvider;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.jobsystem.dataaccess.api.JobDao;
import org.ue.jobsystem.logic.JobSystemException;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;

public class JobManagerImpl implements JobManager {

	private static final Logger log = LoggerFactory.getLogger(JobManagerImpl.class);
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidationHandler validationHandler;
	private final JobcenterManager jobcenterManager;
	private final ConfigDao configDao;
	private final ServerProvider serverProvider;
	private Map<String, Job> jobList = new HashMap<>();

	@Inject
	public JobManagerImpl(ServerProvider serverProvider, ConfigDao configDao, JobcenterManager jobcenterManager,
			JobsystemValidationHandler validationHandler, EconomyPlayerManager ecoPlayerManager) {
		this.configDao = configDao;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.jobcenterManager = jobcenterManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public List<Job> getJobList() {
		return new ArrayList<>(jobList.values());
	}

	@Override
	public List<String> getJobNameList() {
		return new ArrayList<>(jobList.keySet());
	}

	@Override
	public Job getJobByName(String jobName) throws GeneralEconomyException {
		Job job = jobList.get(jobName);
		validationHandler.checkForValueExists(job, jobName);
		return job;
	}

	@Override
	public void deleteJob(Job job) {
		removeJobFromAllJobcenters(job);
		removeJobFromAllPlayers(job);
		jobList.remove(job.getName());
		job.deleteJob();
		configDao.saveJobList(getJobNameList());
	}

	@Override
	public void createJob(String jobName) throws GeneralEconomyException {
		validationHandler.checkForValueNotInList(getJobNameList(), jobName);
		JobDao jobDao = serverProvider.getServiceComponent().getJobDao();
		jobList.put(jobName, new JobImpl(validationHandler, jobDao, jobName, true));
		configDao.saveJobList(getJobNameList());
	}

	@Override
	public void loadAllJobs() {
		for (String jobName : configDao.loadJobList()) {
			JobDao jobDao = serverProvider.getServiceComponent().getJobDao();
			jobList.put(jobName, new JobImpl(validationHandler, jobDao, jobName, false));
		}
	}

	@Override
	public void removeJobFromAllPlayers(Job job) {
		for (EconomyPlayer ecoPlayer : ecoPlayerManager.getAllEconomyPlayers()) {
			if (ecoPlayer.hasJob(job)) {
				try {
					ecoPlayer.leaveJob(job, false);
				} catch (EconomyPlayerException e) {
					log.warn("[Ultimate_Economy] Failed leave the job " + job.getName());
					log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			}
		}
	}

	private void removeJobFromAllJobcenters(Job job) {
		for (Jobcenter jobcenter : jobcenterManager.getJobcenterList()) {
			if (jobcenter.hasJob(job)) {
				try {
					jobcenter.removeJob(job);
				} catch (JobSystemException e) {
					log.warn("[Ultimate_Economy] Failed remove the job " + job.getName());
					log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			}
		}
	}
}
