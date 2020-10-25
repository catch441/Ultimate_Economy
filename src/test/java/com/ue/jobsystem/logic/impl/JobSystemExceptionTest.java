package com.ue.jobsystem.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;

@ExtendWith(MockitoExtension.class)
public class JobSystemExceptionTest {

	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void constructorTest() {
		when(messageWrapper.getErrorString("job_not_exist_in_jobcenter", "param1", 2)).thenReturn("my error message");
		JobSystemException e = new JobSystemException(messageWrapper, JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER,
				"param1", 2);
		
		assertEquals(JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER, e.getKey());
		assertEquals(2, e.getParams().length);
		assertEquals("param1", e.getParams()[0]);
		assertEquals(2, e.getParams()[1]);
		assertEquals("my error message", e.getMessage());
	}
}
