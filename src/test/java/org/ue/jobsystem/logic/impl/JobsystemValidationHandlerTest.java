package org.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobsystemException;

@ExtendWith(MockitoExtension.class)
public class JobsystemValidationHandlerTest {

	@InjectMocks
	JobsystemValidatorImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void checkForJobDoesNotExistInJobcenterTest() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		jobs.add(job);
		JobsystemException exception = assertThrows(JobsystemException.class,
				() -> validationHandler.checkForJobDoesNotExistInJobcenter(jobs, job));
		assertEquals(0, exception.getParams().length);
		assertEquals(ExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER, exception.getKey());
	}

	@Test
	public void checkForJobDoesNotExistInJobcenterTestValid() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> validationHandler.checkForJobDoesNotExistInJobcenter(jobs, job));
	}

	@Test
	public void checkForJobExistInJobcenterTest() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		JobsystemException exception = assertThrows(JobsystemException.class,
				() -> validationHandler.checkForJobExistsInJobcenter(jobs, job));
		assertEquals(0, exception.getParams().length);
		assertEquals(ExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER, exception.getKey());
	}

	@Test
	public void checkForJobExistsInJobcenterTestValid() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		jobs.add(job);
		assertDoesNotThrow(() -> validationHandler.checkForJobExistsInJobcenter(jobs, job));
	}
}
