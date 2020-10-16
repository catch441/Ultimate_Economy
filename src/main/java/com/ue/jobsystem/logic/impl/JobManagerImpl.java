package com.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;

public class JobManagerImpl implements JobManager {

	private final Logger logger;
	private List<Job> jobList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidationHandler validationHandler;
	private final JobcenterManager jobcenterManager;
	private final ConfigDao configDao;
	private final ServerProvider serverProvider;

	/**
	 * Inject constructor.
	 * 
	 * @param serverProvider
	 * @param configDao
	 * @param jobcenterManager
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param messageWrapper
	 * @param logger
	 */
	@Inject
	public JobManagerImpl(ServerProvider serverProvider, ConfigDao configDao, JobcenterManager jobcenterManager,
			JobsystemValidationHandler validationHandler, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper, Logger logger) {
		this.messageWrapper = messageWrapper;
		this.configDao = configDao;
		this.logger = logger;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.jobcenterManager = jobcenterManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public List<Job> getJobList() {
		return new ArrayList<>(jobList);
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
		jobList.remove(job);
		job.deleteJob();
		configDao.saveJobList(getJobNameList());
	}

	@Override
	public void createJob(String jobName) throws GeneralEconomyException {
		validationHandler.checkForJobNameDoesNotExist(getJobNameList(), jobName);
		JobDao jobDao = serverProvider.getServiceComponent().getJobDao();
		jobList.add(new JobImpl(validationHandler, jobDao, jobName, true));
		configDao.saveJobList(getJobNameList());
	}

	@Override
	public void loadAllJobs() {
		for (String jobName : configDao.loadJobList()) {
			JobDao jobDao = serverProvider.getServiceComponent().getJobDao();
			jobList.add(new JobImpl(validationHandler, jobDao, jobName, false));
		}
	}

	@Override
	public void removeJobFromAllPlayers(Job job) {
		for (EconomyPlayer ecoPlayer : ecoPlayerManager.getAllEconomyPlayers()) {
			if (ecoPlayer.hasJob(job)) {
				try {
					ecoPlayer.leaveJob(job, false);
				} catch (EconomyPlayerException e) {
					logger.warn("[Ultimate_Economy] Failed leave the job " + job.getName());
					logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
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
					logger.warn("[Ultimate_Economy] Failed remove the job " + job.getName());
					logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			}
		}
	}
}
