package org.ue.jobsystem.logic.impl;

import java.util.List;

import javax.inject.Inject;

import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyvillager.logic.impl.EconomyVillagerValidatorImpl;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidator;

public class JobsystemValidatorImpl extends EconomyVillagerValidatorImpl<JobsystemException>
		implements JobsystemValidator {

	@Inject
	public JobsystemValidatorImpl(ServerProvider serverProvider, MessageWrapper messageWrapper) {
		super(serverProvider, messageWrapper);
	}

	@Override
	protected JobsystemException createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object... params) {
		return new JobsystemException(messageWrapper, key, params);
	}

	@Override
	public void checkForJobDoesNotExistInJobcenter(List<Job> jobList, Job job) throws JobsystemException {
		if (jobList.contains(job)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER);
		}
	}

	@Override
	public void checkForJobExistsInJobcenter(List<Job> jobList, Job job) throws JobsystemException {
		if (!jobList.contains(job)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER);
		}
	}
}