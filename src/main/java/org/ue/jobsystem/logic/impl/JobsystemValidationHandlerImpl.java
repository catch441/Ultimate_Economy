package org.ue.jobsystem.logic.impl;

import java.util.List;

import javax.inject.Inject;

import org.ue.common.logic.impl.EconomyVillagerValidationHandlerImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.jobsystem.logic.JobExceptionMessageEnum;
import org.ue.jobsystem.logic.JobSystemException;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;

public class JobsystemValidationHandlerImpl extends EconomyVillagerValidationHandlerImpl implements JobsystemValidationHandler {
	
	@Inject
	public JobsystemValidationHandlerImpl(MessageWrapper messageWrapper) {
		super(messageWrapper);
	}
	
	@Override
	public void checkForValidFisherLootType(String lootType) throws GeneralEconomyException {
		if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					lootType);
		}
	}

	@Override
	public void checkForJobDoesNotExistInJobcenter(List<Job> jobList, Job job) throws JobSystemException {
		if (jobList.contains(job)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER);
		}
	}

	@Override
	public void checkForJobExistsInJobcenter(List<Job> jobList, Job job) throws JobSystemException {
		if (!jobList.contains(job)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER);
		}
	}
}