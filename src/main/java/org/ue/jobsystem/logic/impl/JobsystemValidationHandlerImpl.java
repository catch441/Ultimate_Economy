package org.ue.jobsystem.logic.impl;

import java.util.List;

import javax.inject.Inject;

import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.impl.EconomyVillagerValidationHandlerImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;

public class JobsystemValidationHandlerImpl extends EconomyVillagerValidationHandlerImpl<JobsystemException>
		implements JobsystemValidationHandler {

	@Inject
	public JobsystemValidationHandlerImpl(ServerProvider serverProvider, MessageWrapper messageWrapper) {
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